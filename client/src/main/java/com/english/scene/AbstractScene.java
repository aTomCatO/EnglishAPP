package com.english.scene;

import com.english.Client;
import com.english.concurrent.ServiceFunctionHandler;
import com.english.entity.Corpus;
import com.english.entity.Dictionary;
import com.english.function.ServiceFunction;
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
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author XYC
 */
public abstract class AbstractScene<T> implements ServiceFunction<T> {
    protected static final DictionaryService DICTIONARY_SERVICE = DictionaryServiceImpl.DICTIONARY_SERVICE;
    protected static final CorpusService CORPUS_SERVICE = CorpusServiceImpl.CORPUS_SERVICE;
    public static final Pattern PATTERN = Pattern.compile("\\w+");
    public static final Random RANDOM = new Random();
    protected static final Logger LOGGER = LoggerFactory.getLogger(AbstractScene.class);
    public static final List<Dictionary> DICTIONARY_LIST = new ArrayList<>();
    public static final List<Corpus> CORPUS_LIST = new ArrayList<>();

    public static int dataSize;
    public static int dataIndex;
    protected Scene scene;
    protected AnchorPane anchorPane;
    protected static final Dialog<ButtonType> MAIN_DIALOG = new Dialog<>();

    static {
        MAIN_DIALOG.getDialogPane().getButtonTypes().addAll(ButtonType.NO, ButtonType.OK);
    }

    /**
     * 退出按钮
     */
    protected Button exitButton;
    /**
     * 继续按钮
     */
    protected Button nextButton;

    protected HBox sceneHBox;
    protected VBox sceneVBox;

    private ServiceFunctionHandler<T> initDataHandler = new ServiceFunctionHandler<>(this);

    public AbstractScene() {
        this.initScene();
        this.bindEvent();
    }

    public static void setDictionaryListData(List<Dictionary> dictionaryList) {
        DICTIONARY_LIST.clear();
        DICTIONARY_LIST.addAll(dictionaryList);
    }

    public static void setCorpusListData(List<Corpus> corpusList) {
        CORPUS_LIST.clear();
        CORPUS_LIST.addAll(corpusList);
    }

    /**
     * 初始化场景界面
     * 控件对象在这里创建
     */
    public void initScene() {
        anchorPane = new AnchorPane();
        anchorPane.setStyle("-fx-background-color: pink");
        scene = new Scene(anchorPane);
    }

    /**
     * 初始化场景数据
     */
    public void initData() {
        this.initDataHandler.restart();
    }

    /**
     * 为场景绑定事件
     */
    public abstract void bindEvent();

    public static List<Tab> addTab() {
        List<Tab> tabList = new ArrayList<>();
        for (int i = 0; i < DICTIONARY_LIST.size(); i++) {
            Dictionary dictionary = DICTIONARY_LIST.get(i);
            Tab tab = new Tab(dictionary.getEn());

            ListView<Object> listView = new ListView<>();
            tab.setContent(listView);

            listView.getItems().add(dictionary.getZh());
            List<Corpus> dictionaryCorpusList = dictionary.getCorpusList();
            if (dictionaryCorpusList != null && dictionaryCorpusList.size() > 0) {
                listView.getItems().addAll(dictionaryCorpusList);
            }
            tabList.add(tab);
        }
        return tabList;
    }

    public static void setMainDialog(String title, int width, int height) {
        MAIN_DIALOG.setTitle(title);
        MAIN_DIALOG.setWidth(width);
        MAIN_DIALOG.setHeight(height);
    }


    public Scene run() {
        initData();
        return this.scene;
    }

    public Scene run(Object... args) {
        return run();
    }

    /**
     * 默认的退出按钮事件（回到主场景）
     */
    public void exitButtonEvent() {
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                Client.convertScene("com.english.scene.general.MainScene");
            }
        });
    }


    public void addSceneHBox() {
        this.sceneHBox = new HBox(20);
        this.sceneHBox.setAlignment(Pos.CENTER);
        this.anchorPane.getChildren().add(sceneHBox);
    }

    public void addSceneVBox() {
        this.sceneVBox = new VBox(36);
        this.sceneVBox.setAlignment(Pos.CENTER);
        this.sceneVBox.setStyle("-fx-background-color: rgba(60,83,176,0.68);-fx-pref-width: 378;-fx-pref-height: 266");
        this.anchorPane.getChildren().add(sceneVBox);
        AnchorPane.setTopAnchor(sceneVBox, 36.6);
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

    public TextField getTextField(int width) {
        TextField textField = new TextField();
        textField.setPrefWidth(width);
        textField.setAlignment(Pos.CENTER);
        return textField;
    }

    public Label getLabel(int fontSize) {
        Label label = new Label();
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font(fontSize));
        return label;
    }
}
