package com.tuna.desktop.kafka.controller;

import io.vertx.core.json.JsonArray;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class BaseController {

    protected JsonArray loadConfig(String fxml) throws IOException {
        String confPath = SystemUtils.getUserDir() + File.separator + "conf" + File.separator + fxml;
        File file = new File(confPath);
        if (!file.exists()) {
            return new JsonArray();
        }

        String jsonStr = FileUtils.readFileToString(new File(confPath), Charset.defaultCharset());
        if (StringUtils.isEmpty(jsonStr)) {
            return new JsonArray();
        }
        return new JsonArray(jsonStr);
    }
}
