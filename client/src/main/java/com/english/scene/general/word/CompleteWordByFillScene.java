package com.english.scene.general.word;

import com.english.Client;
import com.english.util.StringUtils;
import com.english.scene.AbstractScene;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * @author XYC
 * 单词补全场景
 */
public class CompleteWordByFillScene extends AbstractScene<Object> {

    protected static final List<TextField> TEXT_FIELD_LIST = new ArrayList<>();
    /**
     * fillChars 将挖去的字母存在字符数组中
     */
    protected char[] fillChars;
    protected TextFlow enCurrentTextFlow;
    /**
     * 显示计时器的 label
     */
    protected Label enPreviousLabel;
    protected Label zhCurrentLabel;

    @Override
    public void initScene() {
        super.initScene();

        //进行场景基本组件实例化
        enCurrentTextFlow = new TextFlow();
        enPreviousLabel = new Label();
        zhCurrentLabel = new Label();

        enPreviousLabel.setFont(Font.font(18));
        zhCurrentLabel.setFont(Font.font(18));

        addSceneHBox();
        addSceneVBox();
        addExitButton();
        addNextButton();

        sceneHBox.getChildren().add(enCurrentTextFlow);
        sceneVBox.getChildren().addAll(enPreviousLabel, sceneHBox, zhCurrentLabel);
    }

    @Override
    public Object doCall() {
        dataSize = 20;
        dataIndex = RANDOM.nextInt(dataSize);
        DICTIONARY_LIST.clear();
        DICTIONARY_LIST.addAll(DICTIONARY_SERVICE.queryRandom(dataSize));
        return null;
    }

    @Override
    public void updateUI(Object value) {
        fillImplement();
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
                enPreviousLabel.setText(null);
                enCurrentTextFlow.getChildren().clear();
                Client.convertScene("com.english.scene.general.MainScene");
            }
        });
    }

    public void nextButtonEvent() {
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                enCurrentTextFlow.getChildren().clear();
                //先通过旧单词的索引设置 enPreviousLabel 的文本
                enPreviousLabel.setText(DICTIONARY_LIST.get(dataIndex).getEn());
                //再更新为新单词的索引
                dataIndex = RANDOM.nextInt(dataSize);
                fillImplement();
                TEXT_FIELD_LIST.get(0).requestFocus();
            }
        });
    }

    /**
     * 单词补全功能的具体实现
     * 因为要把挖掉的字符保存在字符数组中作为方法返回值,就出现了以下问题:
     * 1,由于是随机生成需要挖掉的字符的索引,所以要保证随机生成的索引不能重复
     * 2,因为返回的字符数组是作为检索判断的,为了使输入框和字符依依对应起来,必须保证每个字符在字符数组中的顺序(字符在单词里位置顺序)
     * 例如 culture 挖出来三个字符 c,l和u , _u_t_re
     * 此时在字符数组中 c 的位置必须 l 和 u 的前面,也就是 0 索引;而 t 的位置必须在  u 的前面,也就是 1 索引 ; u 最后一个位置 2 索引
     * 由于随机生成的索引本来就代表着它们在单词里的位置
     * 再结合使用 TreeSet集合 的特性,便可达到 去重,有序 的需求
     */
    public void fillImplement() {
        int beforeSize = TEXT_FIELD_LIST.size();

        String en = DICTIONARY_LIST.get(dataIndex).getEn();
        LOGGER.info(en);
        char[] enChars = en.toCharArray();
        int enLength = enChars.length;
        int fillCount = enLength / 2;
        this.fillChars = new char[fillCount];
        Set<Integer> indexSet = new TreeSet<>();
        for (int i = 0; i < fillCount; i++) {
            int charIndex = RANDOM.nextInt(enLength);
            while (!indexSet.add(charIndex)) {
                charIndex = RANDOM.nextInt(enLength);
            }
        }
        StringBuilder piece = new StringBuilder();
        int textFieldIndex = 0;
        for (int i = 0; i < enLength; i++) {
            if (indexSet.contains(i)) {
                if (piece.length() > 0) {
                    Label label = getLabel(26);
                    label.setText(piece.toString());

                    enCurrentTextFlow.getChildren().add(label);
                    piece.delete(0, piece.length());
                }

                TextField inputChar = getTextField(textFieldIndex, 32);

                enCurrentTextFlow.getChildren().add(inputChar);
                this.fillChars[textFieldIndex++] = enChars[i];
            } else {
                piece.append(enChars[i]);
            }
        }
        if (piece.length() > 0) {
            Label label = getLabel(26);
            label.setText(piece.toString());
            enCurrentTextFlow.getChildren().add(label);
        }

        //当 集合中的元素大小 afterSize 大于 beforeSize 时
        //则表示有新地输入框需要绑定事件
        //从 beforeSize 作为起始索引开始则是为了避免旧地输入框重复绑定事假
        int afterSize = TEXT_FIELD_LIST.size();
        if (afterSize > beforeSize) {
            textFieldRequestFocus(beforeSize);
        }
        zhCurrentLabel.setText(DICTIONARY_LIST.get(dataIndex).getZh());
    }

    /**
     * 为输入框绑定事件，如输入监听事件，以及键盘 左/右 按钮触发事件
     */
    public void textFieldRequestFocus(int beginIndex) {
        for (int i = beginIndex; i < TEXT_FIELD_LIST.size(); i++) {
            TextField textField = TEXT_FIELD_LIST.get(i);
            int thisIndex = i;
            textField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (StringUtils.hasText(newValue)) {
                        int nextIndex = thisIndex + 1;
                        if (nextIndex == fillChars.length) {
                            nextButton.requestFocus();
                        } else {
                            TEXT_FIELD_LIST.get(nextIndex).requestFocus();
                        }
                    }
                }
            });
            textField.setOnKeyPressed(new EventHandler<KeyEvent>() {
                @Override
                public void handle(KeyEvent event) {
                    //System.out.println(event.getCode().getName());
                    switch (event.getCode().getName()) {
                        case "Left": {
                            //如果当前焦点在第一个输入框,键盘点击向左箭头 ⬅ ,焦点就会给到最后一个输入框
                            //否则上一个输入框就会获得焦点
                            if (thisIndex == 0) {
                                TEXT_FIELD_LIST.get(fillChars.length - 1).requestFocus();
                            } else {
                                TEXT_FIELD_LIST.get(thisIndex - 1).requestFocus();
                            }
                            break;
                        }
                        case "Right": {
                            //如果当前焦点在最后一个输入框,键盘点击向右箭头 ➡ ,焦点就会给到第一个输入框
                            //否则下一个输入框就会获得焦点
                            if (thisIndex == fillChars.length - 1) {
                                TEXT_FIELD_LIST.get(0).requestFocus();
                            } else {
                                TEXT_FIELD_LIST.get(thisIndex + 1).requestFocus();
                            }
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
            });
        }
    }

    public TextField getTextField(int index, int width) {
        if (TEXT_FIELD_LIST.size() > index) {
            TextField textField = TEXT_FIELD_LIST.get(index);
            textField.clear();
            return textField;
        }
        TextField textField = super.getTextField(width);
        TEXT_FIELD_LIST.add(textField);
        return textField;
    }
}
