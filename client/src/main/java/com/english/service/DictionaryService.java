package com.english.service;

import com.english.entity.Dictionary;

import java.util.List;

/**
 * @author XYC
 */
public interface DictionaryService extends BaseService<Dictionary> {
    /**
     * 查询：翻译业务
     *
     * @param word 翻译对象（支持中英文）
     * @param from 源语种 （en/zh）
     * @param to   翻译后语种 （en/zh)
     * @return List
     */
    List<Dictionary> translate(String word, String from, String to);

    /**
     * 查询：随机查询出 n 条数据
     *
     * @param amount 数量
     * @return List
     */
    List<Dictionary> queryRandomAddCorpus(int amount);
}
