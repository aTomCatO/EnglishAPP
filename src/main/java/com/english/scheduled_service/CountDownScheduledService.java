package com.english.scheduled_service;

import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.util.Duration;
import lombok.Data;

/**
 * @author XYC
 * 竞赛场景倒计时
 */
@Data
public class CountDownScheduledService extends ScheduledService<Integer> {

    private static CountDownScheduledService scheduledService;
    private Integer time;
    private Label countDownLabel;

    private CountDownSupport countDownSupport;


    private CountDownScheduledService() {
        this.countDownLabel = new Label();
        countDownLabel.setFont(Font.font(13));
        setPeriod(Duration.seconds(1));
    }


    public static CountDownScheduledService getScheduledService() {
        if (scheduledService == null) {
            scheduledService = new CountDownScheduledService();
        }
        return scheduledService;
    }

    @Override
    protected Task<Integer> createTask() {
        return new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                return time--;
            }

            @Override
            protected void updateValue(Integer value) {
                super.updateValue(value);
                if (value >= 0) {
                    countDownLabel.setText(value.toString());
                } else {
                    countDownSupport.countDownEnd();
                }
            }
        };
    }

    //public void start(AbstractScene scene) {
    //    Class<?>[] interfaces = scene.getClass().getInterfaces();
    //    for (Class<?> anInterface : interfaces) {
    //        if (anInterface.equals(CountDownSupport.class)) {
    //            restart();
    //            return;
    //        }
    //    }
    //    throw new UnsupportedOperationException("This object type "+scene.getClass().getSimpleName()+" does not support countdown");
    //}

    public interface CountDownSupport {
        /**
         * 倒计时结束
         */
        void countDownEnd();
    }

}
