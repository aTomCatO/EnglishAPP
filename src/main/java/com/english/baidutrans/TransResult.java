package com.english.baidutrans;

import lombok.Data;

import java.util.List;

/**
 * 翻译结果 实体
 *
 * @author XYC
 */
@Data
public class TransResult {
    /**
     * 翻译源语言
     */
    private String from;
    /**
     * 译文语言
     */
    private String to;
    /**
     * 翻译结果
     */
    private List<TransData> trans_result;
}