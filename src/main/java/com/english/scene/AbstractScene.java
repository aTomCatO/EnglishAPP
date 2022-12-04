package com.english.scene;

import com.english.EnglishAppStart;
import com.english.javaBeans.Corpus;
import com.english.javaBeans.Dictionary;
import com.english.node_pool.AbstractNodeAccessor;
import com.english.service.CorpusService;
import com.english.service.CorpusServiceImpl;
import com.english.service.DictionaryService;
import com.english.service.DictionaryServiceImpl;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author XYC
 */
public abstract class AbstractScene extends AbstractNodeAccessor {
    public static final Random RANDOM = new Random();
    public static int dataSize;
    public static int dataIndex;
    protected static DictionaryService dictionaryService = DictionaryServiceImpl.DICTIONARY_SERVICE;
    protected static CorpusService corpusService = CorpusServiceImpl.CORPUS_SERVICE;
    protected static List<Dictionary> dictionaryList = new ArrayList<>();
    protected static List<Corpus> corpusList = new ArrayList<>();
    protected static Dialog<ButtonType> dialog = new Dialog<>();

    static {
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.NO, ButtonType.OK);
        dialog.initOwner(EnglishAppStart.primaryStage);
        dialog.initModality(Modality.WINDOW_MODAL);
    }

    public final Pattern pattern = Pattern.compile("[\\w]+");
    public String sceneName;
    protected Scene scene;
    protected AnchorPane anchorPane;
    protected HBox hBoxMain;
    protected VBox vBoxMain;
    /**
     * 退出按钮
     */
    protected Button exitButton;
    /**
     * 继续按钮
     */
    protected Button nextButton;


    public AbstractScene() {
        this.initScene();
        this.bindEvent();
    }

    public static List<Corpus> getCorpusList() {
        return corpusList;
    }

    public static Dialog<ButtonType> getDialog(String title, int width, int height) {
        dialog.setTitle(title);
        dialog.setWidth(width);
        dialog.setHeight(height);
        return dialog;
    }

    public static List<Tab> addTab() {
        List<Tab> tabList = new ArrayList<>();
        for (int i = 0; i < dictionaryList.size(); i++) {
            Dictionary dictionary = dictionaryList.get(i);
            Tab tab = new Tab(dictionary.getEn());

            ListView<Object> listView = new ListView<>();
            listView.getItems().add(dictionary.getZh());
            tab.setContent(listView);
            List<Corpus> list = dictionary.getCorpusList();
            if (list != null) {
                corpusList.clear();
                corpusList.addAll(list);
                listView.getItems().addAll(corpusList);
            }
            tabList.add(tab);
        }
        return tabList;
    }

    public void initScene() {
        anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: pink");
        scene = new Scene(anchorPane);
    }

    /**
     * 初始化场景数据
     */
    public abstract void initData();

    /**
     * 为场景绑定事件
     */
    public abstract void bindEvent();

    /**
     * 默认的退出按钮事件（回到主场景）
     */
    public void exitButtonEvent() {
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                EnglishAppStart.convertScene("主场景");
            }
        });
    }

    public void addHBoxMain() {
        this.hBoxMain = new HBox(20);
        this.hBoxMain.setAlignment(Pos.CENTER);
        this.anchorPane.getChildren().add(hBoxMain);
    }

    public void addVBoxMain() {
        this.vBoxMain = new VBox(36);
        this.vBoxMain.setAlignment(Pos.CENTER);
        this.vBoxMain.setStyle("-fx-background-color: rgba(60,83,176,0.68);-fx-pref-width: 378;-fx-pref-height: 266");
        this.anchorPane.getChildren().add(vBoxMain);
        AnchorPane.setTopAnchor(vBoxMain, 36.6);
    }

    public void addExitButton() {
        this.exitButton = new Button("退出");
        this.anchorPane.getChildren().add(exitButton);
        AnchorPane.setTopAnchor(exitButton, 8.8);
        AnchorPane.setLeftAnchor(exitButton, 8.8);
    }

    public void addNextButton() {
        this.nextButton = new Button("继续");
        this.anchorPane.getChildren().add(nextButton);
        AnchorPane.setBottomAnchor(nextButton, 8.8);
        AnchorPane.setRightAnchor(nextButton, 8.8);
    }

    public Scene run() {
        initData();
        System.out.println(sceneName);
        return this.scene;
    }

    public Scene run(Object... args) {
        return run();
    }
}
