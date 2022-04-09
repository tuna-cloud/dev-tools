package com.tuna.tools.fiddler;

import com.tuna.commons.utils.JacksonUtils;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpMethod;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;

public class RequestController implements Initializable {

    @FXML
    private AnchorPane root;
    @FXML
    private TextField host;
    @FXML
    private TextField uri;
    @FXML
    private ComboBox method;
    @FXML
    private TextArea header;
    @FXML
    private TextArea body;
    @FXML
    private TextArea response;
    private HttpClient httpClient;
    private ObservableList<String> methods = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        methods.add("GET");
        methods.add("POST");
        methods.add("PUT");
        methods.add("PATCH");
        methods.add("DELETE");
        methods.add("HEAD");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth() * 0.66;
        double height = screenSize.getHeight() * 0.66;
        root.setPrefWidth(width);
        root.setPrefHeight(height);

        Log log = FiddlerController.selectedLog;
        HttpClientOptions options = new HttpClientOptions();
        if (log != null) {
            host.setText(log.getHost());
            uri.setText(log.getUri());
            if (log.getMethod().equalsIgnoreCase("get")) {
                method.setValue("GET");
            } else if (log.getMethod().equalsIgnoreCase("post")) {
                method.setValue("POST");
            } else if (log.getMethod().equalsIgnoreCase("put")) {
                method.setValue("PUT");
            } else if (log.getMethod().equalsIgnoreCase("PATCH")) {
                method.setValue("PATCH");
            } else if (log.getMethod().equalsIgnoreCase("DELETE")) {
                method.setValue("DELETE");
            } else if (log.getMethod().equalsIgnoreCase("HEAD")) {
                method.setValue("HEAD");
            }
            header.setText(JacksonUtils.serializePretty(log.getReqHeaders()));
            body.setText(log.getRequestBody());
            if (log.getRemoteIp().contains("443")) {
                options.setSsl(true);
                options.setTrustAll(true);
            }
        }
        method.setItems(methods);

        httpClient = VertxInstance.getInstance().createHttpClient(options);
    }

    public void onSendHttpRequest(MouseEvent event) {
        String[] hostPort;
        if (host.getText().contains(":")) {
            hostPort = host.getText().split(":");
        } else {
            hostPort = new String[] {host.getText(), "443"};
        }
        String md = method.getValue().toString();
        HttpMethod httpMethod;
        if (md.equalsIgnoreCase("GET")) {
            httpMethod = HttpMethod.GET;
        } else if (md.equalsIgnoreCase("POST")) {
            httpMethod = HttpMethod.POST;
        } else if (md.equalsIgnoreCase("PUT")) {
            httpMethod = HttpMethod.PUT;
        } else if (md.equalsIgnoreCase("PATCH")) {
            httpMethod = HttpMethod.PATCH;
        } else if (md.equalsIgnoreCase("DELETE")) {
            httpMethod = HttpMethod.DELETE;
        } else if (md.equalsIgnoreCase("HEAD")) {
            httpMethod = HttpMethod.HEAD;
        } else {
            return;
        }

        httpClient.request(httpMethod, Integer.parseInt(hostPort[1]), hostPort[0], uri.getText())
                .onSuccess(request -> {
                    request.response().onSuccess(response -> {
                        response.body().onSuccess(buf -> {
                            showResponse(buf.toString());
                        });
                    });

                    Map<String, String> headers = JacksonUtils.deserialize(header.getText(), Map.class);
                    headers.forEach((k, v) -> {
                        request.putHeader(k, v);
                    });
                    request.putHeader("Content-Length", body.getText().length() + "");
                    request.write(body.getText());
                    // Make sure the request is ended when you're done with it
                    request.end();
                });
    }

    public void showResponse(String rsp) {
        Platform.runLater(() -> {
            response.setText(rsp);
        });
    }
}
