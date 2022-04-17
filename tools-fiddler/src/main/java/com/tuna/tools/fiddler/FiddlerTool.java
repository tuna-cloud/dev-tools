package com.tuna.tools.fiddler;

import com.tuna.commons.utils.JacksonUtils;
import com.tuna.tools.common.SystemUtils;
import com.tuna.tools.plugin.Resource;
import com.tuna.tools.plugin.ToolPlugin;
import com.tuna.tools.plugin.UiContext;
import io.vertx.core.json.JsonObject;
import javafx.application.Platform;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FiddlerTool implements ToolPlugin {
    private static final Logger logger = LogManager.getLogger();
    private static final String KEY = "fiddler";

    private UiContext ctx;
    private ProxyRequestInterceptor interceptor;
    private HttpMockClient mockClient;

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
        if (jsonObject.getString("action").equals("boot")) {
            start(jsonObject.getBoolean("data"));
        } else if (jsonObject.getString("action").equals("getConfig")) {
            ctx.executeScript("initConfig", getConfig());
        } else if (jsonObject.getString("action").equals("getLog")) {
            Log log = interceptor.getLog(jsonObject.getLong("data"));
            if (log != null) {
                ctx.executeScript("setLogResult", log);
            }
        } else if (jsonObject.getString("action").equals("clear")) {
            interceptor.clear();
        } else if (jsonObject.getString("action").equals("saveConfig")) {
            try {
                String dir = SystemUtils.getPluginDataDir(KEY);
                FileUtils.forceMkdir(new File(dir));
                File file = new File(dir + File.separator + "config.json");
                FileUtils.writeStringToFile(file, jsonObject.getJsonObject("data").toString(), "UTF-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (jsonObject.getString("action").equals("mockRequest")) {
            mockClient.mock(jsonObject.getJsonObject("data")).onComplete(result -> {
                Platform.runLater(() -> {
                    if (result.succeeded()) {
                        ctx.executeScript("mockResponse", result.result());
                    } else {
                        ctx.executeScript("mockResponse", result.cause().getLocalizedMessage());
                    }
                });
            });
        }
    }

    private void start(boolean startOrStop) {
        if (startOrStop) {
            interceptor = new ProxyRequestInterceptor(log -> {
                Platform.runLater(() -> {
                    ctx.executeScript("appendLog", log);
                });
            });
            interceptor.start(getConfig(), result -> {
            });
            mockClient = new HttpMockClient();
        } else {
            mockClient.close();
            interceptor.close(r -> {
                if (r.succeeded()) {
                    logger.info("Proxy Server Closed");
                } else {
                    logger.error("Proxy server closed failed", r.cause());
                }
            });
        }
    }

    private ProxyConfig getConfig() {
        String dir = SystemUtils.getPluginDataDir(KEY);
        File file = new File(dir + File.separator + "config.json");
        if (file.exists()) {
            String config = null;
            try {
                config = FileUtils.readFileToString(new File(dir + File.separator + "config.json"), "UTF-8");
                return JacksonUtils.deserialize(config, ProxyConfig.class);
            } catch (IOException e) {
                logger.error("FileUtils.readFileToString", e);
            }
            return null;
        } else {
            ProxyConfig config = new ProxyConfig();
            if (PortFactory.isAvailable(8090)) {
                config.setPort(8090);
            } else {
                config.setPort(PortFactory.findFreePort());
            }
            config.setHttps(true);
            config.setHttp2(true);
            config.setRemoteConnection(true);
            config.setSetupSystemProxy(false);
            return config;
        }
    }
}
