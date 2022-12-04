package com.english.scene.general.word;

import com.english.EnglishAppStart;
import com.english.scene.AbstractScene;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author XYC
 * 读句填词场景
 */
public class ReadSentenceFillWordScene extends AbstractScene {
    private Label enLabel;
    private Label zhTextLabel;

    private TextFlow enCurrentTextFlow;

    {
        this.sceneName = "读句填词场景";
    }

    @Override
    public void initScene() {
        super.initScene();

        //进行场景基本组件实例化
        enLabel = new Label();
        zhTextLabel = new Label();
        enCurrentTextFlow = new TextFlow();

        addVBoxMain();
        addExitButton();
        addNextButton();

        enLabel.setFont(Font.font(16));
        zhTextLabel.setFont(Font.font(16));

        vBoxMain.getChildren().addAll(enLabel, enCurrentTextFlow, zhTextLabel);
    }

    public void initData() {
        dataSize = 20;
        dataIndex = 0;
        corpusList.clear();
        corpusList.addAll(corpusService.queryRandom(dataSize));

        zhTextLabel.setText(corpusList.get(dataIndex).getZhText());
        nextCorpus();
    }


    @Override
    public void bindEvent() {
        exitButtonEvent();
        nextButtonEvent();
    }

    @Override
    public void exitButtonEvent() {
        exitButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                enCurrentTextFlow.getChildren().clear();
                releaseNode();
                EnglishAppStart.convertScene("主场景");
            }
        });
    }

    public void nextButtonEvent() {
        nextButton.setOnAction(event -> {
            String en = corpusList.get(dataIndex).getEn();
            String enText = corpusList.get(dataIndex).getEnText();
            StringBuilder text = new StringBuilder();
            for (Node child : enCurrentTextFlow.getChildren()) {
                if (child instanceof TextField) {
                    TextField textField = (TextField) child;
                    text.append(textField.getText());
                    textField.setText(null);
                } else {
                    Label label = (Label) child;
                    text.append(label.getText());
                }
            }
            if (text.toString().equals(enText)) {
                enCurrentTextFlow.getChildren().clear();
                enLabel.setText(null);
                dataIndex += 1;
                zhTextLabel.setText(corpusList.get(dataIndex).getZhText());
                nextCorpus();
            } else {
                enLabel.setText(en);
            }
        });
    }


    public void nextCorpus() {
        String en = corpusList.get(dataIndex).getEn();
        String enText = corpusList.get(dataIndex).getEnText();
        Pattern pattern = Pattern.compile("(?i)" + en + "[a-z]*");
        Matcher matcher = pattern.matcher(enText);
        int textFieldIndex = 0;
        int labelIndex = 0;
        while (matcher.find()) {
            String matchedEn = matcher.group();
            System.out.println(matchedEn);
            int beginIndex = enText.indexOf(matchedEn);
            int endIndex = beginIndex + matchedEn.length();
            TextField textField = getTextField(textFieldIndex++, 78);
            Label label = getLabel(labelIndex++, 16);
            if (beginIndex == 0) {
                enCurrentTextFlow.getChildren().add(textField);
            } else {
                label.setText(enText.substring(0, beginIndex));
                enCurrentTextFlow.getChildren().addAll(label, textField);
                enText = enText.substring(endIndex);
            }

        }
        if (enText.length() != 0) {
            Label label = getLabel(labelIndex, 16);
            label.setText(enText);
            enCurrentTextFlow.getChildren().add(label);
        }
    }
}
