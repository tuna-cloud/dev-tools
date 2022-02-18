package com.tuna.tools.plugin;

import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;

public interface UiContext {

    TreeView treeView();

    TabPane tabPane();
}
