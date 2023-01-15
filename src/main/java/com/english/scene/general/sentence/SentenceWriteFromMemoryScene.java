package com.english.scene.general.sentence;

import com.english.EnglishAppStart;
import com.english.scene.AbstractScene;
import com.english.service.BaseService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
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
    /**
     * 计时关闭弹窗任务
     */
    private Task<Boolean> timedCloseDialogTask;
    private Label enTextLabel;
    private Label zhTextLabel;
    private TextArea inputTextArea;
    private Label correctRateLabel;
    private Integer correctCount;

    private Label questionLabel;

    @Override
    public void initScene() {
        super.initScene();

        addSceneVBox();
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

        sceneVBox.getChildren().addAll(enTextLabel, inputTextArea, zhTextLabel);
    }


    @Override
    public void initData() {
        dataSize = 10;
        dataIndex = 0;
        correctCount = 0;
        CORPUS_LIST.clear();
        CORPUS_LIST.addAll(CORPUS_SERVICE.queryRandom(dataSize));

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
                String enSentence = CORPUS_LIST.get(dataIndex).getEnText();
                if (StringUtils.isBlank(inputText)) {
                    enTextLabel.setText(enSentence);
                    return;
                }
                //分别从语句中匹配每一个词
                Matcher matcherCorrectSentence = PATTERN.matcher(enSentence);
                Matcher matcherInputSentence = PATTERN.matcher(inputText);
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
            if (timedCloseDialogTask != null) {
                timedCloseDialogTask.cancel();
            }
            MAIN_DIALOG.setGraphic(null);
            enTextLabel.setText(null);
            EnglishAppStart.convertScene("MainScene");
        });
    }

    public void updateQuestion() {
        String enText = CORPUS_LIST.get(dataIndex).getEnText();
        System.out.println(enText);
        questionLabel.setText(CORPUS_LIST.get(dataIndex).getEnText());
        zhTextLabel.setText(CORPUS_LIST.get(dataIndex).getZhText());
        MAIN_DIALOG.show();

        timedCloseDialogTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws InterruptedException {
                Thread.sleep(6000);
                return null;
            }

            @Override
            protected void updateValue(Boolean open) {
                MAIN_DIALOG.close();
            }
        };
        BaseService.THREAD_POOL.execute(timedCloseDialogTask);
    }

    @Override
    public Scene run() {
        addMainDialog("请看题", 366, 166);
        MAIN_DIALOG.setGraphic(questionLabel);
        return super.run();
    }

}
