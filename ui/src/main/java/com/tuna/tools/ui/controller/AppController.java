package com.tuna.tools.ui.controller;

import com.google.common.collect.Maps;
import com.sun.javafx.webkit.WebConsoleListener;
import com.tuna.commons.utils.JacksonUtils;
import com.tuna.tools.plugin.Resource;
import com.tuna.tools.plugin.ToolPlugin;
import com.tuna.tools.plugin.UiContext;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.KeyCode;
import javafx.scene.web.WebView;
import netscape.javascript.JSObject;
import org.apache.commons.lang3.SystemUtils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.ServiceLoader;

public class AppController implements Initializable, UiContext {

    private Map<String, ToolPlugin> pluginMap = Maps.newHashMap();

    @FXML
    private WebView webView;
    private UiEventHandler uiEventHandler;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ServiceLoader<ToolPlugin> loader = ServiceLoader.load(ToolPlugin.class);
        for (ToolPlugin plugin : loader) {
            plugin.init(this);
            pluginMap.put(plugin.root().getName(), plugin);
        }

        uiEventHandler = new UiEventHandler(pluginMap);
        webView.setContextMenuEnabled(false);

        String baseDir = "file:" + SystemUtils.getUserDir().getAbsolutePath() + File.separator + "html";
        webView.getEngine().load(baseDir + File.separator + "index.html");
        WebConsoleListener.setDefaultListener((webView, message, lineNumber, sourceId) -> {
            System.out.println(message + "[at " + lineNumber + "][" + sourceId + "]");
        });
        webView.setOnKeyReleased(event -> {
            if (event.getCode() == KeyCode.F5) {
                webView.getEngine().reload();
            }
        });
        webView.getEngine().getLoadWorker().stateProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                if (newValue != Worker.State.SUCCEEDED) {
                    return;
                }

                JSObject window = (JSObject) webView.getEngine().executeScript("window");
                window.setMember("eventHandler", uiEventHandler);

                JsonArray jsonArray = new JsonArray();
                for (Map.Entry<String, ToolPlugin> entry : pluginMap.entrySet()) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.put("name", entry.getKey());
                    jsonObject.put("icon", entry.getValue().root().getIcon());
                    jsonObject.put("path", baseDir + entry.getValue().root().getPath());
                    JsonArray children = new JsonArray();
                    if (entry.getValue().children() != null) {
                        for (int i = 0; i < entry.getValue().children().size(); i++) {
                            JsonObject child = new JsonObject();
                            Resource obj = entry.getValue().children().get(i);
                            child.put("name", obj.getName());
                            child.put("icon", obj.getIcon());
                            child.put("path", baseDir + obj.getPath());
                            children.add(child);
                        }
                    }
                    jsonObject.put("children", children);
                    jsonArray.add(jsonObject);
                }
                webView.getEngine().executeScript("initMenu('" + jsonArray + "')");
                webView.widthProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                                        Number newValue) {
                        webView.getEngine().executeScript("initWidth('" + (newValue.doubleValue() - 260) + "')");
                    }
                });
                webView.heightProperty().addListener(new ChangeListener<Number>() {
                    @Override
                    public void changed(ObservableValue<? extends Number> observable, Number oldValue,
                                        Number newValue) {
                        webView.getEngine().executeScript("initHeight('" + newValue.doubleValue() + "')");
                    }
                });
                Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
                double width = screenSize.getWidth() * 0.8;
                double height = screenSize.getHeight() * 0.8;
                if (width < webView.getWidth()) {
                    width = webView.getWidth();
                }
                if (height < webView.getHeight()) {
                    height = webView.getHeight() + 28;
                }
                webView.getEngine().executeScript("initWidth('" + (width - 259) + "')");
                webView.getEngine().executeScript("initHeight('" + (height - 28) + "')");
            }
        });
    }

    @Override
    public Object executeScript(String function, Object args) {
        String fullFun = "doFunction('" + function;
        if (args == null) {
            fullFun += "')";
        } else {
            fullFun += "','";
            if (args instanceof String) {
                fullFun += Base64.getEncoder().encodeToString(((String) args).getBytes(StandardCharsets.UTF_8));
            } else {
                fullFun += Base64.getEncoder().encodeToString(JacksonUtils.serialize2buf(args));
            }
            fullFun += "')";
        }
        return webView.getEngine().executeScript(fullFun);
    }
}
