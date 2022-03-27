package com.tuna.tools.fiddler;

import com.tuna.tools.plugin.ToolPlugin;
import com.tuna.tools.plugin.UiContext;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Tab;
import javafx.scene.control.TreeItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.List;

public class FiddlerTool implements ToolPlugin {
    private UiContext ctx;

    @Override
    public String name() {
        return "抓包工具";
    }

    @Override
    public TreeItem rootItem() {
        Node rootIcon = new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("fiddler.png")));
        return new TreeItem(name(), rootIcon);
    }

    @Override
    public void init(UiContext ctx) {
        this.ctx = ctx;
    }

    @Override
    public void onTreeItemDoubleClick(List<String> path) {
        for (Tab tab : ctx.tabPane().getTabs()) {
            if (name().equals(tab.getId())) {
                return;
            }
        }
        Tab tab = new Tab();
        tab.setText(name());
        tab.setId(name());
        try {
            Parent root = FXMLLoader.load(FiddlerTool.class.getResource("fiddler.fxml"));
            tab.setContent(root);
        } catch (IOException e) {
            e.printStackTrace();
        }

        ctx.tabPane().getTabs().add(tab);
        ctx.tabPane().getSelectionModel().select(tab);
    }

    @Override
    public void onTreeItemClick(List<String> path) {

    }

    @Override
    public ContextMenu onContextMenuRequested(List<String> path) {
        return null;
    }
}
