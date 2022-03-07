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
import io.vertx.core.net.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.Arrays;
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

        NetServerOptions options = new NetServerOptions();
        if (StringUtils.isNotEmpty(bindPortTextField.getText())) {
            options.setPort(Integer.parseInt(bindPortTextField.getText()));
        }
        options.setSsl(false);
        NetServer netServer = vertx.createNetServer(options);

        netServer.connectHandler(netSocket -> {
            netSocket.handler(message -> {
                String text = message.toString();
                if (text.startsWith("CONNECT")) {
                    String[] lines = text.split("\n");
                    String[] hostPort = lines[0].split(" ")[1].split(":");
                    Future<NetSocket> proxyFuture = startNetClient(hostPort[0], Integer.parseInt(hostPort[1]));
                    proxyFuture.onComplete(proxyResult -> {
                        if (proxyResult.succeeded()) {
                            netSocket.write("HTTP/1.0 200 Connection established\n\n");
                            netSocket.handler(proxyResult.result()::write);
                            proxyResult.result().handler(netSocket::write);
                        }
                    });
                }
            });
        });

        netServer.listen(res -> {
            if (res.succeeded()) {
                logger.info("Net server bind at {} success", res.result().actualPort());
            } else {
                logger.error("Net server bind failed", res.cause());
            }
        });
    }

    private Future<NetSocket> startNetClient(String host, int port) {
        Promise<NetSocket> promise = Promise.promise();

        NetClientOptions clientOptions = new NetClientOptions();
        clientOptions.setTrustAll(true);
        NetClient client = vertx.createNetClient(clientOptions);
        client.connect(port, host, connectResult -> {
            if (connectResult.succeeded()) {
                promise.complete(connectResult.result());
            } else {
                promise.fail(connectResult.cause());
            }
        });
        return promise.future();
    }
}
