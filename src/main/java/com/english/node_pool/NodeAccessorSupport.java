package com.english.node_pool;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * @author XYC
 * Node 寄存器支持
 */
public interface NodeAccessorSupport {

    /**
     * 获取一个 TextField 组件
     *
     * @param index :textField 在集合中的索引
     * @param width :textField 的宽度
     * @return TextField 实例
     */
    TextField getTextField(int index, int width);

    /**
     * 获取一个 Label 组件
     *
     * @param index    : label 在集合中的索引
     * @param fontSize : label 字体大小
     * @return Label 实例
     */
    Label getLabel(int index, int fontSize);

    /**
     * 释放 Node
     */
    void releaseNode();

}
