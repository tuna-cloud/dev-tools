package com.tuna.tools.fiddler;

import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.*;
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
import java.util.ResourceBundle;

public class FiddlerController implements Initializable {
    private static final Logger logger = LogManager.getLogger(FiddlerController.class);

    @FXML
    private TextField bindPortTextField;
    private Vertx vertx;
    private HttpClient httpClient;
    private Future<NetSocket> proxySocketFuture;

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

                if(!serverRequest.host().contains("bing")) {
                    return;
                }
                Future<NetSocket> httpNetSocketFuture = serverRequest.toNetSocket().compose(socket -> socket.upgradeToSsl().map(socket));

                httpNetSocketFuture.onComplete(cp -> {
                    if (cp.succeeded()) {
                        logger.info("proxy success");
                        proxySocketFuture.result().handler(httpNetSocketFuture.result()::write);
                        httpNetSocketFuture.result().handler(proxySocketFuture.result()::write);
                    } else {
                        logger.error("proxy failed", cp.cause());
                    }
                });
            } else {
                logger.info("url: {}, method: {}, headers: {}", serverRequest.absoluteURI(), serverRequest.method().name(), serverRequest.headers().toString());
                messageHandler(serverRequest);
            }
        });

        HttpClientOptions httpClientOptions = new HttpClientOptions();
        httpClientOptions.setTrustAll(true);
        httpClientOptions.setSsl(true);
        httpClient = vertx.createHttpClient(httpClientOptions);

        httpServer.listen(res -> {
            if (res.succeeded()) {
                proxySocketFuture = startNetClient();
                logger.info("http server bind at {} success", res.result().actualPort());
            } else {
                logger.error("http server bind failed", res.cause());
            }
        });
    }

    /**
     * 如果想直接做代理，则直接TCP建立链接后，升级为SSL加密通道，则变成了https的代理
     *
     * @return
     */
    private Future<NetSocket> startNetClient() {
        Promise<NetSocket> promise = Promise.promise();

        NetClientOptions clientOptions = new NetClientOptions();
        clientOptions.setTrustAll(true);
        NetClient client = vertx.createNetClient(clientOptions);
        client.connect(Integer.parseInt(bindPortTextField.getText()), "127.0.0.1", connectResult -> {
            if (connectResult.succeeded()) {
                logger.info("proxy client connect to http server success");
                promise.complete(connectResult.result());
            } else {
                logger.error("proxy client connect to http server failed", connectResult.cause());
                promise.fail(connectResult.cause());
            }
        });
        return promise.future();
    }

    private void messageHandler(HttpServerRequest proxyRequest) {
//        request.response().end("{\"msg\":1111}");
        RequestOptions options = new RequestOptions().setAbsoluteURI(proxyRequest.absoluteURI().replace("http", "https")).setMethod(proxyRequest.method())
                .setSsl(true).setPort(443).setHost(proxyRequest.host());
        httpClient.request(options, result -> {
            if (result.succeeded()) {
                HttpClientRequest clientRequest = result.result();
                clientRequest.headers().addAll(proxyRequest.headers());
                if (proxyRequest.method() != HttpMethod.GET) {
                    proxyRequest.body(bodyResult -> {
                        if (bodyResult.succeeded()) {
                            clientRequest.send(bodyResult.result(), rspResult -> {
                                if (rspResult.succeeded()) {
                                    proxyResponseHandler(proxyRequest, rspResult.result());
                                } else {
                                    responseError(proxyRequest, rspResult.cause());
                                }
                            });
                        } else {
                            clientRequest.send(rspResult -> {
                                if (rspResult.succeeded()) {
                                    proxyResponseHandler(proxyRequest, rspResult.result());
                                } else {
                                    responseError(proxyRequest, rspResult.cause());
                                }
                            });
                        }
                    });
                } else {
                    clientRequest.send(rspResult -> {
                        if (rspResult.succeeded()) {
                            proxyResponseHandler(proxyRequest, rspResult.result());
                        } else {
                            responseError(proxyRequest, rspResult.cause());
                        }
                    });
                }
            } else {
                responseError(proxyRequest, result.cause());
            }
        });
    }

    private void responseError(HttpServerRequest proxyRequest, Throwable error) {
        JsonObject rsp = new JsonObject();
        rsp.put("code", -1);
        rsp.put("message", error.getMessage());
        proxyRequest.response().end(rsp.toBuffer());
        logger.error("Proxy request failed", error.getCause());
    }

    private void proxyResponseHandler(HttpServerRequest proxyRequest, HttpClientResponse clientResponse) {
        HttpServerResponse serverResponse = proxyRequest.response();
        serverResponse.headers().addAll(clientResponse.headers());
        serverResponse.setStatusCode(clientResponse.statusCode());
        clientResponse.bodyHandler(buf -> {
            logger.info("clientResponse: {}", buf.toString());
            serverResponse.end(buf);
        });
    }
}
