package com.tuna.tools.kafka;

import com.tuna.tools.plugin.ToolPlugin;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;

public class KafkaTool implements ToolPlugin {
    public static void main(String[] args) {

    }
    @Override
    public String name() {
        return "Kafka";
    }

    @Override
    public TreeItem rootItem() {
        Node rootIcon =  new ImageView(new Image(getClass().getClassLoader().getResourceAsStream("logo.png")));

        TreeItem root = new TreeItem(name(), rootIcon);
        root.getChildren().add(new TreeItem<>("192.168.3.1"));
        root.getChildren().add(new TreeItem<>("192.168.3.2"));
        return root;
    }

    @Override
    public void onTreeItemDoubleClick(List<String> path) {
        System.out.println("onTreeItemDoubleClick");
    }

    @Override
    public void onTreeItemClick(List<String> path) {
        System.out.println(path.toString());
    }

    @Override
    public void onMenuItemClick(String item, TabPane tabPane) {
        Tab tab = new Tab(item);
        tabPane.getTabs().add(tab);
    }

    @Override
    public ContextMenu onContextMenuRequested(List<String> path) {
        final ContextMenu cm = new ContextMenu();

        MenuItem menuItem1 = getMenuItemForLine("line 1");
        MenuItem menuItem2 = getMenuItemForLine("line 2");
        MenuItem menuItem3 = getMenuItemForLine("line 3");
        cm.getItems().add(menuItem1);
        cm.getItems().add(menuItem2);
        cm.getItems().add(menuItem3);

        menuItem1.setOnAction(event -> {

        });
        return cm;
    }

    private MenuItem getMenuItemForLine(String menuName) {
        Label menuLabel = new Label(menuName);
        MenuItem menuItem = new MenuItem();
        menuItem.setGraphic(menuLabel);
        return menuItem;
    }
}
