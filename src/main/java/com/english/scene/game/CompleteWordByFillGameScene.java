package com.english.scene.game;

import com.english.EnglishAppStart;
import com.english.concurrent.CountDownHandler;
import com.english.scene.general.word.CompleteWordByFillScene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;


/**
 * @author XYC
 */
public class CompleteWordByFillGameScene extends CompleteWordByFillScene implements CountDownHandler.CountDownSupport {
    protected CountDownHandler gameCountDownHandler;
    protected Integer gameDuration;
    protected Integer correctCount;
    /**
     * 显示 倒计时 的Label
     */
    protected Label countDownLabel;

    @Override
    public void initScene() {
        super.initScene();

        gameCountDownHandler = CountDownHandler.getCountDownHandler();
        countDownLabel = gameCountDownHandler.getCountDownLabel();

        this.anchorPane.getChildren().add(countDownLabel);
        AnchorPane.setTopAnchor(countDownLabel, 8.8);
        AnchorPane.setRightAnchor(countDownLabel, 8.8);
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
    public void bindEvent() {
        this.exitButtonEvent();
        this.nextButtonEvent();
    }

    @Override
    public void exitButtonEvent() {
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gameCountDownHandler.cancel();
                enPreviousLabel.setText(null);
                enCurrentTextFlow.getChildren().clear();
                EnglishAppStart.convertScene("MainScene");
            }
        });
    }

    @Override
    public void nextButtonEvent() {
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean right = true;
                for (int i = 0; i < fillChars.length; i++) {
                    TextField fill = TEXT_FIELD_LIST.get(i);
                    if (!String.valueOf(fillChars[i]).equals(fill.getText())) {
                        right = false;
                        break;
                    }
                }
                if (right) {
                    enCurrentTextFlow.getChildren().clear();
                    //先通过旧单词的索引设置 enPreviousLabel 的文本
                    enPreviousLabel.setText(DICTIONARY_LIST.get(dataIndex).getEn());
                    //再更新为新单词的索引
                    dataIndex += 1;
                    correctCount += 1;
                    if (dataIndex == dataSize) {
                        gameEnd();
                    } else {
                        fillImplement();
                        zhCurrentLabel.setText(DICTIONARY_LIST.get(dataIndex).getZh());
                        TEXT_FIELD_LIST.get(0).requestFocus();
                    }
                }
            }
        });
    }

    public void gameEnd() {
        gameCountDownHandler.cancel();
        setMainDialog("竞赛结束", 266, 166);
        MAIN_DIALOG.setContentText("共 " + dataSize + " 道题\n" +
                "您一共答对 " + correctCount + " 道题\n" +
                "成绩为: " + (100 / dataSize) * correctCount);
        MAIN_DIALOG.setOnCloseRequest(new EventHandler<DialogEvent>() {
            @Override
            public void handle(DialogEvent event) {
                MAIN_DIALOG.setContentText(null);
                EnglishAppStart.convertScene("WordShowScene");
            }
        });
        MAIN_DIALOG.show();
    }

    @Override
    public void countDownEnd() {
        gameEnd();
    }

    @Override
    public Scene run(Object... args) {
        this.gameDuration = (Integer) args[0];
        gameCountDownHandler.setCountDownSupport(this);
        gameCountDownHandler.setTime(gameDuration);
        gameCountDownHandler.restart();
        initData();
        return scene;
    }
}
