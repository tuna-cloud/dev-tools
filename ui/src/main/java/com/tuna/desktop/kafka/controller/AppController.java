package com.tuna.desktop.kafka.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    @FXML
    private RadioMenuItem kafka;
    @FXML
    private RadioMenuItem zookeeper;
    @FXML
    private RadioMenuItem redis;
    @FXML
    private VBox contentBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ToggleGroup group = new ToggleGroup();
        kafka.setToggleGroup(group);
        zookeeper.setToggleGroup(group);
        redis.setToggleGroup(group);

        kafka.setSelected(true);
        changePanel("kafka.fxml", resources);

        kafka.setOnAction(event -> {
            changePanel("kafka.fxml", resources);
        });

        zookeeper.setOnAction(event -> {
            changePanel("zookeeper.fxml", resources);
        });

        redis.setOnAction(event -> {
            changePanel("redis.fxml", resources);
        });

    }

    public void changePanel(String fxml, ResourceBundle bundle) {
        try {
            if (contentBox.getChildren().size() > 1) {
                contentBox.getChildren().remove(1);
            }
            Node rootLayout = FXMLLoader.load(getClass().getResource(fxml), bundle);
            VBox.setVgrow(rootLayout, Priority.ALWAYS);
            contentBox.getChildren().add(rootLayout);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
