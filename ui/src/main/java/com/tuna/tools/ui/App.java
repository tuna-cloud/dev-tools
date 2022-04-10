package com.tuna.tools.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {
    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        Locale locale = new Locale("zh");
        ResourceBundle bundle = ResourceBundle.getBundle("app", locale);

        scene = new Scene(FXMLLoader.load(App.class.getResource("app.fxml"), bundle));
        stage.setScene(scene);

        stage.setTitle(bundle.getString("title"));
        stage.getIcons().add(new Image(App.class.getResourceAsStream("favicon.png")));

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        double width = screenSize.getWidth() * 0.8;
        double height = screenSize.getHeight() * 0.8;

        stage.setWidth(width);
        stage.setHeight(height);
        stage.show();
    }
}
