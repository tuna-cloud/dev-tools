package com.tuna.tools.fiddler;

import com.tuna.commons.utils.JacksonUtils;
import com.tuna.tools.plugin.Resource;
import com.tuna.tools.plugin.ToolPlugin;
import com.tuna.tools.plugin.UiContext;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;

import java.util.Base64;
import java.util.List;

public class FiddlerTool implements ToolPlugin {
    private UiContext ctx;

    @Override
    public Resource root() {
        return new Resource("wifi-lock-open", "抓包工具", "/plugin/fiddler/index.html");
    }

    @Override
    public List<Resource> children() {
        return null;
    }

    @Override
    public void init(UiContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void eventHandler(String name, String data) {
        JsonObject jsonObject = new JsonObject(data);
        System.out.println((jsonObject.toString()));
        if (jsonObject.getString("action").equals("boot")) {
            if (jsonObject.getBoolean("data")) {
                VertxInstance.getInstance().setPeriodic(1000, id -> {
                    Log log = new Log();
                    log.setTs(System.currentTimeMillis());
                    log.setProtocol("HTTPV1.0");
                    log.setMethod("GET");
                    log.setUri("/a/b/c");
                    log.setHost("www.baidu.com");
                    log.setStatus(200);
                    Platform.runLater(() -> {
                        ctx.executeScript("appendLog", log);
                    });
                });
            } else {
                VertxInstance.close();
            }
        }
    }
}
