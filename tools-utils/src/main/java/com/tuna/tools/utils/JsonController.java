package com.tuna.tools.utils;

import com.tuna.commons.utils.JacksonUtils;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.stage.Popup;
import javafx.stage.Window;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang3.StringUtils;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class JsonController implements Initializable {

    @FXML
    private TextArea rawTextArea;
    @FXML
    private TextArea prettyTextArea;
    @FXML
    private TextField keyWordTextField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    public void onClick(MouseEvent event) {
        String text = rawTextArea.getText().trim();
        try {
            Object obj = praseJson(text);
            if (obj != null) {
                String prettyText = JacksonUtils.serializePretty(obj);
                prettyTextArea.setText(appendLineNo(prettyText));
            }
        } catch (Exception e) {
            prettyTextArea.setText(appendLineNo(e.getLocalizedMessage()));
        }
    }

    private String appendLineNo(String text) {
        if (StringUtils.isEmpty(text)) {
            return null;
        }
        String[] arrs = text.split("\n");
        StringBuilder bd = new StringBuilder();
        int no = 1;
        for (String s : arrs) {
            bd.append(StringUtils.rightPad(Integer.toString(no++) + ":", 8, " "));
            bd.append(s);
            bd.append("\n");
        }
        return bd.toString();
    }

    public void onSearchClick(MouseEvent event) {

        String text = prettyTextArea.getText();
        String keyWord = keyWordTextField.getText();
        if (StringUtils.isEmpty(text) || StringUtils.isEmpty(keyWord)) {
            return;
        }
        List<SearchResult> list = Lists.newArrayList();

        int idx = text.indexOf(keyWord);
        int prevIdx = 0;
        while (idx >= 0) {
            int lineStart = StringUtils.lastIndexOf(text, "\n", idx);
            if (lineStart == -1) {
                lineStart = -1;
            }
            int lineEnd = StringUtils.indexOf(text, "\n", idx);
            if (lineEnd == -1) {
                lineEnd = text.length();
            }
            list.add(new SearchResult(idx, idx + keyWord.length(), text.substring(lineStart + 1, lineEnd - 1)));
            prevIdx = idx;
            idx = text.indexOf(keyWord, prevIdx + keyWord.length());
        }


        showResult(list);
    }

    private void showResult(List<SearchResult> list) {
        Window parent = keyWordTextField.getScene().getWindow();
        Popup popup = new Popup();
        popup.setAutoHide(true);
        popup.setWidth(parent.getWidth() * 0.35);
        popup.setHeight(parent.getHeight() * 0.2);

        ListView<SearchResult> listView = new ListView<>();
        listView.setMinWidth(popup.getWidth());
        listView.setMaxWidth(popup.getWidth());
        listView.setMaxHeight(popup.getHeight());
        ObservableList<SearchResult> items = FXCollections.observableArrayList( list);
        listView.setItems(items);
        Text totalCount = new Text();
        totalCount.setText("共查找到" + list.size() + "条记录");
        popup.getContent().add(totalCount);
        popup.getContent().add(new Separator());
        popup.getContent().add(listView);
        popup.show(parent, parent.getX() + parent.getWidth() * 0.6, parent.getY() + parent.getHeight() * 0.35);

        listView.setOnMouseClicked(event -> {
            SearchResult searchResult = listView.getSelectionModel().getSelectedItem();
            if (searchResult != null) {
                prettyTextArea.selectRange(searchResult.getStart(), searchResult.getEnd());
                prettyTextArea.requestFocus();
            }
        });
    }

    private Object praseJson(String json) throws Exception {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        if (json.charAt(0) == '{') {
            return new JsonObject(json);
        }
        if (json.charAt(0) == '[') {
            return new JsonArray(json);
        }
        return null;
    }

    public static class SearchResult {
        private int start;
        private int end;
        private String line;

        public SearchResult() {
        }

        public SearchResult(int start, int end, String line) {
            this.start = start;
            this.end = end;
            this.line = line;
        }

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getEnd() {
            return end;
        }

        public void setEnd(int end) {
            this.end = end;
        }

        @Override
        public String toString() {
            return line;
        }
    }
}
