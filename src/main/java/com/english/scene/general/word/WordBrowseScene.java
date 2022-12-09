package com.english.scene.general.word;

import com.english.EnglishAppStart;
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
public class WordBrowseScene extends AbstractScene {
    private Label enPreviousLabel;
    private Label enCurrentLabel;
    private Label zhCurrentLabel;

    private final ScheduledService<Integer> scheduledService = new ScheduledService<Integer>() {
        @Override
        protected Task<Integer> createTask() {
            return new Task<Integer>() {
                @Override
                protected Integer call() throws Exception {
                    if (dataIndex == dataSize - 1) {
                        dataIndex = 0;
                        dictionaryList = DICTIONARY_SERVICE.queryRandom(dataSize);
                    }
                    return dataIndex += 1;
                }

                @Override
                protected void updateValue(Integer index) {
                    super.updateValue(index);
                    enPreviousLabel.setText(dictionaryList.get(index - 1).getEn());
                    enCurrentLabel.setText(dictionaryList.get(index).getEn());
                    zhCurrentLabel.setText(dictionaryList.get(index).getZh());
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
    public void initData() {
        dataSize = 30;
        dataIndex = 0;
        dictionaryList = DICTIONARY_SERVICE.queryRandom(dataSize);

        enCurrentLabel.setText(dictionaryList.get(dataIndex).getEn());
        zhCurrentLabel.setText(dictionaryList.get(dataIndex).getZh());
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
                EnglishAppStart.convertScene("MainScene");
            }
        });
    }

    @Override
    public Scene run() {
        scheduledService.setDelay(Duration.seconds(6));
        scheduledService.setPeriod(Duration.seconds(6));
        scheduledService.start();
        initData();
        return scene;
    }
}
