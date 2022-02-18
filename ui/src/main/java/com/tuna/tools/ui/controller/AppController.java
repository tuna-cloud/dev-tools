package com.tuna.tools.ui.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tuna.tools.plugin.ToolPlugin;
import com.tuna.tools.plugin.UiContext;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.AnchorPane;
import javafx.util.Pair;

import java.net.URL;
import java.util.*;

public class AppController implements Initializable, UiContext {

    @FXML
    private TreeView rootTreeView;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private AnchorPane contentAnchorPane;

    private ResourceBundle bundle = ResourceBundle.getBundle("app", new Locale("zh"));

    private Map<String, ToolPlugin> pluginMap = Maps.newHashMap();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ServiceLoader<ToolPlugin> loader = ServiceLoader.load(ToolPlugin.class);
        for (ToolPlugin plugin : loader) {
            plugin.init(this);
            pluginMap.put(plugin.name(), plugin);
        }

        AnchorPane.setTopAnchor(contentAnchorPane, 0.0);
        AnchorPane.setBottomAnchor(contentAnchorPane, 0.0);
        AnchorPane.setLeftAnchor(contentAnchorPane, 0.0);
        AnchorPane.setRightAnchor(contentAnchorPane, 0.0);

        TreeItem<String> rootItem = new TreeItem<>(bundle.getString("treeViewRootItem"));
        for (ToolPlugin plugin : pluginMap.values()) {
            rootItem.getChildren().add(plugin.rootItem());
        }
        rootItem.setExpanded(true);
        rootTreeView.setRoot(rootItem);

        rootTreeView.setOnMouseClicked(mouseEvent -> {
            TreeItem treeItem = (TreeItem) rootTreeView.getSelectionModel().getSelectedItem();
            Pair<String, List<String>> sel = getPluginKey(treeItem);

            if (sel == null) {
                return;
            }

            if (mouseEvent.getClickCount() == 2) {
                pluginMap.get(sel.getKey()).onTreeItemDoubleClick(sel.getValue());
            } else {
                if (mouseEvent.getButton() == MouseButton.PRIMARY) {
                    pluginMap.get(sel.getKey()).onTreeItemClick(sel.getValue());
                } else if (mouseEvent.getButton() == MouseButton.SECONDARY) {
                    System.out.println("Node SECONDARY click: ");
                } else {
                    System.out.println("Node click: " + mouseEvent.getButton().name());
                }
            }
        });
        rootTreeView.setOnContextMenuRequested(event -> {
            if (!event.getTarget().getClass().getName().contains("LabeledText")) {
                return;
            }
            TreeItem treeItem = (TreeItem) rootTreeView.getSelectionModel().getSelectedItem();
            Pair<String, List<String>> sel = getPluginKey(treeItem);
            if (sel != null) {
                Node node = event.getPickResult().getIntersectedNode();
                ContextMenu menu = pluginMap.get(sel.getKey()).onContextMenuRequested(sel.getValue());
                if (menu != null) {
                    menu.show(node, javafx.geometry.Side.BOTTOM, 0, 0);
                }
            }
        });

        mainTabPane.setOnContextMenuRequested(event -> {
            ContextMenu cm = new ContextMenu();
            MenuItem menuItem = new MenuItem("关闭当前");
            cm.getItems().add(menuItem);
            MenuItem menuItem1 = new MenuItem("关闭左侧");
            cm.getItems().add(menuItem1);
            MenuItem menuItem2 = new MenuItem("关闭全部");
            cm.getItems().add(menuItem2);

            menuItem.setOnAction(event1 -> {
                Tab tab = mainTabPane.getSelectionModel().getSelectedItem();
                mainTabPane.getTabs().removeIf(t -> t.getText().equals(tab.getText()));
            });
            menuItem1.setOnAction(event1 -> {
                Tab tab = mainTabPane.getSelectionModel().getSelectedItem();
                int idx = mainTabPane.getTabs().indexOf(tab);
                if (idx > 0) {
                    List<Tab> liveTabs = Lists.newArrayList();
                    for (int i = idx; i < mainTabPane.getTabs().size(); i++) {
                        liveTabs.add(mainTabPane.getTabs().get(i));
                    }
                    mainTabPane.getTabs().clear();
                    mainTabPane.getTabs().addAll(liveTabs);
                }
            });
            menuItem2.setOnAction(event1 -> {
                mainTabPane.getTabs().clear();
            });
            cm.show(event.getPickResult().getIntersectedNode(), javafx.geometry.Side.BOTTOM, 0, 0);
        });
    }

    private Pair<String, List<String>> getPluginKey(TreeItem treeItem) {
        if (treeItem == null || treeItem.getValue().toString().equals(bundle.getString("treeViewRootItem"))) {
            return null;
        }
        List<String> path = Lists.newArrayList();
        TreeItem tmp = treeItem;
        while (!pluginMap.containsKey(tmp.getValue().toString())) {
            path.add(tmp.getValue().toString());
            tmp = treeItem.getParent();
            if (tmp == null) {
                break;
            }
        }
        String key = tmp.getValue().toString();
        return new Pair<>(key, path);
    }

    @Override
    public TreeView treeView() {
        return rootTreeView;
    }

    @Override
    public TabPane tabPane() {
        return mainTabPane;
    }
}
