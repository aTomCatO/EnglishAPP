package com.english.scene.general.sentence;

import com.english.EnglishAppStart;
import com.english.scene.AbstractScene;
import com.english.scheduled_service.TimedCloseDialogScheduledService;
import javafx.concurrent.Service;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Font;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;

/**
 * @author XYC
 * 语句默写场景
 */
public class SentenceWriteFromMemoryScene extends AbstractScene {
    private Service<Boolean> scheduledService;
    private Label enTextLabel;
    private Label zhTextLabel;
    private TextArea inputTextArea;
    private Label correctRateLabel;
    private Integer correctCount;

    private Dialog<ButtonType> questionDialog;
    private Label questionLabel;

    {
        this.sceneName = "语句默写场景";
    }

    @Override
    public void initScene() {
        super.initScene();

        addVBoxMain();
        addExitButton();
        addNextButton();

        enTextLabel = new Label();
        zhTextLabel = new Label();
        correctRateLabel = new Label();
        questionLabel = new Label();

        inputTextArea = new TextArea();

        //设置自动换行
        enTextLabel.setWrapText(true);
        zhTextLabel.setWrapText(true);
        questionLabel.setWrapText(true);
        inputTextArea.setWrapText(true);

        enTextLabel.setFont(Font.font(13));
        zhTextLabel.setFont(Font.font(13));
        correctRateLabel.setFont(Font.font(16));
        inputTextArea.setFont(Font.font(16));

        inputTextArea.setPrefHeight(66);

        anchorPane.getChildren().add(correctRateLabel);
        AnchorPane.setTopAnchor(correctRateLabel, 8.8);
        AnchorPane.setRightAnchor(correctRateLabel, 8.8);

        vBoxMain.getChildren().addAll(enTextLabel, inputTextArea, zhTextLabel);

        //题目弹窗
        questionDialog = new Dialog<>();
        questionDialog.setGraphic(questionLabel);
        questionDialog.setTitle("请看题");
        questionDialog.setWidth(366);
        questionDialog.setHeight(166);

        scheduledService = new TimedCloseDialogScheduledService(questionDialog);
    }

    @Override
    public void initData() {
        dataSize = 10;
        dataIndex = 0;
        correctCount = 0;
        corpusList.clear();
        corpusList.addAll(corpusService.queryRandom(dataSize));

        updateQuestion();
    }

    @Override
    public void bindEvent() {
        exitButtonEvent();
        nextButtonEvent();
    }


    public void nextButtonEvent() {
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            int wordCount = 0;

            @Override
            public void handle(ActionEvent event) {
                String inputText = inputTextArea.getText();
                String enSentence = corpusList.get(dataIndex).getEnText();
                if (StringUtils.isBlank(inputText)) {
                    enTextLabel.setText(enSentence);
                    return;
                }
                //分别从语句中匹配每一个词
                Matcher matcherCorrectSentence = pattern.matcher(enSentence);
                Matcher matcherInputSentence = pattern.matcher(inputText);
                while (matcherCorrectSentence.find()) {
                    wordCount++;
                    while (matcherInputSentence.find()) {
                        if (matcherCorrectSentence.group(0).equals(matcherInputSentence.group(0))) {
                            correctCount += 1;
                        }
                    }
                    matcherInputSentence.reset();
                }
                if (correctCount < wordCount) {
                    correctRateLabel.setText("正确率: " + ((double) correctCount / wordCount) * 100 + "%");
                    enTextLabel.setText(enSentence);
                } else {
                    enTextLabel.setText(enSentence);
                    inputTextArea.setText(null);
                    correctRateLabel.setText(null);
                    dataIndex += 1;
                    if (dataIndex == dataSize) {
                        initData();
                    }
                    updateQuestion();
                }
                wordCount = 0;
                correctCount = 0;
            }
        });

    }

    @Override
    public void exitButtonEvent() {
        exitButton.setOnAction(event -> {
            scheduledService.cancel();

            enTextLabel.setText(null);

            EnglishAppStart.convertScene("主场景");
        });
    }

    public void updateQuestion() {
        String enText = corpusList.get(dataIndex).getEnText();
        System.out.println(enText);
        questionLabel.setText(corpusList.get(dataIndex).getEnText());
        zhTextLabel.setText(corpusList.get(dataIndex).getZhText());
        questionDialog.show();
        scheduledService.restart();
    }
}
