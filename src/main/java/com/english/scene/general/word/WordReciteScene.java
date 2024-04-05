package com.english.scene.general.word;

import com.english.EnglishAppStart;
import com.english.entity.Corpus;
import com.english.entity.Dictionary;
import com.english.scene.AbstractScene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ChoiceBox;
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
    private static final ListView<Corpus> listView = new ListView<>();
    private static final Label enPreviousLabel = new Label();
    private static final Label enCurrentLabel = new Label();
    private static final Label zhLabel = new Label();

    public void initScene() {
        super.initScene();

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
                    updateUI(null);
                } else {
                    List<String> items = new ArrayList<>();
                    items.add("重新背词");
                    items.add("单词补全");
                    items.add("退出");
                    ChoiceBox<String> choiceBox = new ChoiceBox<>();
                    choiceBox.setValue("退出");
                    choiceBox.getItems().addAll(items);
                    DIALOG.setGraphic(choiceBox);
                    setDialog("计划完成", 266, 166);
                    DIALOG.show();
                    DIALOG_OK.setOnAction(new EventHandler<ActionEvent>() {
                        @Override
                        public void handle(ActionEvent event) {
                            String selectedItem = choiceBox.getValue();
                            switch (selectedItem) {
                                case "单词补全" -> {
                                    dataIndex = 0;
                                    EnglishAppStart.sceneChanger("com.english.scene.general.word.WordCompletionScene");
                                }
                                case "重新背词" -> {
                                    dataIndex = -1;
                                }
                                default -> {
                                    EnglishAppStart.sceneChanger("com.english.scene.general.MainScene");
                                }
                            }
                            DIALOG_OK.setOnAction(null);
                            DIALOG.setGraphic(null);
                        }
                    });
                }
            }
        });
    }
}
