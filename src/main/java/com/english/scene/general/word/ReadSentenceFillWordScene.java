package com.english.scene.general.word;

import com.english.EnglishAppStart;
import com.english.Utils.InstanceUtils;
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
public class ReadSentenceFillWordScene extends AbstractScene<Object> {
    private final Label enLabel = getLabel(16);
    private final Label zhTextLabel = new Label();

    private final TextFlow enTextFlow = new TextFlow();

    @Override
    public void initScene() {
        super.initScene();

        zhTextLabel.setFont(Font.font(16));

        addSceneVBox();
        addExitButton();
        addNextButton();

        sceneVBox.getChildren().addAll(enLabel, enTextFlow, zhTextLabel);
    }

    @Override
    public Object doCall() {
        dataIndex = 0;
        dataSize = 20;
        CORPUS_LIST.clear();
        CORPUS_LIST.addAll(CORPUS_SERVICE.queryRandom(dataSize));
        return null;
    }

    @Override
    public void updateUI(Object value) {
        updateQuestion();
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
                enTextFlow.getChildren().clear();
                EnglishAppStart.convertScene("com.english.scene.general.MainScene");
            }
        });
    }

    public void nextButtonEvent() {
        nextButton.setOnAction(event -> {
            String en = CORPUS_LIST.get(dataIndex).getEn();
            String enText = CORPUS_LIST.get(dataIndex).getEnText();
            StringBuilder text = new StringBuilder();
            //如果句子只挖掉一个词，那么遍历获取每个 child 的 text 是不可取的，
            //应直接获取 输入框 的 text 与句子中被挖去的词进行比较就行。（单词比较单词）
            //但如果被挖掉的词出现在该句子中的其他地方（可能词性不同），那么这另一个词也会被挖掉
            //这样造成的后果就是无法精确地进行比较，所以就采用了句子比较句子的方式。
            for (Node child : enTextFlow.getChildren()) {
                if (child instanceof TextField textField) {
                    text.append(textField.getText());
                    textField.setText(null);
                } else {
                    Label label = (Label) child;
                    text.append(label.getText());
                }
            }
            if (text.toString().equals(enText)) {
                enTextFlow.getChildren().clear();
                enLabel.setText(null);
                dataIndex += 1;
                updateQuestion();
            } else {
                enLabel.setText(en);
            }
        });
    }


    public void updateQuestion() {
        zhTextLabel.setText(CORPUS_LIST.get(dataIndex).getZhText());
        String en = CORPUS_LIST.get(dataIndex).getEn();
        String enText = CORPUS_LIST.get(dataIndex).getEnText();
        Pattern pattern = Pattern.compile("(?i)" + en + "[a-z]*");
        Matcher matcher = pattern.matcher(enText);
        while (matcher.find()) {
            String matchedEn = matcher.group();
            InstanceUtils.LOGGER.info(matchedEn);
            int beginIndex = enText.indexOf(matchedEn);
            int endIndex = beginIndex + matchedEn.length();
            TextField textField = getTextField(78);
            Label label = getLabel(16);
            if (beginIndex == 0) {
                enTextFlow.getChildren().add(textField);
            } else {
                label.setText(enText.substring(0, beginIndex));
                enTextFlow.getChildren().addAll(label, textField);
                enText = enText.substring(endIndex);
            }

        }
        if (enText.length() != 0) {
            Label label = getLabel(16);
            label.setText(enText);
            enTextFlow.getChildren().add(label);
        }
    }
}
