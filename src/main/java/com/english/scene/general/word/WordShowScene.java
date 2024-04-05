package com.english.scene.general.word;

import com.english.EnglishAppStart;
import com.english.scene.AbstractScene;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;

/**
 * @author XYC
 * 单词展示场景
 */
public class WordShowScene extends AbstractScene<Object> {
    private static final FlowPane flowPane = new FlowPane();
    private static final ListView<String> enListView = new ListView<>();
    private static final ListView<String> zhListView = new ListView<>();

    @Override
    public void initScene() {
        super.initScene();

        addExitButton();

        enListView.setPrefWidth(194);
        zhListView.setPrefWidth(194);

        flowPane.getChildren().addAll(enListView, zhListView);

        anchorPane.getChildren().add(flowPane);
        AnchorPane.setTopAnchor(flowPane, 36.6);
    }

    @Override
    public void loadData() {
        for (int i = 0; i < dataSize; i++) {
            enListView.getItems().add(DICTIONARY_LIST.get(i).getEn());
            zhListView.getItems().add(DICTIONARY_LIST.get(i).getZh());
        }
    }

    @Override
    public Object doCall() {

        return null;
    }

    @Override
    public void updateUI(Object value) {

    }

    @Override
    public void bindEvent() {
        exitButtonEvent();
    }

    @Override
    public void exitButtonEvent() {
        exitButton.setOnAction(event -> {
            enListView.getItems().clear();
            zhListView.getItems().clear();
            EnglishAppStart.sceneChanger("com.english.scene.general.MainScene");
        });
    }
}
