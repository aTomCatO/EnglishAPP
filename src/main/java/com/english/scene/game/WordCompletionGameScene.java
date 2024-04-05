package com.english.scene.game;

import com.english.EnglishAppStart;
import com.english.scene.general.word.WordCompletionScene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;


/**
 * @author XYC
 */
public class WordCompletionGameScene extends WordCompletionScene {
    @Override
    public void extend() {
        addCountdown();
    }

    @Override
    public Object doCall() {
        dataIndex = 0;
        correctCount = 0;
        DICTIONARY_LIST.clear();
        DICTIONARY_LIST.addAll(DICTIONARY_SERVICE.queryRandom(dataSize));
        return null;
    }

    @Override
    public void exitButtonEvent() {
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                countdownExecutor.cancel();
                enPreviousLabel.setText(null);
                enCurrentTextFlow.getChildren().clear();
                EnglishAppStart.convertScene("com.english.scene.general.MainScene");
            }
        });
    }

    @Override
    public void nextButtonEvent() {
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean isRight = assessAnswer();
                if (isRight) {
                    enCurrentTextFlow.getChildren().clear();
                    // 先通过旧单词的索引设置 enPreviousLabel 的文本
                    enPreviousLabel.setText(DICTIONARY_LIST.get(dataIndex).getEn());
                    // 再更新为新单词的索引
                    dataIndex += 1;
                    correctCount += 1;
                    if (dataIndex == dataSize) {
                        countdownEnd();
                    } else {
                        fillImplement();
                        zhCurrentLabel.setText(DICTIONARY_LIST.get(dataIndex).getZh());
                        TEXT_FIELD_LIST.get(0).requestFocus();
                    }
                }
            }
        });
    }

    @Override
    public Scene run(Object... args) {
        int duration = (Integer) args[0];
        countdownExecutor.setCountdownScene(this);
        countdownExecutor.setDuration(duration);
        countdownExecutor.restart();
        loadData();
        return scene;
    }
}
