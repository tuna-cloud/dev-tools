package com.tuna.tools.fiddler;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;

public class FiddlerController implements Initializable {
    private static final Logger logger = LogManager.getLogger(FiddlerController.class);

    @FXML
    private TextField bindPortTextField;

    private ProxyRequestInterceptor interceptor;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (PortFactory.isAvailable(8090)) {
            bindPortTextField.setText("8090");
        } else {
            bindPortTextField.setText(Integer.toString(PortFactory.findFreePort()));
        }
        interceptor = new ProxyRequestInterceptor();
    }

    public void onWebServerStartClick(MouseEvent event) {
        bindPortTextField.getScene().getWindow().setOnCloseRequest(close -> interceptor.close(r -> logger.info("interceptor closed")));
        int port = Integer.parseInt(bindPortTextField.getText());
        interceptor.start(port);
    }

}
