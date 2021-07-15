package com.tuna.desktop.kafka.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class AppController implements Initializable {

    @FXML
    private TreeView treeView;
    @FXML
    private RadioMenuItem kafka;
    @FXML
    private RadioMenuItem zookeeper;
    @FXML
    private RadioMenuItem redis;

    public void onTreeViewClick() {
        System.out.println("onTreeViewClick");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup group = new ToggleGroup();
        kafka.setToggleGroup(group);
        kafka.setSelected(true);
        zookeeper.setToggleGroup(group);
        redis.setToggleGroup(group);

        kafka.setOnAction(event -> {

        });

        zookeeper.setOnAction(event -> {

        });

        redis.setOnAction(event -> {

        });

        TreeItem<String> root = new TreeItem<String>(resources.getString("cluster"));
        root.setExpanded(true);
        root.getChildren().addAll(
            new TreeItem<String>("Item 1"),
            new TreeItem<String>("Item 2"),
            new TreeItem<String>("Item 3")
        );
        treeView.setRoot(root);
    }
}
