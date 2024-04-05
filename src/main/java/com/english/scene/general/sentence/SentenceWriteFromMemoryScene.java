package com.english.scene.general.sentence;

import com.english.EnglishAppStart;
import com.english.Utils.InstanceUtils;
import com.english.Utils.StringUtils;
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

import java.util.regex.Matcher;

/**
 * @author XYC
 * 语句默写场景
 */
public class SentenceWriteFromMemoryScene extends AbstractScene<Object> {
    /**
     * 计时关闭弹窗任务
     */
    private Task<Boolean> timedCloseDialogTask;
    private static final Label enTextLabel = getLabel(13);
    private static final Label zhTextLabel = getLabel(13);
    private static final TextArea inputTextArea = new TextArea();
    private static final Label correctRateLabel = getLabel(16);
    private static final Label questionLabel = getLabel(13);
    private Integer correctCount;

    @Override
    public void initScene() {
        super.initScene();
        inputTextArea.setWrapText(true);
        inputTextArea.setFont(Font.font(16));
        inputTextArea.setPrefHeight(66);

        addSceneVBox();
        addExitButton();
        addNextButton();

        anchorPane.getChildren().add(correctRateLabel);
        AnchorPane.setTopAnchor(correctRateLabel, 8.8);
        AnchorPane.setRightAnchor(correctRateLabel, 8.8);

        sceneVBox.getChildren().addAll(enTextLabel, inputTextArea, zhTextLabel);
    }

    @Override
    public Object doCall() {
        dataIndex = 0;
        correctCount = 0;
        dataSize = 10;
        CORPUS_LIST.clear();
        CORPUS_LIST.addAll(CORPUS_SERVICE.queryRandom(dataSize));
        return null;
    }

    @Override
    public void updateUI(Object value) {
        updateQuestion();
    }

    public void updateQuestion() {
        String enText = CORPUS_LIST.get(dataIndex).getEnText();
        InstanceUtils.LOGGER.info(enText);
        questionLabel.setText(enText);
        zhTextLabel.setText(CORPUS_LIST.get(dataIndex).getZhText());
        DIALOG.show();

        timedCloseDialogTask = new Task<Boolean>() {
            @Override
            protected Boolean call() throws InterruptedException {
                Thread.sleep(6000);
                return null;
            }

            @Override
            protected void updateValue(Boolean open) {
                DIALOG.close();
            }
        };
        BaseService.THREAD_POOL.execute(timedCloseDialogTask);
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
                if (StringUtils.hasText(inputText)) {
                    // 分别从语句中匹配每一个词
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
                        // 在Java中会进行整数除法，会导致结果小数部分被截断。为了避免这个问题，应将至少一个操作数转换为浮点数，以便进行浮点除法。
                        // 最后将结果使用 String.format 来格式化输出:
                        // 1、结果保留两位小数: %.2f%  (double) correctCount / wordCount * 100
                        // 2、结果不保留小数位: %d%    (int)((double) correctCount / wordCount * 100) // 将浮点数转换为整数，去掉小数部分
                        correctRateLabel.setText(String.format("正确率: %d%%", (int) ((double) correctCount / wordCount * 100)));
                        enTextLabel.setText(enSentence);
                    } else {
                        enTextLabel.setText(enSentence);
                        inputTextArea.setText(null);
                        correctRateLabel.setText(null);
                        dataIndex += 1;
                        if (dataIndex == dataSize) {
                            doCall();
                        }
                        updateQuestion();
                    }
                    wordCount = 0;
                    correctCount = 0;
                } else {
                    enTextLabel.setText(enSentence);
                }
            }
        });
    }

    @Override
    public void exitButtonEvent() {
        exitButton.setOnAction(event -> {
            if (timedCloseDialogTask != null) {
                timedCloseDialogTask.cancel();
            }
            enTextLabel.setText(null);
            DIALOG.setGraphic(null);
            EnglishAppStart.sceneChanger("com.english.scene.general.MainScene");
        });
    }


    @Override
    public Scene run() {
        setDialog("请看题", 366, 166);
        DIALOG.setGraphic(questionLabel);
        return super.run();
    }

}
