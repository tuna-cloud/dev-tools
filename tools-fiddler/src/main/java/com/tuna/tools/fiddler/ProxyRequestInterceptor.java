package com.tuna.tools.fiddler;

import com.google.common.collect.Sets;
import com.tuna.tools.fiddler.ext.DynamicJksOptions;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetServer;
import io.vertx.core.net.NetServerOptions;
import io.vertx.core.net.NetSocket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.security.cert.Certificate;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ProxyRequestInterceptor {
    private static final Logger logger = LogManager.getLogger(ProxyRequestInterceptor.class);

    private Vertx vertx;
    private final DynamicJksOptions jksOptions;

    public ProxyRequestInterceptor() {
        jksOptions = new DynamicJksOptions();
        jksOptions.setPath(System.getProperty("user.dir") + File.separator + "data");
        jksOptions.setRootCert("root.cert");
        jksOptions.setRootKey("root.key");
    }

    public void start(int port) {
        vertx = Vertx.vertx();
        NetServerOptions options = new NetServerOptions();
        options.setPort(port);
        options.setSsl(false);
        options.setSni(true);
        options.setKeyStoreOptions(jksOptions);
        options.addEnabledSecureTransportProtocol("TLSv1");
        options.addEnabledSecureTransportProtocol("TLSv1.1");
        options.addEnabledSecureTransportProtocol("TLSv1.2");
        NetServer netServer = vertx.createNetServer(options);

        netServer.connectHandler(netSocket -> netSocket.handler(message -> {
            String text = message.toString();
            if (text.startsWith("CONNECT")) {
                String[] lines = text.split("\n");
                String[] hostPort = lines[0].split(" ")[1].split(":");
                Future<NetSocket> proxyFuture = startNetClient(hostPort[0], Integer.parseInt(hostPort[1]));
                proxyFuture.onComplete(proxyResult -> {
                    if (proxyResult.succeeded()) {
                        logger.info("connect to {}:{} success", hostPort[0], hostPort[1]);
                        Future<Void> writeFuture = netSocket.write("HTTP/1.0 200 Connection established\n\n");
                        writeFuture.onComplete(writeResult -> {
                            if (writeFuture.succeeded()) {
                                try {
                                    netSocket.upgradeToSsl(sslResult -> {
                                        if (sslResult.succeeded()) {
                                            netSocket.handler(buf -> {
//                                                    logger.info("chrome: \n{}", buf.toString());
                                                proxyResult.result().write(buf);
                                            });
                                            proxyFuture.result().handler(buf -> {
//                                                    logger.info("server: \n{}", buf.toString());
                                                netSocket.write(buf);
                                            });
                                        } else {
                                            logger.error("chrome upgrade ssl failed, host: " + hostPort[0],
                                                    sslResult.cause());
                                            netSocket.close();
                                        }
                                    });
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
            } else {
                logger.error("Net server bind failed", res.cause());
            }
        });

    }

    public void close(Handler<AsyncResult<Void>> completionHandler) {
        vertx.close(completionHandler);
    }

    private Future<NetSocket> startNetClient(String host, int port) {
        Promise<NetSocket> promise = Promise.promise();

        NetClientOptions clientOptions = new NetClientOptions();
        clientOptions.setTrustAll(true);
        NetClient client = vertx.createNetClient(clientOptions);
        client.connect(port, host, connectResult -> {
            if (connectResult.succeeded()) {
                connectResult.result().upgradeToSsl(r -> {
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
                                        logger.error("cn spit err: {}", cn);
                                        throw e;
                                    }
                                    subNames.add(cn);
                                }
                                jksOptions.getHelper(vertx).createCertIfNotExist(cn, subNames, null);
                            }
                        }
                    } catch (Exception e) {
                        logger.error("connect to " + host, e);
                    }
                    if (r.succeeded()) {
                        promise.complete(connectResult.result());
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
