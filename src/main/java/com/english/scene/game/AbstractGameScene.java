package com.english.scene.game;

import com.english.EnglishAppStart;
import com.english.scene.AbstractScene;
import com.english.scheduled_service.GameCountDownScheduledService;
import javafx.event.EventHandler;
import javafx.scene.control.DialogEvent;
import javafx.scene.control.Label;

/**
 * @author XYC
 */
public abstract class AbstractGameScene extends AbstractScene {
    protected GameCountDownScheduledService gameCountDownScheduledService;
    protected Integer gameDuration;
    protected Integer correctCount;

    /**
     * 显示 倒计时 的Label
     */
    protected Label countDownLabel;

    public void gameEnd() {
        gameCountDownScheduledService.cancel();
        getDialog("竞赛结束", 266, 166);
        dialog.setContentText("共 " + dataSize + " 道题\n" +
                "您一共答对 " + correctCount + " 道题\n" +
                "成绩为: " + (100 / dataSize) * correctCount);
        dialog.setOnCloseRequest(new EventHandler<DialogEvent>() {
            @Override
            public void handle(DialogEvent event) {
                dialog.setContentText(null);
                EnglishAppStart.convertScene("单词展示场景");
            }
        });
        dialog.show();
    }
}
