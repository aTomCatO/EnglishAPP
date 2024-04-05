package com.english.concurrent;

import com.english.scene.game.CountdownScene;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.util.Duration;
import lombok.Data;

/**
 * @author XYC
 * 倒计时执行器
 */
@Data
public class CountdownExecutor extends ScheduledService<Integer> {

    /**
     * 懒汉单例
     */
    private static CountdownExecutor countDownExecutor;
    private Label countDownLabel;
    /**
     * 倒计时时长
     */
    private Integer duration;
    /**
     * 倒计时场景对象
     */
    private CountdownScene countdownScene;

    private CountdownExecutor() {
        this.countDownLabel = new Label();
        countDownLabel.setFont(Font.font(13));
        setPeriod(Duration.seconds(1));
    }


    public static CountdownExecutor getCountDownExecutor() {
        if (countDownExecutor == null) {
            countDownExecutor = new CountdownExecutor();
        }
        return countDownExecutor;
    }

    @Override
    protected Task<Integer> createTask() {
        return new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                return duration--;
            }

            @Override
            protected void updateValue(Integer value) {
                super.updateValue(value);
                if (value > 0) {
                    countDownLabel.setText(value.toString());
                } else {
                    countDownLabel.setText("0");
                    countdownScene.countdownEnd();
                }
            }
        };
    }
}
