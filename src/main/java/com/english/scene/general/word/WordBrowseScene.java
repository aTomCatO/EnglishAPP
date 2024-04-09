package com.english.scene.general.word;

import com.english.EnglishAppStart;
import com.english.scene.AbstractScene;
import com.english.util.TextToSpeechUtil;
import javafx.concurrent.ScheduledService;
import javafx.concurrent.Task;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/**
 * @author XYC
 * 单词浏览场景
 */
public class WordBrowseScene extends AbstractScene<Object> {
    private static final String AUDIO_PATH = System.getProperty("user.dir") + "\\audio\\";
    private Path audioPath = null;
    private final Label enPreviousLabel = new Label();
    private final Label enCurrentLabel = new Label();
    private final Label zhCurrentLabel = new Label();

    private final ScheduledService<Integer> scheduledService = new ScheduledService<Integer>() {
        {
            setDelay(Duration.seconds(6));
            setPeriod(Duration.seconds(6));
        }

        @Override
        protected Task<Integer> createTask() {
            return new Task<>() {
                @Override
                protected Integer call() {
                    if (dataIndex == dataSize - 1) {
                        dataIndex = 0;
                        DICTIONARY_LIST.clear();
                        DICTIONARY_LIST.addAll(DICTIONARY_SERVICE.queryRandom(dataSize));
                    } else {
                        dataIndex += 1;
                    }
                    // 预备当前单词的音频
                    prepareAudio(DICTIONARY_LIST.get(dataIndex).getEn());
                    return dataIndex;
                }

                @Override
                protected void updateValue(Integer index) {
                    if (index > 0) {
                        enPreviousLabel.setText(DICTIONARY_LIST.get(index - 1).getEn());
                    }
                    enCurrentLabel.setText(DICTIONARY_LIST.get(index).getEn());
                    zhCurrentLabel.setText(DICTIONARY_LIST.get(index).getZh());
                    playMedia();
                }
            };
        }
    };

    @Override
    public void initScene() {
        super.initScene();

        enPreviousLabel.setFont(Font.font(18));
        enCurrentLabel.setFont(Font.font(28));
        zhCurrentLabel.setFont(Font.font(23));

        addExitButton();
        addSceneVBox();
        sceneVBox.getChildren().addAll(enPreviousLabel, enCurrentLabel, zhCurrentLabel);
    }

    @Override
    public Object doCall() {
        dataSize = 30;
        dataIndex = 0;
        DICTIONARY_LIST.clear();
        DICTIONARY_LIST.addAll(DICTIONARY_SERVICE.queryRandom(dataSize));
        // 预备当前单词的音频
        prepareAudio(DICTIONARY_LIST.get(dataIndex).getEn());
        return null;
    }

    @Override
    public void updateUI(Object value) {
        enCurrentLabel.setText(DICTIONARY_LIST.get(dataIndex).getEn());
        zhCurrentLabel.setText(DICTIONARY_LIST.get(dataIndex).getZh());
        playMedia();
    }

    @Override
    public void bindEvent() {
        exitButtonEvent();
    }

    @Override
    public void exitButtonEvent() {
        exitButton.setOnAction(event -> {
            scheduledService.cancel();
            scheduledService.reset();
            EnglishAppStart.convertScene("com.english.scene.general.MainScene");
        });
    }

    @Override
    public Scene run() {
        loadData();
        scheduledService.start();
        return scene;
    }

    /**
     * 预备音频
     *
     * @param audioName 音频文件名
     */
    public void prepareAudio(String audioName) {
        String filePath = AUDIO_PATH + audioName + ".mp3";
        audioPath = Paths.get(filePath);
        if (!Files.exists(audioPath)) {
            if (!TextToSpeechUtil.toSpeech(audioName, filePath)) {
                audioPath = null;
            }
        }
    }

    /**
     * 播放音频
     */
    public void playMedia() {
        if (audioPath != null) {
            URI uri = audioPath.toUri();
            Media media = new Media(uri.toString());
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        }
    }
}
