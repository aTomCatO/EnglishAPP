package com.english.node_pool;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

/**
 * @author XYC
 */
public abstract class AbstractNodeAccessor implements NodeAccessorSupport {
    protected static final List<TextField> TEXT_FIELD_LIST = new ArrayList<>();
    protected static final List<Label> LABEL_LIST = new ArrayList<>();

    @Override
    public TextField getTextField(int index, int width) {
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

    @Override
    public Label getLabel(int index, int fontSize) {
        Label label;
        try {
            label = LABEL_LIST.get(index);
        } catch (IndexOutOfBoundsException e) {
            try {
                label = NodePool.LABEL_POOL.borrowObject();
                label.setFont(Font.font(fontSize));
                LABEL_LIST.add(label);
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        return label;
    }

    public void releaseNode() {
        try {
            if (TEXT_FIELD_LIST.size() != 0) {
                for (TextField textField : TEXT_FIELD_LIST) {
                    NodePool.TEXT_FIELD_POOL.returnObject(textField);
                }
                TEXT_FIELD_LIST.clear();
            }
            if (LABEL_LIST.size() != 0) {
                for (Label label : LABEL_LIST) {
                    NodePool.LABEL_POOL.returnObject(label);
                }
                LABEL_LIST.clear();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
