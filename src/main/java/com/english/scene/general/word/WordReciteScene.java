package com.english.scene.general.word;

import com.english.EnglishAppStart;
import com.english.entity.Corpus;
import com.english.entity.Dictionary;
import com.english.scene.AbstractScene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XYC
 * 背词场景
 */
public class WordReciteScene extends AbstractScene<Object> {
    private ListView<Corpus> listView;
    private Label enPreviousLabel;
    private Label enCurrentLabel;
    private Label zhLabel;

    public void initScene() {
        super.initScene();

        //进行场景基本组件实例化
        listView = new ListView<>();
        enPreviousLabel = new Label();
        enCurrentLabel = new Label();
        zhLabel = new Label();

        listView.setPrefHeight(166);
        listView.setBorder(new Border(new BorderStroke(Paint.valueOf("384A98FF"), BorderStrokeStyle.DASHED, new CornerRadii(10), new BorderWidths(2))));

        enPreviousLabel.setFont(Font.font(18));
        enCurrentLabel.setFont(Font.font(28));
        zhLabel.setFont(Font.font(16));

        addSceneVBox();
        addExitButton();
        addNextButton();


        sceneVBox.getChildren().addAll(enPreviousLabel, enCurrentLabel, zhLabel, listView);
    }

    @Override
    public Object doCall() {
        dataIndex = 0;
        DICTIONARY_LIST.clear();
        DICTIONARY_LIST.addAll(DICTIONARY_SERVICE.queryRandomAddCorpus(dataSize));
        return null;
    }

    @Override
    public void updateUI(Object value) {
        Dictionary dictionary = DICTIONARY_LIST.get(dataIndex);
        enCurrentLabel.setText(dictionary.getEn());
        zhLabel.setText(dictionary.getZh());
        List<Corpus> dictionaryCorpusList = dictionary.getCorpusList();
        if (dictionaryCorpusList != null && dictionaryCorpusList.size() > 0) {
            listView.getItems().addAll(dictionaryCorpusList);
        }
    }

    @Override
    public void bindEvent() {
        exitButtonEvent();
        nextButtonEvent();
    }

    public void nextButtonEvent() {
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                enPreviousLabel.setText(enCurrentLabel.getText());
                dataIndex += 1;
                if (dataIndex < dataSize) {
                    listView.getItems().clear();
                    Dictionary dictionary = DICTIONARY_LIST.get(dataIndex);
                    enCurrentLabel.setText(dictionary.getEn());
                    zhLabel.setText(dictionary.getZh());
                    List<Corpus> dictionaryCorpusList = dictionary.getCorpusList();
                    if (dictionaryCorpusList != null && dictionaryCorpusList.size() > 0) {
                        listView.getItems().addAll(dictionaryCorpusList);
                    }
                } else {
                    List<String> items = new ArrayList<>();
                    items.add("退出");
                    items.add("单词补全");
                    items.add("重新背词");
                    ChoiceBox<String> choiceBox = new ChoiceBox<>();
                    choiceBox.setValue("退出");
                    choiceBox.getItems().addAll(items);
                    MAIN_DIALOG.setTitle("计划完成");
                    MAIN_DIALOG.setGraphic(choiceBox);
                    MAIN_DIALOG.show();
                    MAIN_DIALOG.setWidth(266);
                    MAIN_DIALOG.setHeight(166);
                    MAIN_DIALOG.setOnCloseRequest(new EventHandler<DialogEvent>() {
                        @Override
                        public void handle(DialogEvent event) {
                            if (MAIN_DIALOG.getResult().getButtonData().isDefaultButton()) {
                                String selectedItem = choiceBox.getValue();
                                switch (selectedItem) {
                                    case "单词补全": {
                                        dataIndex = 0;
                                        EnglishAppStart.convertScene("CompleteWordByFillScene");
                                        break;
                                    }
                                    case "重新背词": {
                                        dataIndex = -1;
                                        break;
                                    }
                                    default: {
                                        EnglishAppStart.convertScene("MainScene");
                                        break;
                                    }
                                }
                            }
                            MAIN_DIALOG.setGraphic(null);
                        }
                    });
                }
            }
        });
    }
}
