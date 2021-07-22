package com.tuna.desktop.kafka;

import java.util.Locale;
import java.util.ResourceBundle;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class App extends Application {
    private static Scene scene;

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Locale locale = new Locale("zh");
        ResourceBundle bundle = ResourceBundle.getBundle("app", locale);

        scene = new Scene(FXMLLoader.load(App.class.getResource("app.fxml"), bundle));
        scene.getStylesheets().add(App.class.getResource("styles.css").toExternalForm());
        stage.setScene(scene);

        stage.setTitle(bundle.getString("title"));
        stage.getIcons().add(new Image(App.class.getResourceAsStream("/logo.png")));

        Rectangle2D screenRectangle = Screen.getPrimary().getBounds();
        double width = screenRectangle.getWidth();
        double height = screenRectangle.getHeight();
        stage.setWidth(width / 3.0 * 2.0);
        stage.setHeight(height / 3.0 * 2.0);

        stage.show();
    }
}
