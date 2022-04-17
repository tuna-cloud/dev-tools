package com.tuna.tools.fiddler;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.google.common.collect.Sets;
import com.tuna.commons.utils.SystemUtils;
import com.tuna.tools.common.VertxInstance;
import com.tuna.tools.fiddler.ext.DynamicJksOptions;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.http.DefaultFullHttpRequest;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpContentDecompressor;
import io.netty.handler.codec.http.HttpServerCodec;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.function.Consumer;

public class ProxyRequestInterceptor {
    private static final Logger logger = LogManager.getLogger(ProxyRequestInterceptor.class);

    private final DynamicJksOptions jksOptions;
    private ProxyConfig config;

    private Map<String, EmbeddedChannel> reqChannelMap = Maps.newConcurrentMap();
    private Map<String, EmbeddedChannel> rspChannelMap = Maps.newConcurrentMap();

    private String filter;
    private List<Log> allLogList = Lists.newArrayList();
    private Queue<Log> queue = Queues.newConcurrentLinkedQueue();
    private Set<String> whiteSet = Sets.newHashSet();
    private Consumer<Log> consumer;

    private NetServer netServer;
    private NetClient client;

    public ProxyRequestInterceptor(Consumer<Log> consumer) {
        this.consumer = consumer;
        jksOptions = new DynamicJksOptions();
        jksOptions.setPath(com.tuna.tools.common.SystemUtils.getBaseDir() + File.separator + "data");
        jksOptions.setRootCert("root.cert");
        jksOptions.setRootKey("root.key");
    }

    private void addLog(Log log) {
        allLogList.add(log);
    }

    public void updateFilter(String filter) {
        this.filter = filter;
    }

    private boolean isApplyFilter(Log log) {
        if (log.getHost().contains(filter)) {
            return true;
        }
        if (log.getUri().contains(filter)) {
            return true;
        }
        if (log.getMethod().contains(filter)) {
            return true;
        }
        return false;
    }

    public void close(Handler<AsyncResult<Void>> completionHandler) {
        allLogList.clear();
        queue.clear();
        whiteSet.clear();
        reqChannelMap.clear();
        rspChannelMap.clear();
        client.close().compose(r -> netServer.close()).onComplete(completionHandler);
    }

    public void clear() {
        allLogList.clear();
    }

    public Log getLog(long id) {
        for (Log log : allLogList) {
            if (log.getId() == id) {
                return log;
            }
        }
        return null;
    }

    public void start(ProxyConfig config, Handler<AsyncResult<Void>> completionHandler) {
        this.config = config;
        whiteSet.clear();
        if (StringUtils.isNotEmpty(config.getByPassUrls())) {
            String[] lines = config.getByPassUrls().split("\n");
            for (String line : lines) {
                whiteSet.add(line);
            }
        }

        NetClientOptions clientOptions = new NetClientOptions();
        clientOptions.setReuseAddress(false);
        clientOptions.setReusePort(false);
        clientOptions.setTrustAll(true);
        client = VertxInstance.getInstance().createNetClient(clientOptions);

        NetServerOptions options = new NetServerOptions();
        options.setPort(config.getPort());
        options.setSsl(false);
        options.setSni(true);
        options.setKeyStoreOptions(jksOptions);
        netServer = VertxInstance.getInstance().createNetServer(options);

        netServer.connectHandler(netSocket -> netSocket.handler(message -> {
            if (!config.isRemoteConnection()) {
                if (!netSocket.remoteAddress().toString().contains("127.0.0.1")
                        || SystemUtils.getLocalIpAddress().contains(netSocket.remoteAddress().toString())) {
                    netSocket.close();
                    return;
                }
            }
            String text = message.toString();
            if (text.startsWith("CONNECT")) {
                String[] lines = text.split("\n");
                String[] hostPort = lines[0].split(" ")[1].split(":");
                Future<NetSocket> proxyFuture = startNetClient(hostPort[0], Integer.parseInt(hostPort[1]));
                proxyFuture.onComplete(proxyResult -> {
                    if (proxyResult.succeeded()) {
                        Future<Void> writeFuture = netSocket.write("HTTP/1.0 200 Connection established\n\n");
                        writeFuture.onComplete(writeResult -> {
                            if (writeFuture.succeeded()) {
                                try {
                                    if (config.isHttps() && hostPort[1].equals("443")) {
                                        netSocket.upgradeToSsl(sslResult -> {
                                            if (sslResult.succeeded()) {
                                                beginProxy(hostPort[0], hostPort[1], netSocket, proxyResult.result());
                                            } else {
                                                netSocket.close();
                                            }
                                        });
                                    } else {
                                        beginProxy(hostPort[0], hostPort[1], netSocket, proxyResult.result());
                                    }
                                } catch (Exception e) {
                                    logger.error("upgrade err", e);
                                    netSocket.close();
                                }
                            } else {
                                logger.error("chrome write connection err", writeFuture.cause());
                                netSocket.close();
                            }
                        });
                    } else {
                        logger.error("connect to {} failed. err: {}", lines[0], proxyResult.cause());
                        netSocket.close();
                    }
                });
            }
        }));

        netServer.listen(res -> {
            if (res.succeeded()) {
                logger.info("Net server bind at {} success", res.result().actualPort());
                completionHandler.handle(Future.succeededFuture());
                VertxInstance.getInstance().setPeriodic(1000, id -> mergeHttpRequest());
            } else {
                logger.error("Net server bind failed", res.cause());
                completionHandler.handle(Future.failedFuture(res.cause()));
            }
        });
    }

    protected void mergeHttpRequest() {
        while (!queue.isEmpty()) {
            Log log = queue.poll();

            if (log.getHttpObject() == null) {
                Log lastLog = findLastLog(log);
                if (lastLog != null) {
                    lastLog.setReqStopTime(log.getReqStopTime());
                } else {
                    logger.info("reqStopTime match failed");
                }
            } else {
                // new request
                if (log.getHttpObject() instanceof DefaultFullHttpRequest) {
                    DefaultFullHttpRequest request = (DefaultFullHttpRequest) log.getHttpObject();
                    log.initId();
                    log.setUri(request.uri());
                    log.setMethod(request.method().name());
                    log.setProtocol(request.protocolVersion().text());
                    Iterator<Map.Entry<String, String>> iterator = request.headers().iteratorAsString();
                    while (iterator.hasNext()) {
                        Map.Entry<String, String> entry = iterator.next();
                        log.getReqHeaders().put(entry.getKey(), entry.getValue());
                    }
                    log.setReqHeaderSize(request.headers().size());
                    log.writeRequestBody(request.content());
                    addLog(log);
                } else if (log.getHttpObject() instanceof DefaultHttpRequest) {
                    DefaultHttpRequest request = (DefaultHttpRequest) log.getHttpObject();
                    log.initId();
                    log.setUri(request.uri());
                    log.setMethod(request.method().name());
                    log.setProtocol(request.protocolVersion().text());
                    Iterator<Map.Entry<String, String>> iterator = request.headers().iteratorAsString();
                    while (iterator.hasNext()) {
                        Map.Entry<String, String> entry = iterator.next();
                        log.getReqHeaders().put(entry.getKey(), entry.getValue());
                    }
                    log.setReqHeaderSize(request.headers().size());
                    addLog(log);
                } else if (log.getHttpObject() instanceof HttpContent) {
                    HttpContent content = (HttpContent) log.getHttpObject();
                    Log lastLog = findLastLog(log);
                    if (lastLog != null) {
                        if (lastLog.getRspHeaderSize() == 0) { // merge to http request body
                            lastLog.writeRequestBody(content.content());
                        } else {
                            lastLog.writeResponseBody(content.content());
                            log.setRspStopTime(log.getRspStopTime());
                        }
                    } else {
                        logger.info("http content match failed");
                    }
                } else if (log.getHttpObject() instanceof DefaultFullHttpResponse) {
                    DefaultFullHttpResponse response = (DefaultFullHttpResponse) log.getHttpObject();
                    Log lastLog = findLastLog(log);
                    if (lastLog != null) {
                        lastLog.setStatus(response.status().code());
                        lastLog.setRspHeaderSize(response.headers().size());
                        Iterator<Map.Entry<String, String>> iterator = response.headers().iteratorAsString();
                        while (iterator.hasNext()) {
                            Map.Entry<String, String> entry = iterator.next();
                            lastLog.getRspHeaders().put(entry.getKey(), entry.getValue());
                        }
                        lastLog.setRspStopTime(log.getRspStopTime());
                        lastLog.writeResponseBody(response.content());
                        logger.info(log.getHttpObject().getClass().getName());
                    } else {
                        logger.info("http response match failed");
                    }
                } else if (log.getHttpObject() instanceof DefaultHttpResponse) {
                    DefaultHttpResponse response = (DefaultHttpResponse) log.getHttpObject();
                    Log lastLog = findLastLog(log);
                    if (lastLog != null) {
                        lastLog.setStatus(response.status().code());
                        lastLog.setRspHeaderSize(response.headers().size());
                        Iterator<Map.Entry<String, String>> iterator = response.headers().iteratorAsString();
                        while (iterator.hasNext()) {
                            Map.Entry<String, String> entry = iterator.next();
                            lastLog.getRspHeaders().put(entry.getKey(), entry.getValue());
                        }
                        lastLog.setRspStopTime(log.getRspStopTime());
                    } else {
                        logger.info("http response match failed");
                    }
                } else {
                    logger.warn("un hand object: {}", log.getHttpObject().getClass().getName());
                }
            }
        }

        Iterator<Log> iterator = allLogList.iterator();
        while (iterator.hasNext()) {
            Log log = iterator.next();
            if (log.getStatus() != 0) {
                if (log.getPushTime() < log.getRspStopTime()) {
                    consumer.accept(log);
                    log.setPushTime(log.getRspStopTime());
                }
            }
        }
    }

    protected Log findLastLog(Log key) {
        int i = allLogList.size() - 1;
        while (i >= 0) {
            Log log = allLogList.get(i--);
            if (log.getHost().equals(key.getHost()) && log.getClientIp().equals(key.getClientIp())) {
                return log;
            }
        }
        return null;
    }

    public void beginProxy(String host, String port, NetSocket clientSocket, NetSocket proxySocket) {
        if (!whiteSet.isEmpty()) {
            boolean isIn = false;
            for (String key : whiteSet) {
                if (host.contains(key)) {
                    isIn = true;
                    break;
                }
            }
            if (!isIn) {
                clientSocket.pipeTo(proxySocket);
                proxySocket.pipeTo(clientSocket);
                return;
            }
        }
        String remoteIpAddr = proxySocket.remoteAddress().toString();
        String localAddress = proxySocket.localAddress().toString();

        clientSocket.handler(buf -> {
            EmbeddedChannel channel = getReqChannel(localAddress, port, host, remoteIpAddr);
            buf.getByteBuf().markReaderIndex();
            channel.writeInbound(buf.getByteBuf());
            buf.getByteBuf().resetReaderIndex();

            proxySocket.write(buf, proxyWriteFuture -> {
                Log record = new Log();
                record.setTs(System.currentTimeMillis());
                record.setHost(host);
                record.setPort(Integer.parseInt(port));
                record.setReqStopTime(System.currentTimeMillis());
                record.setClientIp(proxySocket.localAddress().toString());
                record.setRemoteIp(proxySocket.remoteAddress().toString());
                queue.add(record);
            });
        });

        proxySocket.handler(buf -> {
            EmbeddedChannel channel = getRspChannel(localAddress, port, host, remoteIpAddr);
            buf.getByteBuf().markReaderIndex();
            channel.writeInbound(buf.getByteBuf());
            buf.getByteBuf().resetReaderIndex();

            clientSocket.write(buf);
        });

        clientSocket.closeHandler(v -> {
            reqChannelMap.remove(localAddress);
            rspChannelMap.remove(localAddress);
            proxySocket.close();
        });
        proxySocket.closeHandler(v -> {
            reqChannelMap.remove(localAddress);
            rspChannelMap.remove(localAddress);
            proxySocket.close();
        });
    }

    public EmbeddedChannel getReqChannel(String localIp, String port, String host, String remoteIp) {
        EmbeddedChannel channel = reqChannelMap.get(localIp);
        if (channel == null) {
            channel = new EmbeddedChannel();
            channel.pipeline().addLast("codec", new HttpServerCodec());
//            channel.pipeline().addLast("inflater", new HttpContentDecompressor(false));
            channel.pipeline().addLast("hander", new ChannelInboundHandler() {
                @Override
                public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

                }

                @Override
                public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

                }

                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {

                }

                @Override
                public void channelInactive(ChannelHandlerContext ctx) throws Exception {

                }

                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    Log record = new Log();
                    record.setTs(System.currentTimeMillis());
                    record.setHost(host);
                    record.setPort(Integer.parseInt(port));
                    record.setReqStartTime(System.currentTimeMillis());
                    record.setClientIp(localIp);
                    record.setRemoteIp(remoteIp);
                    record.setHttpObject(msg);
                    queue.add(record);
                }

                @Override
                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

                }

                @Override
                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

                }

                @Override
                public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

                }

                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

                }

                @Override
                public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

                }

                @Override
                public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

                }
            });
            reqChannelMap.put(localIp, channel);
        }
        return channel;
    }

    public EmbeddedChannel getRspChannel(String localIp, String port, String host, String remoteIp) {
        EmbeddedChannel channel = rspChannelMap.get(localIp);
        if (channel == null) {
            channel = new EmbeddedChannel();
            channel.pipeline().addLast("codec", new HttpClientCodec());
            channel.pipeline().addLast("inflater", new HttpContentDecompressor(false));
            channel.pipeline().addLast("hander", new ChannelInboundHandler() {
                @Override
                public void channelRegistered(ChannelHandlerContext ctx) throws Exception {

                }

                @Override
                public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {

                }

                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {

                }

                @Override
                public void channelInactive(ChannelHandlerContext ctx) throws Exception {

                }

                @Override
                public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
                    Log record = new Log();
                    record.setTs(System.currentTimeMillis());
                    record.setHost(host);
                    record.setPort(Integer.parseInt(port));
                    record.setRspStopTime(System.currentTimeMillis());
                    record.setClientIp(localIp);
                    record.setRemoteIp(remoteIp);
                    record.setHttpObject(msg);
                    queue.add(record);
                }

                @Override
                public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {

                }

                @Override
                public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

                }

                @Override
                public void channelWritabilityChanged(ChannelHandlerContext ctx) throws Exception {

                }

                @Override
                public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

                }

                @Override
                public void handlerAdded(ChannelHandlerContext ctx) throws Exception {

                }

                @Override
                public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {

                }
            });
            rspChannelMap.put(localIp, channel);
        }
        return channel;
    }

    private Future<NetSocket> startNetClient(String host, int port) {
        Promise<NetSocket> promise = Promise.promise();

        client.connect(port, host, connectResult -> {
            if (connectResult.succeeded()) {
                if (!config.isHttps()) {
                    promise.complete(connectResult.result());
                    return;
                }
                connectResult.result().upgradeToSsl(r -> {
                    if (r.succeeded()) {
                        try {
                            List<Certificate> certificates = connectResult.result().peerCertificates();
                            if (certificates != null) {
                                Certificate crt = certificates.get(0);
                                if (crt instanceof X509Certificate) {
                                    Set<String> subNames = Sets.newHashSet();
                                    String cn = null;
                                    X509Certificate certificate = (X509Certificate) crt;
                                    Collection<List<?>> names = certificate.getSubjectAlternativeNames();
                                    if (names != null) {
                                        for (List<?> values : names) {
                                            if (values.size() >= 2) {
                                                subNames.add(values.get(1).toString());
                                            }
                                        }
                                        cn = certificate.getSubjectX500Principal().getName();
                                        try {
                                            if (cn.contains("CN=")) {
                                                cn = cn.substring(cn.indexOf("CN=") + 3);
                                            }
                                            if (cn.contains(",")) {
                                                cn = cn.substring(0, cn.indexOf(","));
                                            }
                                        } catch (Exception e) {
                                            logger.error("CN spit err: {}", cn);
                                            throw e;
                                        }
                                        subNames.add(cn);
                                    }
                                    jksOptions.getHelper(VertxInstance.getInstance())
                                            .createCertIfNotExist(cn, subNames, null);
                                }
                            }
                            promise.complete(connectResult.result());
                        } catch (Exception e) {
                            logger.error("generate certificate", e);
                            promise.fail(e);
                        }
                    } else {
                        promise.fail(r.cause());
                    }
                });
            } else {
                promise.fail(connectResult.cause());
            }
        });
        return promise.future();
    }
}
