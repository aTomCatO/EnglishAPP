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
public class WordShowScene extends AbstractScene {
    private FlowPane flowPane;
    private ListView<String> enListView;
    private ListView<String> zhListView;

    {
        this.sceneName = "单词展示场景";
    }

    @Override
    public void initScene() {
        super.initScene();

        addExitButton();

        flowPane = new FlowPane();

        enListView = new ListView<>();
        zhListView = new ListView<>();
        enListView.setPrefWidth(194);
        zhListView.setPrefWidth(194);

        flowPane.getChildren().addAll(enListView, zhListView);

        anchorPane.getChildren().add(flowPane);
        AnchorPane.setTopAnchor(flowPane, 36.6);
    }

    @Override
    public void initData() {
        for (int i = 0; i < dataSize; i++) {
            enListView.getItems().add(dictionaryList.get(i).getEn());
            zhListView.getItems().add(dictionaryList.get(i).getZh());
        }
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
            releaseNode();
            EnglishAppStart.convertScene("主场景");
        });
    }
}
