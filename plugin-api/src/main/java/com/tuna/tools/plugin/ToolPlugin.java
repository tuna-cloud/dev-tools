package com.tuna.tools.plugin;

import java.util.List;

public interface ToolPlugin {

    Resource root();

    List<Resource> children();

    void init(UiContext ctx);

    void eventHandler(String name, String data);
}
