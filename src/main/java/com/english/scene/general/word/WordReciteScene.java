package com.english.scene.general.word;

import com.english.EnglishAppStart;
import com.english.javaBeans.Corpus;
import com.english.javaBeans.Dictionary;
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
public class WordReciteScene extends AbstractScene {
    private ListView<Corpus> listView;
    private Label enPreviousLabel;
    private Label enCurrentLabel;
    private Label zhLabel;

    {
        this.sceneName = "背词场景";
    }

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

        addVBoxMain();
        addExitButton();
        addNextButton();


        vBoxMain.getChildren().addAll(enPreviousLabel, enCurrentLabel, zhLabel, listView);
    }

    @Override
    public void initData() {
        dictionaryList = dictionaryService.queryRandomAddCorpus(dataSize);
        dataIndex = 0;
        Dictionary dictionary = dictionaryList.get(dataIndex);
        enCurrentLabel.setText(dictionary.getEn());
        zhLabel.setText(dictionary.getZh());
        listView.getItems().addAll(dictionary.getCorpusList());

    }


    @Override
    public void bindEvent() {
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
                    Dictionary dictionary = dictionaryList.get(dataIndex);
                    enCurrentLabel.setText(dictionary.getEn());
                    zhLabel.setText(dictionary.getZh());
                    listView.getItems().addAll(dictionary.getCorpusList());
                } else {
                    List<String> items = new ArrayList<>();
                    items.add("退出");
                    items.add("单词补全");
                    items.add("重新背词");
                    ChoiceBox<String> choiceBox = new ChoiceBox<>();
                    choiceBox.setValue("退出");
                    choiceBox.getItems().addAll(items);
                    dialog.setTitle("计划完成");
                    dialog.setGraphic(choiceBox);
                    dialog.show();
                    dialog.setWidth(266);
                    dialog.setHeight(166);
                    dialog.setOnCloseRequest(new EventHandler<DialogEvent>() {
                        @Override
                        public void handle(DialogEvent event) {
                            if (dialog.getResult().getButtonData().isDefaultButton()) {
                                String selectedItem = choiceBox.getValue();
                                switch (selectedItem) {
                                    case "单词补全": {
                                        dataIndex = 0;
                                        EnglishAppStart.convertScene("单词补全场景");
                                        break;
                                    }
                                    case "重新背词": {
                                        dataIndex = -1;
                                        break;
                                    }
                                    default: {
                                        EnglishAppStart.convertScene("主场景");
                                        break;
                                    }
                                }
                            }
                            dialog.setGraphic(null);
                        }
                    });
                }
            }
        });
    }

}
