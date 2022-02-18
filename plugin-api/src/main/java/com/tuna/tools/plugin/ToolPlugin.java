package com.tuna.tools.plugin;

import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

import java.util.List;

public interface ToolPlugin {

    String name();

    TreeItem rootItem();

    void init(UiContext ctx);

    void onTreeItemDoubleClick(List<String> path);

    void onTreeItemClick(List<String> path);

    ContextMenu onContextMenuRequested(List<String> path);
}
