package com.english.scene.game;

import com.english.EnglishAppStart;
import com.english.scene.AbstractScene;
import com.english.scheduled_service.CountDownScheduledService;
import javafx.event.EventHandler;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;

/**
 * @author XYC
 */
public abstract class AbstractGameScene extends AbstractScene implements CountDownScheduledService.CountDownSupport {
    protected CountDownScheduledService gameCountDownScheduledService;
    protected Integer gameDuration;
    protected Integer correctCount;

    /**
     * 显示 倒计时 的Label
     */
    protected Label countDownLabel;

    public void gameEnd() {
        gameCountDownScheduledService.cancel();
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
}
