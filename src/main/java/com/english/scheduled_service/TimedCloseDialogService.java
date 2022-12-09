package com.english.scheduled_service;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import lombok.AllArgsConstructor;

/**
 * @author XYC
 * 定时关闭弹窗
 */

@AllArgsConstructor
public class TimedCloseDialogService extends Service<Boolean> {
    private final Dialog<ButtonType> dialog;

    @Override
    protected Task<Boolean> createTask() {

        return new Task<Boolean>() {
            @Override
            protected Boolean call() throws InterruptedException {
                Thread.sleep(6000);
                return null;
            }

            @Override
            protected void updateValue(Boolean open) {
                dialog.close();
            }
        };
    }
}
