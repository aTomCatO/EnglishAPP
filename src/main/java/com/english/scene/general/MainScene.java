package com.english.scene.general;

import com.english.EnglishAppStart;
import com.english.entity.Corpus;
import com.english.entity.Dictionary;
import com.english.scene.AbstractScene;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Paint;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author XYC
 * 主场景
 */
@Data
public class MainScene extends AbstractScene {

    private TextField search;

    private Button searchButton;

    private MenuBar functionBar;
    private Menu functionMenu;

    private Menu wordFunction;
    private MenuItem wordReciteButton;
    private MenuItem wordBrowseButton;
    private MenuItem completeWordByFillButton;
    private MenuItem readSentenceFillWordButton;

    private Menu sentenceFunction;
    private MenuItem sentenceWriteFromMemory;
    private Menu gameFunction;
    private MenuItem completeWordByFillGameButton;
    private MenuItem selectMeanByWordGameButton;
    private Menu saveFunction;
    private MenuItem inputButton;
    private MenuItem inputDictionaryFileButton;
    private MenuItem inputCorpusFileButton;
    private TabPane tabPane;
    private EventHandler<Event> gameEventHandler;
    private EventHandler<Event> inputFileHandler;
    @Override
    public void initScene() {
        super.initScene();

        //进行场景基本组件实例化
        search = new TextField();
        search.setPrefWidth(136);
        searchButton = new Button("查词");
        functionBar = new MenuBar();
        functionMenu = new Menu("功能");

        wordFunction = new Menu("单词训练");
        wordReciteButton = new MenuItem("单词背诵");
        wordBrowseButton = new MenuItem("单词浏览");
        completeWordByFillButton = new MenuItem("单词补全");
        readSentenceFillWordButton = new MenuItem("读句填词");

        sentenceFunction = new Menu("语句训练");
        sentenceWriteFromMemory = new MenuItem("语句默写");

        gameFunction = new Menu("竞赛");
        completeWordByFillGameButton = new MenuItem("单词补全竞赛");
        selectMeanByWordGameButton = new MenuItem("单词选义竞赛");

        saveFunction = new Menu("导入数据");
        inputButton = new MenuItem("导入");
        inputDictionaryFileButton = new MenuItem("导入词典");
        inputCorpusFileButton = new MenuItem("导入文集");

        tabPane = new TabPane();

        functionBar.setStyle("-fx-background-color: #93dc49");
        functionBar.getMenus().add(functionMenu);
        wordFunction.getItems().addAll(wordReciteButton, wordBrowseButton, completeWordByFillButton, readSentenceFillWordButton);
        sentenceFunction.getItems().addAll(sentenceWriteFromMemory);
        gameFunction.getItems().addAll(completeWordByFillGameButton, selectMeanByWordGameButton);
        saveFunction.getItems().addAll(inputButton, inputDictionaryFileButton, inputCorpusFileButton);
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

    public void initData() {
        DICTIONARY_LIST.clear();
        DICTIONARY_LIST.addAll(DICTIONARY_SERVICE.queryRandomAddCorpus(6));
        tabPane.getTabs().clear();
        tabPane.getTabs().addAll(addTab());
    }

    public void bindEvent() {
        searchEvent();
        wordReciteEvent();
        wordBrowseEvent();
        completeWordByFillEvent();
        readSentenceFillWordEvent();

        sentenceWriteFromMemoryEvent();

        gameEventHandler = new EventHandler<Event>() {
            @Override
            public void handle(Event eventTarget) {
                //自定义竞赛时长
                List<Integer> items = new ArrayList<>();
                for (int i = 1; i < 7; i++) {
                    items.add(i);
                }
                ChoiceBox<Integer> selectTimeBox = new ChoiceBox<>();
                selectTimeBox.getItems().addAll(items);
                selectTimeBox.setValue(1);

                //自定义竞赛词数
                TextField inputCount = new TextField();
                inputCount.setPrefWidth(88);
                inputCount.setPromptText("计划词数");
                inputCount.setText("10");

                VBox vBox = new VBox(16);
                vBox.getChildren().addAll(inputCount, selectTimeBox);

                setMainDialog("竞赛", 266, 166);
                MAIN_DIALOG.setGraphic(vBox);
                MAIN_DIALOG.setOnCloseRequest(new EventHandler<DialogEvent>() {
                    @Override
                    public void handle(DialogEvent dialogEvent) {
                        //System.out.println(MAIN_DIALOG.getResult().getButtonData() == ButtonBar.ButtonData.OK_DONE);
                        if (MAIN_DIALOG.getResult().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                            String count = inputCount.getText();
                            if (!count.matches("([1-9]{1}[\\d]*)$")) {
                                return;
                            }
                            dataSize = Integer.parseInt(count);
                            Integer gameDuration = selectTimeBox.getValue() * 60;
                            if (eventTarget.getSource() == completeWordByFillGameButton) {
                                EnglishAppStart.convertScene("CompleteWordByFillGameScene", gameDuration);
                            } else if (eventTarget.getSource() == selectMeanByWordGameButton) {
                                EnglishAppStart.convertScene("SelectMeanByWordGameScene", gameDuration);
                            }
                        }
                        MAIN_DIALOG.setGraphic(null);
                    }
                });
                MAIN_DIALOG.show();
            }
        };
        completeWordByFillGameEvent();
        selectMeanByWordGameEvent();

        inputFileHandler = new EventHandler<Event>() {
            @Override
            public void handle(Event event) {
                Stage stage = new Stage();
                List<String> filters = new ArrayList<>();
                filters.add("*.txt");
                FileChooser fileChooser = new FileChooser();
                fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("词典文件", filters));
                File file = fileChooser.showOpenDialog(stage);
                if (file != null) {
                    if (event.getSource() == inputDictionaryFileButton) {
                        DICTIONARY_SERVICE.saveByFile(file.getAbsolutePath());
                    } else if (event.getSource() == inputCorpusFileButton) {
                        CORPUS_SERVICE.saveByFile(file.getAbsolutePath());
                    }
                }
            }
        };

        inputEvent();
        inputDictionaryFileEvent();
        inputCorpusFileEvent();

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
                setMainDialog("背词", 266, 166);
                TextField inputNumber = new TextField();
                inputNumber.setPrefWidth(88);
                inputNumber.setPromptText("计划词数");
                MAIN_DIALOG.setGraphic(inputNumber);
                MAIN_DIALOG.setOnCloseRequest(new EventHandler<DialogEvent>() {
                    @Override
                    public void handle(DialogEvent event) {
                        if (MAIN_DIALOG.getResult().getButtonData().isDefaultButton()) {
                            String numberText = inputNumber.getText();
                            if (numberText.matches("\\d+") || "0".equals(numberText)) {
                                dataSize = Integer.parseInt(numberText);
                                EnglishAppStart.convertScene("WordReciteScene");
                            }
                        }
                        MAIN_DIALOG.setGraphic(null);
                    }
                });
                MAIN_DIALOG.show();
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
                EnglishAppStart.convertScene("WordBrowseScene");
            }
        });
    }

    /**
     * 单词补全事件
     */
    public void completeWordByFillEvent() {
        completeWordByFillButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                EnglishAppStart.convertScene("CompleteWordByFillScene");
            }
        });
    }

    /**
     * 读句填词事件
     */
    public void readSentenceFillWordEvent() {
        readSentenceFillWordButton.setOnAction((event) -> {
            EnglishAppStart.convertScene("ReadSentenceFillWordScene");
        });
    }


    /**
     * 单词补全竞赛事件
     */
    private void completeWordByFillGameEvent() {
        completeWordByFillGameButton.setOnAction(gameEventHandler::handle);
    }


    /**
     * 单词选义竞赛事件
     */
    public void selectMeanByWordGameEvent() {
        selectMeanByWordGameButton.setOnAction(gameEventHandler::handle);
    }

    /**
     * 语句默写事件
     */
    public void sentenceWriteFromMemoryEvent() {
        sentenceWriteFromMemory.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                EnglishAppStart.convertScene("SentenceWriteFromMemoryScene");
            }
        });
    }

    /**
     * 导入数据事件
     */
    public void inputEvent() {
        inputButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                GridPane gridPane = new GridPane();
                gridPane.setAlignment(Pos.CENTER);
                gridPane.setVgap(6.6);

                Label en = getLabel(26);
                en.setText("en: ");
                TextField inputEn = getTextField(666);

                Label zh = getLabel(26);
                zh.setText("zh: ");
                TextField inputZh = getTextField(666);

                Label enText = getLabel(26);
                enText.setText("enText: ");
                TextField inputEnText = getTextField(666);

                Label zhText = getLabel(26);
                zhText.setText("zhText: ");
                TextField inputZhText = getTextField(666);

                gridPane.add(en, 1, 0);
                gridPane.add(inputEn, 2, 0);
                gridPane.add(zh, 1, 2);
                gridPane.add(inputZh, 2, 2);
                gridPane.add(enText, 1, 4);
                gridPane.add(inputEnText, 2, 4);
                gridPane.add(zhText, 1, 6);
                gridPane.add(inputZhText, 2, 6);

                setMainDialog("导入数据", 866, 258);
                MAIN_DIALOG.setGraphic(gridPane);
                MAIN_DIALOG.show();
                MAIN_DIALOG.setOnCloseRequest(new EventHandler<DialogEvent>() {
                    @Override
                    public void handle(DialogEvent event) {
                        if (MAIN_DIALOG.getResult().getButtonData().isDefaultButton()) {
                            String zhRegex = "[\u4e00-\u9fa5]+";
                            String zhTextRegex = "[\u4e00-\u9fa5\\w\\pP]+";
                            String enRegex = "[a-zA-Z]+";
                            String enTextRegex = "[\\w\\s\\pP]+";
                            String en = inputEn.getText();
                            String zh = inputZh.getText();
                            String zhText = inputZhText.getText();
                            String enText = inputEnText.getText();
                            if (StringUtils.hasText(en) && en.matches(enRegex) && StringUtils.hasText(zh) && zh.matches(zhRegex)) {
                                DICTIONARY_SERVICE.save(new Dictionary(en, zh));
                                if (StringUtils.hasText(zhText) && enText.matches(enTextRegex) && StringUtils.hasText(enText) && zhText.matches(zhTextRegex)) {
                                    CORPUS_SERVICE.save(new Corpus(en, enText, zhText));
                                }
                            }
                        }
                        MAIN_DIALOG.setGraphic(null);
                    }
                });
            }
        });
    }

    /**
     * 导入词典文件事件
     */
    public void inputDictionaryFileEvent() {
        inputDictionaryFileButton.setOnAction(inputFileHandler::handle);
    }

    /**
     * 导入语句文件事件
     */
    public void inputCorpusFileEvent() {
        inputCorpusFileButton.setOnAction(inputFileHandler::handle);
    }
}
