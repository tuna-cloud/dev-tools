package com.tuna.desktop.kafka.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class KafkaController implements Initializable {

    @FXML
    private TreeView treeView;

    public void onTreeViewClick() {
        System.out.println("onTreeViewClick");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(resources.getBaseBundleName());
        TreeItem<String> root = new TreeItem<String>(resources.getString("offset"));
        root.setExpanded(true);
        root.getChildren().addAll(
            new TreeItem<String>("Item 1"),
            new TreeItem<String>("Item 2"),
            new TreeItem<String>("Item 3")
        );
        treeView.setRoot(root);
    }
}
