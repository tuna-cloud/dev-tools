package com.tuna.tools.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {
    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        Locale locale = new Locale("zh");
        ResourceBundle bundle = ResourceBundle.getBundle("app", locale);

        scene = new Scene(FXMLLoader.load(App.class.getResource("app.fxml"), bundle));
        scene.getStylesheets().add(App.class.getResource("default.css").toExternalForm());
        stage.setScene(scene);

        stage.setTitle(bundle.getString("title"));
        stage.getIcons().add(new Image(App.class.getResourceAsStream("favicon.png")));

        stage.show();
    }
}
