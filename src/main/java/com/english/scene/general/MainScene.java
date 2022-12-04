package com.english.scene.general;

import com.english.EnglishAppStart;
import com.english.javaBeans.Corpus;
import com.english.javaBeans.Dictionary;
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

    {
        this.sceneName = "主场景";
    }

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
        inputCorpusFileButton = new MenuItem("导入语料");
        tabPane = new TabPane();

        functionBar.setStyle("-fx-background-color: #93dc49");
        functionBar.getMenus().add(functionMenu);
        wordFunction.getItems().addAll(wordReciteButton, wordBrowseButton, completeWordByFillButton, readSentenceFillWordButton);
        sentenceFunction.getItems().addAll(sentenceWriteFromMemory);
        gameFunction.getItems().addAll(completeWordByFillGameButton, selectMeanByWordGameButton);
        saveFunction.getItems().addAll(inputButton, inputDictionaryFileButton, inputCorpusFileButton);
        functionMenu.getItems().addAll(wordFunction, sentenceFunction, gameFunction, saveFunction);

        addHBoxMain();
        hBoxMain.getChildren().addAll(search, searchButton, functionBar);

        tabPane.setPrefWidth(371);
        tabPane.setPrefHeight(288);
        tabPane.setBackground(new Background(new BackgroundFill(Paint.valueOf("E596A1AF"), new CornerRadii(50), new Insets(10))));
        tabPane.setBorder(new Border(new BorderStroke(Paint.valueOf("384A98FF"), BorderStrokeStyle.DASHED, new CornerRadii(10), new BorderWidths(2))));

        anchorPane.getChildren().add(tabPane);
        AnchorPane.setTopAnchor(hBoxMain, 16.0);
        AnchorPane.setLeftAnchor(hBoxMain, 76.0);
        AnchorPane.setTopAnchor(tabPane, 56.0);
    }

    public void initData() {
        dictionaryList = dictionaryService.queryRandomAddCorpus(6);
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

                getDialog("竞赛", 266, 166);
                dialog.setGraphic(vBox);
                dialog.setOnCloseRequest(new EventHandler<DialogEvent>() {
                    @Override
                    public void handle(DialogEvent dialogEvent) {
                        System.out.println(dialog.getResult().getButtonData() == ButtonBar.ButtonData.OK_DONE);
                        if (dialog.getResult().getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                            String count = inputCount.getText();
                            if (!count.matches("([1-9]{1}[\\d]*)$")) {
                                return;
                            }
                            dataSize = Integer.parseInt(count);
                            Integer gameDuration = selectTimeBox.getValue() * 60;
                            if (eventTarget.getSource() == completeWordByFillGameButton) {
                                EnglishAppStart.convertScene("单词补全竞赛场景", gameDuration);
                            } else if (eventTarget.getSource() == selectMeanByWordGameButton) {
                                EnglishAppStart.convertScene("单词选义竞赛场景", gameDuration);
                            }
                        }
                        dialog.setGraphic(null);
                    }
                });
                dialog.show();
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
                        dictionaryService.saveByFile(file.getAbsolutePath());
                    } else if (event.getSource() == inputCorpusFileButton) {
                        corpusService.saveByFile(file.getAbsolutePath());
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
                    dictionaryList.clear();
                    Corpus corpus = null;
                    if (content.matches(zhRegex)) {
                        dictionaryList.addAll(dictionaryService.translate(content, "zh", "en"));
                    } else if (content.matches(enRegex)) {
                        dictionaryList.addAll(dictionaryService.translate(content, "en", "zh"));
                    } else if (content.matches(zhTextRegex)) {
                        corpus = corpusService.translate(content, "zh", "en");
                    } else if (content.matches(enTextRegex)) {
                        corpus = corpusService.translate(content, "en", "zh");
                    }
                    if (dictionaryList.size() > 0) {
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
                getDialog("背词", 266, 166);
                TextField inputNumber = new TextField();
                inputNumber.setPrefWidth(88);
                inputNumber.setPromptText("计划词数");
                dialog.setGraphic(inputNumber);
                dialog.setOnCloseRequest(new EventHandler<DialogEvent>() {
                    @Override
                    public void handle(DialogEvent event) {
                        if (dialog.getResult().getButtonData().isDefaultButton()) {
                            String numberText = inputNumber.getText();
                            if (numberText.matches("[\\d]+") || "0".equals(numberText)) {
                                dataSize = Integer.parseInt(numberText);
                                EnglishAppStart.convertScene("背词场景");
                            }
                        }
                        dialog.setGraphic(null);
                    }
                });
                dialog.show();
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
                EnglishAppStart.convertScene("单词浏览场景");
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
                EnglishAppStart.convertScene("单词补全场景");
            }
        });
    }

    /**
     * 读句填词事件
     */
    public void readSentenceFillWordEvent() {
        readSentenceFillWordButton.setOnAction((event) -> {
            EnglishAppStart.convertScene("读句填词场景");
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
                EnglishAppStart.convertScene("语句默写场景");
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

                Label en = getLabel(0, 26);
                en.setText("en: ");
                TextField inputEn = getTextField(0, 666);

                Label zh = getLabel(1, 26);
                zh.setText("zh: ");
                TextField inputZh = getTextField(1, 666);

                Label enText = getLabel(2, 26);
                enText.setText("enText: ");
                TextField inputEnText = getTextField(2, 666);

                Label zhText = getLabel(3, 26);
                zhText.setText("zhText: ");
                TextField inputZhText = getTextField(3, 666);

                gridPane.add(en, 1, 0);
                gridPane.add(inputEn, 2, 0);
                gridPane.add(zh, 1, 2);
                gridPane.add(inputZh, 2, 2);
                gridPane.add(enText, 1, 4);
                gridPane.add(inputEnText, 2, 4);
                gridPane.add(zhText, 1, 6);
                gridPane.add(inputZhText, 2, 6);

                getDialog("导入数据", 866, 258);
                dialog.setGraphic(gridPane);
                dialog.show();
                dialog.setOnCloseRequest(new EventHandler<DialogEvent>() {
                    @Override
                    public void handle(DialogEvent event) {
                        if (dialog.getResult().getButtonData().isDefaultButton()) {
                            String zhRegex = "[\u4e00-\u9fa5]+";
                            String zhTextRegex = "[\u4e00-\u9fa5\\w\\pP]+";
                            String enRegex = "[a-zA-Z]+";
                            String enTextRegex = "[\\w\\s\\pP]+";
                            String en = inputEn.getText();
                            String zh = inputZh.getText();
                            String zhText = inputZhText.getText();
                            String enText = inputEnText.getText();
                            if (StringUtils.hasText(en) && en.matches(enRegex) && StringUtils.hasText(zh) && zh.matches(zhRegex)) {
                                dictionaryService.save(new Dictionary(en, zh));
                                if (StringUtils.hasText(zhText) && enText.matches(enTextRegex) && StringUtils.hasText(enText) && zhText.matches(zhTextRegex)) {
                                    corpusService.save(new Corpus(en, enText, zhText));
                                }
                            }
                        }
                        dialog.setGraphic(null);
                        releaseNode();
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
