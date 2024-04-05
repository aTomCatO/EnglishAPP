package com.english.scene.game;

import com.english.EnglishAppStart;
import com.english.Utils.InstanceUtils;
import com.english.service.BaseService;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import lombok.extern.slf4j.Slf4j;

import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * @author XYC
 * 单词选义竞赛场景
 */
@Slf4j
public class WordMeaningSelectionGameScene extends CountdownScene<Object> {
    /**
     * 显示先前的单词的 Label
     */
    private static final Label enPreviousLabel = new Label();
    /**
     * 显示当前的单词的 Label
     */
    private static final Label enCurrentLabel = new Label();
    /**
     * 以下 4 个 Label 是显示中文翻译的4个选项
     */
    private static final Label zhSelectLabel1 = new Label();
    private static final Label zhSelectLabel2 = new Label();
    private static final Label zhSelectLabel3 = new Label();
    private static final Label zhSelectLabel4 = new Label();
    /**
     * 不显示，用来指向正确的 Label 选项
     */
    private Label zhAccurateSelectLabel;
    private static final VBox vBox1 = new VBox(26);
    private static final VBox vBox2 = new VBox(26);
    private static final BorderPane borderPane = new BorderPane();
    private UpdateUiTask updateUiTask;
    private boolean doPressed = true;

    @Override
    public void initScene() {
        super.initScene();

        addExitButton();
        addSceneVBox();

        enPreviousLabel.setFont(Font.font(18));
        enCurrentLabel.setFont(Font.font(26));
        zhSelectLabel1.setFont(Font.font(16));
        zhSelectLabel2.setFont(Font.font(16));
        zhSelectLabel3.setFont(Font.font(16));
        zhSelectLabel4.setFont(Font.font(16));

        //为选项 Label 添加边框
        zhSelectLabel1.setBorder(new Border(new BorderStroke(Paint.valueOf("384A98FF"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));
        zhSelectLabel2.setBorder(new Border(new BorderStroke(Paint.valueOf("384A98FF"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));
        zhSelectLabel3.setBorder(new Border(new BorderStroke(Paint.valueOf("384A98FF"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));
        zhSelectLabel4.setBorder(new Border(new BorderStroke(Paint.valueOf("384A98FF"), BorderStrokeStyle.SOLID, new CornerRadii(10), new BorderWidths(2))));

        vBox1.getChildren().addAll(zhSelectLabel1, zhSelectLabel3);
        vBox2.getChildren().addAll(zhSelectLabel2, zhSelectLabel4);

        borderPane.setLeft(vBox1);
        borderPane.setRight(vBox2);

        sceneVBox.getChildren().addAll(enPreviousLabel, enCurrentLabel, borderPane);
    }

    @Override
    public void extend() {
        addCountdown();
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
    public void updateUI(Object value) {
        updateQuestion();
    }

    public void updateQuestion() {
        String en = DICTIONARY_LIST.get(dataIndex).getEn();
        String zh = DICTIONARY_LIST.get(dataIndex).getZh();
        enCurrentLabel.setText(en);
        InstanceUtils.LOGGER.info(zh);

        int zhAccurateLabelIndex = RANDOM.nextInt(4) + 1;
        TreeSet<Integer> wrongIndexSet = generateWrongIndex();
        switch (zhAccurateLabelIndex) {
            case 1: {
                zhSelectLabel1.setText(zh);
                zhAccurateSelectLabel = zhSelectLabel1;

                zhSelectLabel2.setText(DICTIONARY_LIST.get(wrongIndexSet.pollFirst()).getZh());
                zhSelectLabel3.setText(DICTIONARY_LIST.get(wrongIndexSet.pollFirst()).getZh());
                zhSelectLabel4.setText(DICTIONARY_LIST.get(wrongIndexSet.pollFirst()).getZh());
                break;
            }
            case 2: {
                zhSelectLabel2.setText(zh);
                zhAccurateSelectLabel = zhSelectLabel2;

                zhSelectLabel1.setText(DICTIONARY_LIST.get(wrongIndexSet.pollFirst()).getZh());
                zhSelectLabel3.setText(DICTIONARY_LIST.get(wrongIndexSet.pollFirst()).getZh());
                zhSelectLabel4.setText(DICTIONARY_LIST.get(wrongIndexSet.pollFirst()).getZh());
                break;
            }
            case 3: {
                zhSelectLabel3.setText(zh);
                zhAccurateSelectLabel = zhSelectLabel3;

                zhSelectLabel1.setText(DICTIONARY_LIST.get(wrongIndexSet.pollFirst()).getZh());
                zhSelectLabel2.setText(DICTIONARY_LIST.get(wrongIndexSet.pollFirst()).getZh());
                zhSelectLabel4.setText(DICTIONARY_LIST.get(wrongIndexSet.pollFirst()).getZh());
                break;
            }
            default: {
                zhSelectLabel4.setText(zh);
                zhAccurateSelectLabel = zhSelectLabel4;

                zhSelectLabel1.setText(DICTIONARY_LIST.get(wrongIndexSet.pollFirst()).getZh());
                zhSelectLabel2.setText(DICTIONARY_LIST.get(wrongIndexSet.pollFirst()).getZh());
                zhSelectLabel3.setText(DICTIONARY_LIST.get(wrongIndexSet.pollFirst()).getZh());
                break;
            }
        }
    }

    public TreeSet<Integer> generateWrongIndex() {
        TreeSet<Integer> wrongIndexSet = new TreeSet<>();
        for (int i = 0; i < 3; i++) {
            int zhWrongIndex = RANDOM.nextInt(dataSize);
            while (zhWrongIndex == dataIndex || !wrongIndexSet.add(zhWrongIndex)) {
                zhWrongIndex = RANDOM.nextInt(dataSize);
            }
        }
        return wrongIndexSet;
    }

    @Override
    public void bindEvent() {
        exitButtonEvent();
        sceneKeyPressedEvent();
    }

    @Override
    public void exitButtonEvent() {
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (updateUiTask != null && updateUiTask.isRunning()) {
                    updateUiTask.cancel();
                }
                countdownExecutor.cancel();
                enPreviousLabel.setText(null);
                EnglishAppStart.convertScene("com.english.scene.general.MainScene");
            }
        });
    }

    public void sceneKeyPressedEvent() {
        scene.setOnKeyPressed(keyEvent -> {
            if (doPressed) {
                doPressed = false;
                switch (keyEvent.getCode().getName()) {
                    case "A": {
                        assessAnswer(zhSelectLabel1);
                        break;
                    }
                    case "B": {
                        assessAnswer(zhSelectLabel2);
                        break;
                    }
                    case "C": {
                        assessAnswer(zhSelectLabel3);
                        break;
                    }
                    case "D": {
                        assessAnswer(zhSelectLabel4);
                        break;
                    }
                    default: {
                        doPressed = true;
                        break;
                    }
                }
            }
        });
    }

    public void assessAnswer(Label zhChooseLabel) {
        if (DICTIONARY_LIST.get(dataIndex).getZh().equals(zhChooseLabel.getText())) {
            correctCount += 1;
            zhChooseLabel.setStyle("-fx-background-color: #75de6f");
        } else {
            zhChooseLabel.setStyle("-fx-background-color: #d24f76");
            zhAccurateSelectLabel.setStyle("-fx-background-color: #75de6f");
        }
        updateUiTask = new UpdateUiTask();
        BaseService.THREAD_POOL.execute(updateUiTask);
    }

    @Override
    public Scene run(Object... args) {
        int duration = (Integer) args[0];
        loadData();
        doPressed = true;
        countdownExecutor.setCountdownScene(this);
        countdownExecutor.setDuration(duration);
        countdownExecutor.restart();
        return scene;
    }

    /**
     * 与FutureTask一样，Task是一个一次性类，其实例对象不能重复使用。
     */
    class UpdateUiTask extends Task<Object> {

        @Override
        protected Object call() throws Exception {
            //2秒后刷新UI
            TimeUnit.SECONDS.sleep(2);
            return null;
        }

        @Override
        protected void updateValue(Object value) {
            enPreviousLabel.setText(DICTIONARY_LIST.get(dataIndex).getEn());

            zhSelectLabel1.setStyle(null);
            zhSelectLabel2.setStyle(null);
            zhSelectLabel3.setStyle(null);
            zhSelectLabel4.setStyle(null);

            dataIndex += 1;
            if (dataIndex == dataSize) {
                countdownEnd();
                return;
            }

            doPressed = true;
            updateQuestion();
        }
    }
}
