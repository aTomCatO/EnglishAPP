package com.english.service;

import com.english.entity.Corpus;

/**
 * @author XYC
 */
public interface CorpusService extends BaseService<Corpus> {
    /**
     * 查询：翻译业务
     *
     * @param sentence 翻译对象（支持中英文）
     * @param from     源语种 （en/zh）
     * @param to       翻译后语种 （en/zh)
     * @return List
     */
    Corpus translate(String sentence, String from, String to);


}
