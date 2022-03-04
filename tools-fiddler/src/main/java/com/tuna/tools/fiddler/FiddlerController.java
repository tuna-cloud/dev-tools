package com.tuna.tools.fiddler;

import com.google.common.collect.Maps;
import com.google.common.collect.Queues;
import com.tuna.commons.utils.JacksonUtils;
import io.netty.handler.codec.http.HttpRequest;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.*;
import io.vertx.core.http.impl.CookieImpl;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.JksOptions;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetClientOptions;
import io.vertx.core.net.NetSocket;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Map;
import java.util.Queue;
import java.util.ResourceBundle;

public class FiddlerController implements Initializable {
    private static final Logger logger = LogManager.getLogger(FiddlerController.class);

    @FXML
    private TextField bindPortTextField;

    private Queue<Buffer> queue = Queues.newConcurrentLinkedQueue();

    private Vertx vertx;
    private Map<String, NetSocket> proxySocketMap = Maps.newConcurrentMap();
    private Map<String, NetSocket> httpSocketMap = Maps.newConcurrentMap();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        vertx = Vertx.vertx();
        bindPortTextField.setText("8090");
    }

    public void onWebServerStartClick(MouseEvent event) {

        bindPortTextField.getScene().getWindow().setOnCloseRequest(close -> {
            vertx.close(r -> {
                logger.info("vertx close finish");
            });
        });

        HttpServerOptions options = new HttpServerOptions();
        if (StringUtils.isNotEmpty(bindPortTextField.getText())) {
            options.setPort(Integer.parseInt(bindPortTextField.getText()));
        }
        options.setSsl(false);
        options.setKeyStoreOptions(new JksOptions().setPath("/Users/xuyang/IdeaProjects/tuna-dev-tools/tools-fiddler/src/main/resources/tuna.jks").setPassword("tuna.tools"));
        HttpServer httpServer = vertx.createHttpServer(options);

        httpServer.requestHandler(serverRequest -> {
            if (serverRequest.method() == HttpMethod.CONNECT) {
                String[] hostPort = serverRequest.headers().get("Host").split(":");
                if (hostPort[0].contains("google")) {
                    return;
                }

                Future<NetSocket> proxySocketFuture = startSSLNetClient(hostPort[0], hostPort.length > 1 ? Integer.parseInt(hostPort[1]) : 443);
                proxySocketFuture.onComplete(r -> {
                    if (r.succeeded()) {
                        proxySocketMap.put(serverRequest.host(), r.result());
                        r.result().handler(buf -> {
                            logger.info("response: \n{}", buf.toString());
                            if (httpSocketMap.containsKey(serverRequest.host())) {
                                httpSocketMap.get(serverRequest.host()).write(buf);
                            }
                        });
                    } else {
                        logger.error("connect to {} failed", serverRequest.host());
                    }
                });
                Future<NetSocket> httpNetSocketFuture = serverRequest.toNetSocket().compose(socket -> socket.upgradeToSsl().map(socket));
                httpNetSocketFuture.onComplete(r -> {
                    if (r.succeeded()) {
                        httpSocketMap.put(serverRequest.host(), r.result());
                        r.result().handler(buf -> {
                            logger.info("request: \n{}", buf.toString());
                            if (proxySocketMap.containsKey(serverRequest.host())) {
                                proxySocketMap.get(serverRequest.host()).write(buf);
                            }
                        });
                    } else {
                        logger.error("proxy failed", r.cause());
                    }
                });
            } else {
                logger.info("url: {}, method: {}, headers: {}", serverRequest.absoluteURI(), serverRequest.method().name(), serverRequest.headers().toString());
            }
        });

        httpServer.listen(res -> {
            if (res.succeeded()) {
                logger.info("http server bind at {} success", res.result().actualPort());
            } else {
                logger.error("http server bind failed", res.cause());
            }
        });
    }

    private Future<NetSocket> startSSLNetClient(String host, int port) {
        Promise<NetSocket> promise = Promise.promise();

        NetClientOptions clientOptions = new NetClientOptions();
        clientOptions.setTrustAll(true);
        NetClient client = vertx.createNetClient(clientOptions);
        client.connect(port, host, connectResult -> {
            if (connectResult.succeeded()) {
                connectResult.result().upgradeToSsl().onComplete( v -> {
                    if (v.succeeded()) {
                        logger.info("ssl connection to {}:{} success", host, port);
                        promise.complete(connectResult.result());
                    } else {
                        promise.fail(v.cause());
                    }
                });
//                promise.complete(connectResult.result());

            } else {
                promise.fail(connectResult.cause());
            }
        });
        return promise.future();
    }
}
