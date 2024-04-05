package com.english.scene.game;

import com.english.EnglishAppStart;
import com.english.concurrent.CountdownExecutor;
import com.english.scene.AbstractScene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;

/**
 * @author XYC
 */
public abstract class CountdownScene<T> extends AbstractScene<T> {
    /**
     * 倒计时执行器
     */
    protected CountdownExecutor countdownExecutor;

    /**
     * 答题正确数
     */
    protected Integer correctCount;

    /**
     * 显示 倒计时 的Label
     */
    protected Label countdownLabel;


    /**
     * 添加倒计时组件
     */
    public void addCountdown() {
        countdownExecutor = CountdownExecutor.getCountDownExecutor();
        countdownLabel = countdownExecutor.getCountDownLabel();

        anchorPane.getChildren().add(countdownLabel);
        AnchorPane.setTopAnchor(countdownLabel, 8.8);
        AnchorPane.setRightAnchor(countdownLabel, 8.8);
    }

    /**
     * 倒计时结束
     */
    public void countdownEnd() {
        countdownExecutor.cancel();
        setDialog("倒计时结束", 266, 166);
        DIALOG.setContentText(correctCount + "/" + dataSize);
        DIALOG_OK.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                DIALOG_OK.setOnAction(null);
                DIALOG.setContentText(null);
                EnglishAppStart.convertScene("com.english.scene.general.word.WordShowScene");
            }
        });
        DIALOG.show();
    }
}
