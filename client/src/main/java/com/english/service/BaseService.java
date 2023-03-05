package com.english.service;

import java.util.List;

/**
 * @author XYC
 */
public interface BaseService<T> {
    /**
     * 查询：随机查询出 n 条数据
     *
     * @param amount 数量
     * @return List
     */

    List<T> queryRandom(int amount);
}
