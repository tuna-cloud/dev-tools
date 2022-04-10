package com.tuna.tools.utils;

import com.google.common.collect.Maps;
import com.tuna.commons.utils.JacksonUtils;
import com.tuna.tools.plugin.Resource;
import com.tuna.tools.plugin.ToolPlugin;
import com.tuna.tools.plugin.UiContext;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

public class CommonTool implements ToolPlugin {

    private UiContext ctx;

    private Map<String, List<String>> openedTabMap = Maps.newHashMap();

    @Override
    public Resource root() {
        return new Resource("human-greeting", "常用", "/plugin/common/index.html");
    }

    @Override
    public List<Resource> children() {
        List<Resource> list = new ArrayList<>();
        list.add(new Resource(null, "Json格式化", "/plugin/common/json.html"));
        list.add(new Resource(null, "时间戳转换", "/plugin/common/time.html"));
        return list;
    }

    @Override
    public void init(UiContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void eventHandler(String name, String data) {
        if (name.equals("Json格式化")) {
            try {
                Object result = praseJson(new String(Base64.getDecoder().decode(data.getBytes(StandardCharsets.UTF_8))));
                if (result != null) {
                    String msg = Base64.getEncoder()
                            .encodeToString(JacksonUtils.serializePretty(result).getBytes(StandardCharsets.UTF_8));
                    ctx.executeScript("window.document.getElementById('frame').contentWindow.showResult('" + msg + "')");
                }
            } catch (Exception e) {
                String msg =
                        Base64.getEncoder().encodeToString(e.getLocalizedMessage().getBytes(StandardCharsets.UTF_8));
                ctx.executeScript("window.document.getElementById('frame').contentWindow.showResult('" + msg + "')");
            }
        }
    }

    private Object praseJson(String json) {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        if (json.charAt(0) == '{') {
            return new JsonObject(json);
        }
        if (json.charAt(0) == '[') {
            return new JsonArray(json);
        }
        return null;
    }
}
