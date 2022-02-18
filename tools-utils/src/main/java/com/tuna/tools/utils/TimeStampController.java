package com.tuna.tools.utils;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

public class TimeStampController implements Initializable {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    @FXML
    private TextField input1;
    @FXML
    private TextField input2;
    @FXML
    private TextField out1;
    @FXML
    private TextField out2;
    @FXML
    private ComboBox type1;
    @FXML
    private ComboBox type2;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        ObservableList<String> options1 = FXCollections.observableArrayList("ms", "s");
        ObservableList<String> options2 = FXCollections.observableArrayList("ms", "s");
        type1.setItems(options1);
        type2.setItems(options2);

        type1.setValue("ms");
        type2.setValue("ms");

        input1.setOnKeyReleased(event -> {
            convert1();
        });
        input2.setOnKeyReleased(event -> {
            convert2();
        });
        input1.setOnMouseExited(event -> {
            convert1();
        });
        input2.setOnMouseExited(event -> {
            convert2();
        });
        type1.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                convert1();
            }
        });
        type2.valueProperty().addListener(new ChangeListener() {
            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                convert2();
            }
        });

        input1.setText(Long.toString(System.currentTimeMillis()));
        input2.setText(sdf.format(new Date()));
    }

    private void convert1() {
        if (StringUtils.isEmpty(input1.getText())) {
            return;
        }
        long ts = Long.parseLong(input1.getText());
        if (type1.getValue().equals("ms")) {
            out1.setText(sdf.format(new Date(ts)));
        } else {
            out1.setText(sdf.format(new Date(ts * 1000L)));
        }
    }

    private void convert2() {
        if (StringUtils.isEmpty(input2.getText())) {
            return;
        }
        try {
            if (type2.getValue().equals("ms")) {
                out2.setText(Long.toString(sdf.parse(input2.getText()).getTime()));
            } else {
                out2.setText(Long.toString(sdf.parse(input2.getText()).getTime() / 1000L));
            }
        } catch (Exception e) {
        }
    }
}
