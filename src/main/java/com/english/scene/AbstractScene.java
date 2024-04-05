package com.english.scene;

import com.english.EnglishAppStart;
import com.english.concurrent.ServiceFunctionExecutor;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

/**
 * @author XYC
 */
public abstract class AbstractScene<T> implements ServiceFunction<T> {
    public static final DictionaryService DICTIONARY_SERVICE = DictionaryServiceImpl.DICTIONARY_SERVICE;
    public static final CorpusService CORPUS_SERVICE = CorpusServiceImpl.CORPUS_SERVICE;
    /**
     * 正则表达式，匹配所有字母与数字的模式，等同于[a-zA-Z0-9]
     */
    public static final Pattern PATTERN = Pattern.compile("\\w+");
    public static final Random RANDOM = new Random();
    protected static final List<Dictionary> DICTIONARY_LIST = new ArrayList<>();
    protected static final List<Corpus> CORPUS_LIST = new ArrayList<>();

    public static int dataSize;
    public static int dataIndex;
    protected Scene scene;

    protected AnchorPane anchorPane;
    public static final Dialog<ButtonType> DIALOG = new Dialog<>();
    public static final Button DIALOG_OK = (Button) DIALOG.getDialogPane().lookupButton(ButtonType.OK);

    static {
        DIALOG.getDialogPane().getButtonTypes().addAll(ButtonType.CANCEL, ButtonType.OK);
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

    /**
     * 加载场景数据的执行器
     */
    private final ServiceFunctionExecutor<T> loadDataExecutor = new ServiceFunctionExecutor<>(this);

    public AbstractScene() {
        initScene();
        bindEvent();
        extend();
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

    public static void setDialog(String title, int width, int height) {
        DIALOG.setTitle(title);
        DIALOG.setWidth(width);
        DIALOG.setHeight(height);
    }

    /**
     * 为场景绑定事件
     */
    public abstract void bindEvent();

    public static Label getLabel(int fontSize) {
        Label label = new Label();
        label.setWrapText(true);
        label.setAlignment(Pos.CENTER);
        label.setFont(Font.font(fontSize));
        return label;
    }

    public static Label getLabel(String text, int fontSize) {
        Label label = getLabel(fontSize);
        label.setText(text);
        return label;
    }

    public static TextField getTextField(int width) {
        TextField textField = new TextField();
        textField.setPrefWidth(width);
        textField.setAlignment(Pos.CENTER);
        return textField;
    }

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

    /**
     * 加载场景数据
     */
    public void loadData() {
        this.loadDataExecutor.restart();
    }

    /**
     * 其他扩展
     */
    public void extend() {
    }

    /**
     * 运行场景
     */
    public Scene run() {
        loadData();
        return scene;
    }

    /**
     * 、
     * 运行场景，传递场景参数
     */
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
                EnglishAppStart.sceneChanger("com.english.scene.general.MainScene");
            }
        });
    }

    public void addExitButton() {
        exitButton = new Button("退出");
        anchorPane.getChildren().add(exitButton);
        AnchorPane.setTopAnchor(exitButton, 8.8);
        AnchorPane.setLeftAnchor(exitButton, 8.8);
    }

    public void addNextButton() {
        nextButton = new Button("继续");
        anchorPane.getChildren().add(nextButton);
        AnchorPane.setBottomAnchor(nextButton, 8.8);
        AnchorPane.setRightAnchor(nextButton, 8.8);
    }

    public void addSceneHBox() {
        sceneHBox = new HBox(20);
        sceneHBox.setAlignment(Pos.CENTER);
        anchorPane.getChildren().add(sceneHBox);
    }

    public void addSceneVBox() {
        sceneVBox = new VBox(36);
        sceneVBox.setAlignment(Pos.CENTER);
        sceneVBox.setStyle("-fx-background-color: rgba(60,83,176,0.68);-fx-pref-width: 378;-fx-pref-height: 266");
        anchorPane.getChildren().add(sceneVBox);
        AnchorPane.setTopAnchor(sceneVBox, 36.6);
    }
}
