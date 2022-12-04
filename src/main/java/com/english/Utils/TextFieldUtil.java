package com.english.Utils;

import com.english.node_pool.NodePool;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XYC
 */
@Data
public class TextFieldUtil {
    private static final List<TextField> TEXT_FIELD_LIST = new ArrayList<>();
    private static final ValueChangeListener VALUE_CHANGE_LISTENER = new ValueChangeListener();

    public static TextField get(int index, int width) {
        TextField textField;
        try {
            textField = TEXT_FIELD_LIST.get(index);
            textField.setText(null);
        } catch (IndexOutOfBoundsException e) {
            try {
                textField = NodePool.TEXT_FIELD_POOL.borrowObject();
                textField.setPrefWidth(width);
                TEXT_FIELD_LIST.add(textField);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return textField;
    }

    public static TextField getAndAddValueChangeListener(int index, int width) {
        TextField textField;
        try {
            textField = TEXT_FIELD_LIST.get(index);
            textField.setText(null);
        } catch (IndexOutOfBoundsException e) {
            try {
                textField = NodePool.TEXT_FIELD_POOL.borrowObject();
                textField.setPrefWidth(width);
                textField.textProperty().addListener(VALUE_CHANGE_LISTENER);
                TEXT_FIELD_LIST.add(textField);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return textField;
    }

    /**
     * 输入框输入监听，用于 单词补全 场景
     */
    @Data
    static class ValueChangeListener implements ChangeListener<String> {
        /**
         * 单词所持有的输入框的数量
         */
        public Integer fillNumber;
        /**
         * 指向当前输入框的索引
         */
        public Integer thisIndex;

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if (StringUtils.hasText(newValue)) {
                int nextIndex = thisIndex + 1;
                if (nextIndex == fillNumber) {
                    //nextButton.requestFocus();
                } else {
                    TEXT_FIELD_LIST.get(nextIndex).requestFocus();
                }
            }
        }
    }
}
