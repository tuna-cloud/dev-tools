package com.tuna.tools.ui.controller;

import com.google.common.collect.Maps;
import com.tuna.tools.plugin.Resource;
import com.tuna.tools.plugin.ToolPlugin;

import java.util.Base64;
import java.util.Map;

public class UiEventHandler {
    private final Map<String, ToolPlugin> eventHandler = Maps.newHashMap();

    public UiEventHandler(Map<String, ToolPlugin> pluginMap) {
        pluginMap.forEach((k, v) -> {
            eventHandler.put(k, v);
            if (v.children() != null && v.children().size() > 0) {
                for (Resource child : v.children()) {
                    eventHandler.put(child.getName(), v);
                }
            }
        });
    }

    public void emit(String name, String data) {
        if (eventHandler.containsKey(name)) {
            eventHandler.get(name).eventHandler(name, new String(Base64.getDecoder().decode(data)));
        } else {
            System.out.println("undefined, == " + name + ":" + data);
        }
    }
}
