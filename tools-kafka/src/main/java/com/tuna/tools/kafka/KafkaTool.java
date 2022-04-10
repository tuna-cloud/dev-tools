package com.tuna.tools.kafka;

import com.tuna.tools.plugin.Resource;
import com.tuna.tools.plugin.ToolPlugin;
import com.tuna.tools.plugin.UiContext;

import java.util.List;

public class KafkaTool implements ToolPlugin {
    private UiContext ctx;

    @Override
    public Resource root() {
        return new Resource("connection", "Kafka", "/plugin/kafka/index.html");
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
    public void eventHandler(String parent, String child) {
    }

}
