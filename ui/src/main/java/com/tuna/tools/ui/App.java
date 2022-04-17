package com.tuna.tools.ui;

import com.google.common.collect.Sets;
import com.tuna.tools.common.VertxInstance;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.apache.commons.lang3.SystemUtils;

import java.awt.Dimension;
import java.awt.Toolkit;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

public class App extends Application {
    private static Scene scene;

    @Override
    public void start(Stage stage) throws Exception {
        initHtml();
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
        stage.setOnCloseRequest(event -> {
            VertxInstance.close();
        });
    }

    private void initHtml() {
        String htmlDir = SystemUtils.getUserDir().getAbsolutePath() + File.separator + "html";
        File htmlFile = new File(htmlDir);
        if (htmlFile.exists()) {
            return;
        }
        String resourceProject = SystemUtils.getUserDir().getAbsolutePath() + File.separator + "tools-resource";
        // 开发环境，有resource工程，目录直接定位到tools-resource的资源目录
        if (new File(resourceProject).exists()) {
            return;
        }
        // 如果还没有
        String appDir = SystemUtils.getUserDir().getAbsolutePath() + File.separator + "app";
        // 尝试从jar包中取出html文件
        String jar = appDir + File.separator + "tools-resource-1.0.0.jar";
        com.tuna.tools.common.SystemUtils.unzip(jar, SystemUtils.getUserDir().getAbsolutePath(),
                Sets.newHashSet("css", "js", "png", "html", "eot", "ttf", "woff", "woff2", "key", "cert", "json"));
    }
}
