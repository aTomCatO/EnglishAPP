package com.english.scene.event;

import com.english.entity.Corpus;
import com.english.entity.Dictionary;
import com.english.util.StringUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import static com.english.scene.AbstractScene.*;

/**
 * @author XYC
 */
public class InputDataEventHandler implements EventHandler<ActionEvent> {
    public static final InputDataEventHandler inputDataEventHandler = new InputDataEventHandler();
    private final OkActionEvent okActionEvent = new OkActionEvent();
    private final GridPane gridPane = new GridPane();
    private final TextField inputEn = getTextField(666);
    private final TextField inputZh = getTextField(666);
    private final TextField inputEnText = getTextField(666);
    private final TextField inputZhText = getTextField(666);

    private InputDataEventHandler() {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(6.6);

        Label enLabel = getLabel("英语单词: ", 26);
        Label zhLabel = getLabel("中文翻译: ", 26);
        Label enTextLabel = getLabel("单词例句: ", 26);
        Label zhTextLabel = getLabel("例句翻译: ", 26);

        gridPane.add(enLabel, 1, 0);
        gridPane.add(inputEn, 2, 0);
        gridPane.add(zhLabel, 1, 2);
        gridPane.add(inputZh, 2, 2);
        gridPane.add(enTextLabel, 1, 4);
        gridPane.add(inputEnText, 2, 4);
        gridPane.add(zhTextLabel, 1, 6);
        gridPane.add(inputZhText, 2, 6);
    }

    @Override
    public void handle(ActionEvent event) {
        DIALOG_OK.setOnAction(okActionEvent);
        DIALOG.setGraphic(gridPane);
        setDialog("导入数据", 866, 258);
        DIALOG.show();
    }

    class OkActionEvent implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            String zhRegex = "[\u4e00-\u9fa5]+";
            String zhTextRegex = "[\u4e00-\u9fa5\\w\\pP]+";
            String enRegex = "[a-zA-Z]+";
            String enTextRegex = "[\\w\\s\\pP]+";
            String en = inputEn.getText();
            String zh = inputZh.getText();
            String zhText = inputZhText.getText();
            String enText = inputEnText.getText();
            if (StringUtil.hasText(en) && en.matches(enRegex) && StringUtil.hasText(zh) && zh.matches(zhRegex)) {
                DICTIONARY_SERVICE.save(new Dictionary(en, zh));
                if (StringUtil.hasText(zhText) && enText.matches(enTextRegex) && StringUtil.hasText(enText) && zhText.matches(zhTextRegex)) {
                    CORPUS_SERVICE.save(new Corpus(en, enText, zhText));
                }
            }
            DIALOG_OK.setOnAction(null);
            DIALOG.setGraphic(null);
        }
    }
}
