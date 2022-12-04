package com.english.scene.game;

import com.english.EnglishAppStart;
import com.english.scheduled_service.GameCountDownScheduledService;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;

import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

/**
 * @author XYC
 * 单词选义竞赛场景
 */
public class SelectMeanByWordGameScene extends AbstractGameScene {

    private final Service<Object> service = new UpdateService();
    /**
     * 显示先前的单词的 Label
     */
    private Label enPreviousLabel;
    /**
     * 显示当前的单词的 Label
     */
    private Label enCurrentLabel;
    /**
     * 以下 4 个 Label 是显示中文翻译的4个选项
     */
    private Label zhSelectLabel1;
    private Label zhSelectLabel2;
    private Label zhSelectLabel3;
    private Label zhSelectLabel4;
    /**
     * 不显示，用来指向正确的 Label 选项
     */
    private Label zhAccurateSelectLabel;
    private VBox vBox1;
    private VBox vBox2;
    private BorderPane borderPane;
    private boolean doPressed = true;

    {
        this.sceneName = "单词选义竞赛场景";
    }

    @Override
    public void initScene() {
        super.initScene();

        addExitButton();
        addVBoxMain();

        gameCountDownScheduledService = GameCountDownScheduledService.getScheduledService();

        enPreviousLabel = new Label();
        enCurrentLabel = new Label();
        zhSelectLabel1 = new Label();
        zhSelectLabel2 = new Label();
        zhSelectLabel3 = new Label();
        zhSelectLabel4 = new Label();
        countDownLabel = gameCountDownScheduledService.getCountDownLabel();

        //countDownLabel.setFont(Font.font(13));
        this.anchorPane.getChildren().add(countDownLabel);
        AnchorPane.setTopAnchor(countDownLabel, 8.8);
        AnchorPane.setRightAnchor(countDownLabel, 8.8);

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

        vBox1 = new VBox(26);
        vBox2 = new VBox(26);

        vBox1.getChildren().addAll(zhSelectLabel1, zhSelectLabel3);
        vBox2.getChildren().addAll(zhSelectLabel2, zhSelectLabel4);

        borderPane = new BorderPane();
        borderPane.setLeft(vBox1);
        borderPane.setRight(vBox2);

        vBoxMain.getChildren().addAll(enPreviousLabel, enCurrentLabel, borderPane);
    }

    @Override
    public void initData() {
        dataIndex = 0;
        correctCount = 0;
        dictionaryList.clear();
        dictionaryList.addAll(dictionaryService.queryRandom(dataSize));
        updateQuestion();
    }

    public void updateQuestion() {
        String en = dictionaryList.get(dataIndex).getEn();
        String zh = dictionaryList.get(dataIndex).getZh();
        enCurrentLabel.setText(en);
        System.out.println(zh);

        int zhAccurateLabelIndex = RANDOM.nextInt(4) + 1;
        TreeSet<Integer> wrongIndexSet = generateWrongIndex();
        switch (zhAccurateLabelIndex) {
            case 1: {
                zhSelectLabel1.setText(zh);
                zhAccurateSelectLabel = zhSelectLabel1;

                zhSelectLabel2.setText(dictionaryList.get(wrongIndexSet.pollFirst()).getZh());
                zhSelectLabel3.setText(dictionaryList.get(wrongIndexSet.pollFirst()).getZh());
                zhSelectLabel4.setText(dictionaryList.get(wrongIndexSet.pollFirst()).getZh());
                break;
            }
            case 2: {
                zhSelectLabel2.setText(zh);
                zhAccurateSelectLabel = zhSelectLabel2;

                zhSelectLabel1.setText(dictionaryList.get(wrongIndexSet.pollFirst()).getZh());
                zhSelectLabel3.setText(dictionaryList.get(wrongIndexSet.pollFirst()).getZh());
                zhSelectLabel4.setText(dictionaryList.get(wrongIndexSet.pollFirst()).getZh());
                break;
            }
            case 3: {
                zhSelectLabel3.setText(zh);
                zhAccurateSelectLabel = zhSelectLabel3;

                zhSelectLabel1.setText(dictionaryList.get(wrongIndexSet.pollFirst()).getZh());
                zhSelectLabel2.setText(dictionaryList.get(wrongIndexSet.pollFirst()).getZh());
                zhSelectLabel4.setText(dictionaryList.get(wrongIndexSet.pollFirst()).getZh());
                break;
            }
            default: {
                zhSelectLabel4.setText(zh);
                zhAccurateSelectLabel = zhSelectLabel4;

                zhSelectLabel1.setText(dictionaryList.get(wrongIndexSet.pollFirst()).getZh());
                zhSelectLabel2.setText(dictionaryList.get(wrongIndexSet.pollFirst()).getZh());
                zhSelectLabel3.setText(dictionaryList.get(wrongIndexSet.pollFirst()).getZh());
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
                gameCountDownScheduledService.cancel();
                //gameCountDownScheduledService.reset();


                enPreviousLabel.setText(null);
                releaseNode();
                EnglishAppStart.convertScene("主场景");
            }
        });
    }

    public void sceneKeyPressedEvent() {
        scene.setOnKeyPressed(keyEvent -> {
            if (doPressed) {
                doPressed = false;
                switch (keyEvent.getCode().getName()) {
                    case "A": {
                        if (!isCorrect(zhSelectLabel1)) {
                            zhAccurateSelectLabel.setStyle("-fx-background-color: #75de6f");
                        }
                        break;
                    }
                    case "B": {
                        if (!isCorrect(zhSelectLabel2)) {
                            zhAccurateSelectLabel.setStyle("-fx-background-color: #75de6f");
                        }
                        break;
                    }
                    case "C": {
                        if (!isCorrect(zhSelectLabel3)) {
                            zhAccurateSelectLabel.setStyle("-fx-background-color: #75de6f");
                        }
                        break;
                    }
                    case "D": {
                        if (!isCorrect(zhSelectLabel4)) {
                            zhAccurateSelectLabel.setStyle("-fx-background-color: #75de6f");
                        }
                        break;
                    }
                    default: {
                        doPressed = true;
                        return;
                    }
                }
                service.restart();
            }
        });
    }

    public boolean isCorrect(Label zhChooseLabel) {
        if (dictionaryList.get(dataIndex).getZh().equals(zhChooseLabel.getText())) {
            correctCount += 1;
            zhChooseLabel.setStyle("-fx-background-color: #75de6f");
            return true;
        }
        zhChooseLabel.setStyle("-fx-background-color: #d24f76");
        return false;
    }

    @Override
    public Scene run(Object... args) {
        this.gameDuration = (Integer) args[0];

        doPressed = true;
        gameCountDownScheduledService.setRemainTime(gameDuration);
        gameCountDownScheduledService.restart();
        return super.run();
    }

    class UpdateService extends Service<Object> {
        @Override
        protected Task<Object> createTask() {
            return new Task<Object>() {
                @Override
                protected Object call() throws Exception {
                    try {
                        //UI线程暂停2秒，以便让用户看到选择的对、错
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException ignored) {
                    }
                    return null;
                }

                @Override
                protected void updateValue(Object value) {
                    super.updateValue(value);
                    enPreviousLabel.setText(dictionaryList.get(dataIndex).getEn());

                    zhSelectLabel1.setStyle(null);
                    zhSelectLabel2.setStyle(null);
                    zhSelectLabel3.setStyle(null);
                    zhSelectLabel4.setStyle(null);

                    dataIndex += 1;
                    if (dataIndex == dataSize) {
                        gameEnd();
                        return;
                    }

                    doPressed = true;
                    updateQuestion();
                }
            };
        }
    }
}
