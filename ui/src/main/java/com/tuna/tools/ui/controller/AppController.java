package com.tuna.tools.ui.controller;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.tuna.tools.plugin.ToolPlugin;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.util.Pair;

import java.net.URL;
import java.util.*;

public class AppController implements Initializable {

    @FXML
    private TreeView rootTreeView;
    @FXML
    private TabPane tabPane;

    private ResourceBundle bundle = ResourceBundle.getBundle("app", new Locale("zh"));

    private Map<String, ToolPlugin> pluginMap = Maps.newHashMap();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ServiceLoader<ToolPlugin> loader = ServiceLoader.load(ToolPlugin.class);
        for (ToolPlugin plugin : loader) {
            pluginMap.put(plugin.name(), plugin);
        }

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
                menu.show(node, javafx.geometry.Side.BOTTOM, 0, 0);
                for (MenuItem item : menu.getItems()) {
                    item.setOnAction(actionEvent -> {
                        String label = ((Label) item.getGraphic()).getText();
                        pluginMap.get(sel.getKey()).onMenuItemClick(label, tabPane);
                        SingleSelectionModel selectionModel = tabPane.getSelectionModel();
                        selectionModel.select(tabPane.getTabs().size() - 1);
                    });
                }
            }
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
}
