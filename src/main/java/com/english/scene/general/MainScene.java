package com.english.scene.general;

import com.english.EnglishAppStart;
import com.english.Utils.StringUtils;
import com.english.entity.Corpus;
import com.english.scene.AbstractScene;
import com.english.scene.event.GameEventHandler;
import com.english.scene.event.InputDataEventHandler;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Data;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XYC
 * 主场景
 */
@Data
public class MainScene extends AbstractScene<Object> {
    private static final TextField search = new TextField();

    private static final Button searchButton = new Button("查词");

    private static final MenuBar functionBar = new MenuBar();
    private static final Menu functionMenu = new Menu("功能");

    private static final Menu wordFunction = new Menu("单词训练");
    private static final MenuItem wordReciteButton = new MenuItem("单词背诵");
    private static final MenuItem wordBrowseButton = new MenuItem("单词浏览");
    private static final MenuItem wordCompletionButton = new MenuItem("单词补全");
    private static final MenuItem readSentenceFillWordButton = new MenuItem("读句填词");

    private static final Menu sentenceFunction = new Menu("语句训练");
    private static final MenuItem sentenceWriteFromMemory = new MenuItem("语句默写");
    private static final Menu gameFunction = new Menu("竞赛");
    private static final MenuItem wordCompletionGameButton = new MenuItem("单词补全竞赛");
    private static final MenuItem wordMeaningSelectionButton = new MenuItem("单词选义竞赛");
    private static final Menu saveFunction = new Menu("导入数据");
    private static final MenuItem inputButton = new MenuItem("导入");
    private static final MenuItem importDictionaryFileButton = new MenuItem("导入词典");
    private static final MenuItem importCorpusFileButton = new MenuItem("导入文集");
    private static final TabPane tabPane = new TabPane();
    private static final EventHandler<Event> importFileHandler = new EventHandler<Event>() {
        @Override
        public void handle(Event event) {
            Stage stage = new Stage();
            List<String> filters = new ArrayList<>();
            filters.add("*.txt");
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("词典文件", filters));
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                if (event.getSource() == importDictionaryFileButton) {
                    DICTIONARY_SERVICE.saveByFile(file.getAbsolutePath());
                } else if (event.getSource() == importCorpusFileButton) {
                    CORPUS_SERVICE.saveByFile(file.getAbsolutePath());
                }
            }
        }
    };

    @Override
    public void initScene() {
        super.initScene();
        search.setPrefWidth(136);

        wordCompletionGameButton.setUserData("com.english.scene.game.WordCompletionGameScene");
        wordMeaningSelectionButton.setUserData("com.english.scene.game.WordMeaningSelectionGameScene");

        functionBar.setStyle("-fx-background-color: #93dc49");
        functionBar.getMenus().add(functionMenu);
        wordFunction.getItems().addAll(wordReciteButton, wordBrowseButton, wordCompletionButton, readSentenceFillWordButton);
        sentenceFunction.getItems().addAll(sentenceWriteFromMemory);
        gameFunction.getItems().addAll(wordCompletionGameButton, wordMeaningSelectionButton);
        saveFunction.getItems().addAll(inputButton, importDictionaryFileButton, importCorpusFileButton);
        functionMenu.getItems().addAll(wordFunction, sentenceFunction, gameFunction, saveFunction);

        addSceneHBox();
        sceneHBox.getChildren().addAll(search, searchButton, functionBar);

        tabPane.setPrefWidth(371);
        tabPane.setPrefHeight(288);
        tabPane.setBackground(new Background(new BackgroundFill(Paint.valueOf("E596A1AF"), new CornerRadii(50), new Insets(10))));
        tabPane.setBorder(new Border(new BorderStroke(Paint.valueOf("384A98FF"), BorderStrokeStyle.DASHED, new CornerRadii(10), new BorderWidths(2))));

        anchorPane.getChildren().add(tabPane);
        AnchorPane.setTopAnchor(sceneHBox, 16.0);
        AnchorPane.setLeftAnchor(sceneHBox, 76.0);
        AnchorPane.setTopAnchor(tabPane, 56.0);
    }

    @Override
    public Object doCall() {
        DICTIONARY_LIST.clear();
        DICTIONARY_LIST.addAll(DICTIONARY_SERVICE.queryRandomAddCorpus(6));
        return null;
    }

    @Override
    public void updateUI(Object value) {
        tabPane.getTabs().clear();
        tabPane.getTabs().addAll(addTab());
    }

    public void bindEvent() {
        searchEvent();
        wordReciteEvent();
        wordBrowseEvent();
        wordCompletionEvent();
        readSentenceFillWordEvent();

        sentenceWriteFromMemoryEvent();

        wordCompletionGameEvent();
        wordMeaningSelectionGameEvent();

        importEvent();
        importDictionaryFileEvent();
        importCorpusFileEvent();
    }

    /**
     * 查词事件
     */
    public void searchEvent() {
        searchButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                String content = search.getText();
                if (StringUtils.hasText(content)) {
                    String zhRegex = "[\u4e00-\u9fa5]+";
                    String zhTextRegex = "[\u4e00-\u9fa5\\w\\pP]+";
                    String enRegex = "[a-zA-Z]+";
                    String enTextRegex = "[\\w\\s\\pP]+";
                    DICTIONARY_LIST.clear();
                    Corpus corpus = null;
                    if (content.matches(zhRegex)) {
                        DICTIONARY_LIST.addAll(DICTIONARY_SERVICE.translate(content, "zh", "en"));
                    } else if (content.matches(enRegex)) {
                        DICTIONARY_LIST.addAll(DICTIONARY_SERVICE.translate(content, "en", "zh"));
                    } else if (content.matches(zhTextRegex)) {
                        corpus = CORPUS_SERVICE.translate(content, "zh", "en");
                    } else if (content.matches(enTextRegex)) {
                        corpus = CORPUS_SERVICE.translate(content, "en", "zh");
                    }
                    if (DICTIONARY_LIST.size() > 0) {
                        tabPane.getTabs().clear();
                        tabPane.getTabs().addAll(addTab());
                    } else if (corpus != null) {
                        tabPane.getTabs().clear();
                        Tab tab = new Tab();
                        ListView<Object> listView = new ListView<>();
                        listView.getItems().add(corpus);
                        tab.setContent(listView);
                        tabPane.getTabs().add(tab);
                    }
                }
            }
        });
    }

    /**
     * 背词事件
     */
    public void wordReciteEvent() {
        wordReciteButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                setDialog("背词", 266, 166);
                TextField inputNumber = new TextField();
                inputNumber.setPrefWidth(88);
                inputNumber.setPromptText("计划词数");
                DIALOG.setGraphic(inputNumber);
                DIALOG_OK.setOnAction(new EventHandler<ActionEvent>() {
                    @Override
                    public void handle(ActionEvent event) {
                        String numberText = inputNumber.getText();
                        // 匹配是否是不为 0 开头的数字
                        if (numberText.matches("^[1-9]\\d*$")) {
                            dataSize = Integer.parseInt(numberText);
                            EnglishAppStart.sceneChanger("com.english.scene.general.word.WordReciteScene");
                        }
                        DIALOG_OK.setOnAction(null);
                        DIALOG.setGraphic(null);
                    }
                });
                DIALOG.show();
            }
        });
    }

    /**
     * 单词浏览事件
     */
    public void wordBrowseEvent() {
        wordBrowseButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                EnglishAppStart.sceneChanger("com.english.scene.general.word.WordBrowseScene");
            }
        });
    }

    /**
     * 单词补全事件
     */
    public void wordCompletionEvent() {
        wordCompletionButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                EnglishAppStart.sceneChanger("com.english.scene.general.word.WordCompletionScene");
            }
        });
    }

    /**
     * 读句填词事件
     */
    public void readSentenceFillWordEvent() {
        readSentenceFillWordButton.setOnAction((event) -> {
            EnglishAppStart.sceneChanger("com.english.scene.general.word.ReadSentenceFillWordScene");
        });
    }


    /**
     * 单词补全竞赛事件
     */
    private void wordCompletionGameEvent() {
        wordCompletionGameButton.setOnAction(GameEventHandler.gameEventHandler);
    }

    /**
     * 单词选义竞赛事件
     */
    public void wordMeaningSelectionGameEvent() {
        wordMeaningSelectionButton.setOnAction(GameEventHandler.gameEventHandler);
    }

    /**
     * 语句默写事件
     */
    public void sentenceWriteFromMemoryEvent() {
        sentenceWriteFromMemory.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                EnglishAppStart.sceneChanger("com.english.scene.general.sentence.SentenceWriteFromMemoryScene");
            }
        });
    }

    /**
     * 导入数据事件
     */
    public void importEvent() {
        inputButton.setOnAction(InputDataEventHandler.inputDataEventHandler);
    }

    /**
     * 导入文件事件
     */
    public void importDictionaryFileEvent() {
        importDictionaryFileButton.setOnAction(importFileHandler::handle);
    }

    /**
     * 导入语句文件事件
     */
    public void importCorpusFileEvent() {
        importCorpusFileButton.setOnAction(importFileHandler::handle);
    }
}
