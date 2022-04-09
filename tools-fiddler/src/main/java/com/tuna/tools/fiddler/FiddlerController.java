package com.tuna.tools.fiddler;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.net.URL;
import java.util.ResourceBundle;

public class FiddlerController implements Initializable {
    private static final Logger logger = LogManager.getLogger(FiddlerController.class);

    @FXML
    private Spinner bindPort;
    @FXML
    private CheckBox https;
    @FXML
    private CheckBox systemProxySet;
    @FXML
    private CheckBox remoteEnable;
    @FXML
    private CheckBox http2Enable;
    @FXML
    private TextArea byPass;
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

    @FXML
    private TextArea overView;
    @FXML
    private TextField keyWord;
    private boolean isRunning = false;

    public static Log selectedLog;

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
                bindPort.getEditor()
                        .setText(Integer.toString(Integer.parseInt(bindPort.getEditor().getText()) - steps));
            }

            @Override
            public void increment(int steps) {
                bindPort.getEditor()
                        .setText(Integer.toString(Integer.parseInt(bindPort.getEditor().getText()) + steps));
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
            selectedLog = null;
            int idx = mainViewTable.getSelectionModel().getSelectedIndex();
            if (idx >= 0 && idx < interceptor.getRequestLogList().size()) {
                overView.setText(interceptor.getRequestLogList().get(idx).toString());
                selectedLog = interceptor.getRequestLogList().get(idx);
            }
            if (event.getClickCount() == 2) {
                onHttpRequest(null);
            }
        });
        mainViewTable.widthProperty().addListener((observableValue, oldValue, newValue) -> {
            colSeq.setPrefWidth(90.0);
            colUrl.setPrefWidth(newValue.doubleValue() / 3.0);
            colHost.setPrefWidth(newValue.doubleValue() / 5.0);
            colRemoteIp.setPrefWidth(newValue.doubleValue() / 6.0);
        });
    }

    public void onHttpRequest(MouseEvent event) {
        Window parent = mainViewTable.getScene().getWindow();
        final Popup popup = new Popup();
        try {
            Parent root = FXMLLoader.load(FiddlerTool.class.getResource("request.fxml"));
            popup.setHideOnEscape(true);
            popup.getContent().add(root);

            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            double width = screenSize.getWidth() * 0.17;
            double height = screenSize.getHeight() * 0.17;

            popup.show(parent, width, height);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onWebServerStartClick(MouseEvent event) {
        bindPort.getScene().getWindow()
                .setOnCloseRequest(close -> interceptor.close(r -> logger.info("interceptor closed")));
        if (isRunning) {
            isRunning = false;
            interceptor.close(r -> {
                logger.info("Net server closed");
            });
        } else {
            interceptor.start(parseConfig(), r -> {
                isRunning = true;
            });
        }
    }

    public void onClearAllRecords(MouseEvent event) {
        interceptor.clearAllLog();
    }

    public void onSearch(MouseEvent event) {
        String keyWordStr = keyWord.getText();
        if (StringUtils.isNotEmpty(keyWordStr)) {
            interceptor.updateFilter(log -> {
                if (log.getHost().contains(keyWordStr)) {
                    return true;
                }
                if (log.getUri().contains(keyWordStr)) {
                    return true;
                }
                if (log.getMethod().contains(keyWordStr)) {
                    return true;
                }
                return false;
            });
        } else {
            interceptor.updateFilter(null);
        }
    }

    public ProxyConfig parseConfig() {
        ProxyConfig config = new ProxyConfig();
        config.setPort(Integer.parseInt(bindPort.getEditor().getText()));
        config.setHttps(https.isSelected());
        config.setSetupSystemProxy(systemProxySet.isSelected());
        config.setRemoteConnection(remoteEnable.isSelected());
        config.setHttp2(http2Enable.isSelected());
        config.setByPassUrls(byPass.getText());
        return config;
    }
}
