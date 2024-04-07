package com.english.scene.general.word;

import com.english.EnglishAppStart;
import com.english.Utils.InstanceUtils;
import com.english.scene.game.CountdownScene;
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
public class WordCompletionScene extends CountdownScene<Object> {

    protected static final List<TextField> TEXT_FIELD_LIST = new ArrayList<>();
    /**
     * 单词碎片，临时存储不是被挖的字母，以便设置给label
     */
    protected static final StringBuilder piece = new StringBuilder();
    /**
     * 当前单词题文本流
     */
    protected final TextFlow enCurrentTextFlow = new TextFlow();
    /**
     * 当前单词题的中文翻译
     */
    protected final Label zhCurrentLabel = new Label();
    /**
     * 上一个单词题
     */
    protected final Label enPreviousLabel = new Label();
    ;
    /**
     * 缺少的字母数组，将当前单词中挖去的字母保存在该字符数组中
     */
    protected static char[] fillChars;


    @Override
    public void initScene() {
        super.initScene();

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
                EnglishAppStart.convertScene("com.english.scene.general.MainScene");
            }
        });
    }

    public void nextButtonEvent() {
        nextButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                boolean isRight = assessAnswer();
                if (isRight) {
                    enCurrentTextFlow.getChildren().clear();
                    // 先通过旧单词的索引设置 enPreviousLabel 的文本
                    enPreviousLabel.setText(DICTIONARY_LIST.get(dataIndex).getEn());
                    // 再更新为新单词的索引
                    dataIndex = RANDOM.nextInt(dataSize);
                    fillImplement();
                    TEXT_FIELD_LIST.get(0).requestFocus();
                } else {
                    //先通过旧单词的索引设置 enPreviousLabel 的文本
                    enPreviousLabel.setText(DICTIONARY_LIST.get(dataIndex).getEn());
                }
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
        InstanceUtils.LOGGER.info(en);
        char[] enChars = en.toCharArray();
        int enLength = enChars.length;
        // 计算需要填补的字母数
        int fillCount = enLength / 2;
        fillChars = new char[fillCount];
        // 使用TreeSet集合记录要被挖掉的字母在单词字符串中的索引（随机生成的索引）
        Set<Integer> indexSet = new TreeSet<>();
        for (int i = 0; i < fillCount; i++) {
            int charIndex = RANDOM.nextInt(enLength);
            while (!indexSet.add(charIndex)) {
                charIndex = RANDOM.nextInt(enLength);
            }
        }
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
                fillChars[textFieldIndex++] = enChars[i];
            } else {
                piece.append(enChars[i]);
            }
        }
        if (piece.length() > 0) {
            Label label = getLabel(26);
            label.setText(piece.toString());
            enCurrentTextFlow.getChildren().add(label);
            piece.delete(0, piece.length());
        }

        // 当 集合中的元素大小 afterSize 大于 beforeSize 时
        // 则表示有新地输入框需要绑定事件
        // 从 beforeSize 作为起始索引开始则是为了避免旧地输入框重复绑定事假
        int afterSize = TEXT_FIELD_LIST.size();
        if (afterSize > beforeSize) {
            textFieldRequestFocus(beforeSize);
        }

        TEXT_FIELD_LIST.get(0).requestFocus();
        zhCurrentLabel.setText(DICTIONARY_LIST.get(dataIndex).getZh());
    }

    /**
     * 为输入框绑定事件：输入监听事件、键盘 左/右 按钮触发事件
     */
    public void textFieldRequestFocus(int beginIndex) {
        for (int i = beginIndex; i < TEXT_FIELD_LIST.size(); i++) {
            TextField textField = TEXT_FIELD_LIST.get(i);
            int thisIndex = i;
            textField.textProperty().addListener(new ChangeListener<String>() {
                @Override
                public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                    if (newValue.matches("[a-zA-Z]")) {
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
                    String keyName = event.getCode().getName();
                    // InstanceUtils.LOGGER.info(keyName);
                    String left = "Left";
                    String right = "Right";
                    if (left.equals(keyName)) {
                        //如果当前焦点在第一个输入框,键盘点击向左箭头 ⬅ ,焦点就会给到最后一个输入框
                        //否则上一个输入框就会获得焦点
                        if (thisIndex == 0) {
                            TEXT_FIELD_LIST.get(fillChars.length - 1).requestFocus();
                        } else {
                            TEXT_FIELD_LIST.get(thisIndex - 1).requestFocus();
                        }
                    } else if (right.equals(keyName)) {
                        //如果当前焦点在最后一个输入框,键盘点击向右箭头 ➡ ,焦点就会给到第一个输入框
                        //否则下一个输入框就会获得焦点
                        if (thisIndex == fillChars.length - 1) {
                            TEXT_FIELD_LIST.get(0).requestFocus();
                        } else {
                            TEXT_FIELD_LIST.get(thisIndex + 1).requestFocus();
                        }
                    }
                }
            });
        }
    }

    /**
     * 评估用户的回答是否正确
     */
    public boolean assessAnswer() {
        boolean isRight = true;
        for (int i = 0; i < fillChars.length; i++) {
            TextField fill = TEXT_FIELD_LIST.get(i);
            if (!String.valueOf(fillChars[i]).equalsIgnoreCase(fill.getText())) {
                isRight = false;
                break;
            }
        }
        return isRight;
    }

    public TextField getTextField(int index, int width) {
        if (TEXT_FIELD_LIST.size() > index) {
            TextField textField = TEXT_FIELD_LIST.get(index);
            textField.clear();
            return textField;
        }
        TextField textField = getTextField(width);
        TEXT_FIELD_LIST.add(textField);
        return textField;
    }
}
