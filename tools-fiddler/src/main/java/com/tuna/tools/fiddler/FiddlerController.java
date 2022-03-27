package com.tuna.tools.fiddler;

import com.tuna.commons.utils.JacksonUtils;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

public class FiddlerController implements Initializable {
    private static final Logger logger = LogManager.getLogger(FiddlerController.class);

    @FXML
    private Spinner bindPort;
    @FXML
    private VBox mainViewVBox;
    @FXML
    private TableView mainViewTable;
    @FXML
    private GridPane settingsGidPane;
    @FXML
    private AnchorPane settingsGridParent;

    private ProxyRequestInterceptor interceptor;

    @FXML
    private TableColumn colSeq;
    @FXML
    private TableColumn colUrl;
    @FXML
    private TableColumn colHttpVersion;
    @FXML
    private TableColumn colResult;
    @FXML
    private TableColumn colMethod;
    @FXML
    private TableColumn colTime;
    @FXML
    private TableColumn colRemoteIp;
    @FXML
    private TableColumn colBodySize;
    @FXML
    private TableColumn colHost;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        if (PortFactory.isAvailable(8090)) {
            bindPort.getEditor().setText("8090");
        } else {
            bindPort.getEditor().setText(Integer.toString(PortFactory.findFreePort()));
        }
        interceptor = new ProxyRequestInterceptor();
        mainViewVBox.heightProperty().addListener(
                (observable, oldValue, newValue) -> mainViewTable.setPrefHeight(newValue.doubleValue() - 42D));
        settingsGridParent.widthProperty().addListener((observable, oldValue, newValue) -> {
            settingsGidPane.setPrefWidth(newValue.doubleValue());
        });
        bindPort.setValueFactory(new SpinnerValueFactory() {
            @Override
            public void decrement(int steps) {
                bindPort.getEditor().setText(Integer.toString(Integer.parseInt(bindPort.getEditor().getText()) - steps));
            }

            @Override
            public void increment(int steps) {
                bindPort.getEditor().setText(Integer.toString(Integer.parseInt(bindPort.getEditor().getText()) + steps));
            }
        });

        colSeq.setCellValueFactory(new PropertyValueFactory<>("time"));
        colUrl.setCellValueFactory(new PropertyValueFactory<>("uri"));
        colHttpVersion.setCellValueFactory(new PropertyValueFactory<>("protocol"));
        colMethod.setCellValueFactory(new PropertyValueFactory<>("method"));
        colResult.setCellValueFactory(new PropertyValueFactory<>("status"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("consummation"));
        colRemoteIp.setCellValueFactory(new PropertyValueFactory<>("remoteIp"));
        colBodySize.setCellValueFactory(new PropertyValueFactory<>("bodySize"));
        colHost.setCellValueFactory(new PropertyValueFactory<>("host"));
        mainViewTable.setItems(interceptor.getRequestLogList());
        mainViewTable.setOnMouseClicked(event -> {
            int idx = mainViewTable.getSelectionModel().getSelectedIndex();
            logger.info("select\n: {}", JacksonUtils.serializePretty(interceptor.getRequestLogList().get(idx)));
        });
        mainViewTable.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            colSeq.setPrefWidth(90.0);
            colUrl.setPrefWidth(newValue.doubleValue() / 3.0);
            colHost.setPrefWidth(newValue.doubleValue() / 5.0);
            colRemoteIp.setPrefWidth(newValue.doubleValue() / 6.0);
        });
    }

    public void onWebServerStartClick(MouseEvent event) {
        bindPort.getScene().getWindow().setOnCloseRequest(close -> interceptor.close(r -> logger.info("interceptor closed")));
        if (interceptor.isRunning()) {
            interceptor.close(r -> {
//                Platform.runLater(() -> {
//                });
                logger.info("Net server closed");
            });
        } else {
            int port = Integer.parseInt(bindPort.getEditor().getText());
            interceptor.start(port, r -> {
            });
        }
    }

    public void onClearAllRecords(MouseEvent event) {
        interceptor.getRequestLogList().clear();
    }
}
