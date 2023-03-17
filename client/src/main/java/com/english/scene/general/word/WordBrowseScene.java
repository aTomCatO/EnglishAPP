package com.english.scene.general.word;

import com.english.Client;
import com.english.scene.AbstractScene;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.text.Font;
import javafx.util.Duration;


/**
 * @author XYC
 * 单词浏览场景
 */
public class WordBrowseScene extends AbstractScene<Object> {
    private Label enPreviousLabel;
    private Label enCurrentLabel;
    private Label zhCurrentLabel;

    private final ScheduledService<Integer> scheduledService = new ScheduledService<Integer>() {
        {
            setDelay(Duration.seconds(6));
            setPeriod(Duration.seconds(6));
        }

        @Override
        protected Task<Integer> createTask() {
            return new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    if (dataIndex == dataSize - 1) {
                        dataIndex = 0;
                        DICTIONARY_LIST.clear();
                        DICTIONARY_LIST.addAll(DICTIONARY_SERVICE.queryRandom(dataSize));
                    }
                    return dataIndex += 1;
                }

                @Override
                protected void updateValue(Integer index) {
                    enPreviousLabel.setText(DICTIONARY_LIST.get(index - 1).getEn());
                    enCurrentLabel.setText(DICTIONARY_LIST.get(index).getEn());
                    zhCurrentLabel.setText(DICTIONARY_LIST.get(index).getZh());
                }
            };
        }
    };

    @Override
    public void initScene() {
        super.initScene();

        //进行场景基本组件实例化
        enPreviousLabel = new Label();
        enCurrentLabel = new Label();
        zhCurrentLabel = new Label();

        enPreviousLabel.setFont(Font.font(18));
        enCurrentLabel.setFont(Font.font(28));
        zhCurrentLabel.setFont(Font.font(23));

        addExitButton();
        addNextButton();
        addSceneVBox();
        sceneVBox.getChildren().addAll(enPreviousLabel, enCurrentLabel, zhCurrentLabel);
    }

    @Override
    public Object doCall() {
        dataIndex = 0;
        dataSize = 30;
        DICTIONARY_LIST.clear();
        DICTIONARY_LIST.addAll(DICTIONARY_SERVICE.queryRandom(dataSize));
        return null;
    }

    @Override
    public void updateUI(Object value) {
        enCurrentLabel.setText(DICTIONARY_LIST.get(dataIndex).getEn());
        zhCurrentLabel.setText(DICTIONARY_LIST.get(dataIndex).getZh());
    }


    @Override
    public void bindEvent() {
        exitButtonEvent();
    }

    @Override
    public void exitButtonEvent() {
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                scheduledService.cancel();
                scheduledService.reset();
                Client.convertScene("com.english.scene.general.MainScene");
            }
        });
    }

    @Override
    public Scene run() {
        initData();
        scheduledService.start();
        return scene;
    }
}
