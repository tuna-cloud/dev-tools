package com.tuna.tools.utils;

import com.google.common.collect.Maps;
import com.tuna.tools.plugin.ToolPlugin;
import com.tuna.tools.plugin.UiContext;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class CommonTool implements ToolPlugin {

    private static final Logger logger = Logger.getLogger(CommonTool.class);

    private UiContext ctx;

    private Map<String, List<String>> openedTabMap = Maps.newHashMap();

    @Override
    public String name() {
        return "常用";
    }

    @Override
    public TreeItem rootItem() {
        Node rootIcon = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("util.png")));
        TreeItem root = new TreeItem(name(), rootIcon);
        root.getChildren().add(new TreeItem<>("Json格式化"));
        root.getChildren().add(new TreeItem<>("时间戳转换"));
        return root;
    }

    @Override
    public void init(UiContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onTreeItemDoubleClick(List<String> path) {
        if (!CollectionUtils.isEmpty(path)) {
            openNewTab(path.get(path.size() - 1));
        }
    }

    @Override
    public void onTreeItemClick(List<String> path) {

    }

    @Override
    public ContextMenu onContextMenuRequested(List<String> path) {
        if (CollectionUtils.isEmpty(path)) {
            return null;
        }
        final ContextMenu cm = new ContextMenu();

        MenuItem menuItem1 = new MenuItem("打开");
        cm.getItems().add(menuItem1);

        menuItem1.setOnAction(event -> {
            openNewTab(path.get(path.size() - 1));
        });
        return cm;
    }


    private void openNewTab(String type) {
        Tab tab = new Tab();
        String text = type;
        if (openedTabMap.containsKey(type) && openedTabMap.get(type).size() > 0) {
            text += "(" + openedTabMap.get(type).size() + ")";
        }
        tab.setText(text);
        tab.setId(text);
        try {
            if (type.contains("Json格式化")) {
                Parent root = FXMLLoader.load(CommonTool.class.getResource("jackson.fxml"));
                tab.setContent(root);
            } else {
                Parent root = FXMLLoader.load(CommonTool.class.getResource("timestamp.fxml"));
                tab.setContent(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        tab.setOnClosed(event -> {
            openedTabMap.get(type).remove(tab.getId());
        });
        ctx.tabPane().getTabs().add(tab);
        if (!openedTabMap.containsKey(type)) {
            openedTabMap.put(type, Lists.newArrayList());
        }
        openedTabMap.get(type).add(text);
        ctx.tabPane().getSelectionModel().select(tab);
    }
}
