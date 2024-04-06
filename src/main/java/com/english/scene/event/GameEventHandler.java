package com.english.scene.event;

import com.english.EnglishAppStart;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

import static com.english.scene.AbstractScene.*;

/**
 * @author XYC
 */
public class GameEventHandler implements EventHandler<ActionEvent> {
    public static final GameEventHandler gameEventHandler = new GameEventHandler();
    private final OkActionEvent okActionEvent = new OkActionEvent();
    /**
     * 布局
     */
    private final GridPane gridPane = new GridPane();
    /**
     * 题数输入组件
     */
    private final TextField inputSize = new TextField();
    /**
     * 时长选择器
     */
    private final ChoiceBox<Integer> durationChoice = new ChoiceBox<>();

    private ActionEvent eventTarget;

    private GameEventHandler() {
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setVgap(6.6);

        Label sizeLabel = getLabel(26);
        sizeLabel.setText("词数: ");
        inputSize.setPrefWidth(88);
        inputSize.setPromptText("计划词数");
        inputSize.setText("10");

        Label durationLabel = getLabel(26);
        durationLabel.setText("时长: ");
        List<Integer> items = new ArrayList<>();
        for (int i = 1; i < 7; i++) {
            items.add(i);
        }
        durationChoice.getItems().addAll(items);
        durationChoice.setValue(1);

        gridPane.add(sizeLabel, 1, 0);
        gridPane.add(inputSize, 2, 0);
        gridPane.add(durationLabel, 1, 2);
        gridPane.add(durationChoice, 2, 2);
    }

    @Override
    public void handle(ActionEvent event) {
        eventTarget = event;
        DIALOG_OK.setOnAction(okActionEvent);
        setDialog("竞赛", 266, 166);
        DIALOG.setGraphic(gridPane);
        DIALOG.show();
    }


    class OkActionEvent implements EventHandler<ActionEvent> {
        @Override
        public void handle(ActionEvent event) {
            String size = inputSize.getText();
            // 匹配是否是不为 0 开头的数字
            if (size.matches("^[1-9]\\d*$")) {
                dataSize = Integer.parseInt(size);
                Integer duration = durationChoice.getValue() * 60;

                // 获取事件源
                MenuItem menuItem = (MenuItem) eventTarget.getSource();
                String sceneName = (String) menuItem.getUserData();
                EnglishAppStart.convertScene(sceneName, duration);
            }
            DIALOG_OK.setOnAction(null);
            DIALOG.setGraphic(null);
        }
    }
}
