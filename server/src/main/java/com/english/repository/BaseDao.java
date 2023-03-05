package com.english.repository;

import java.util.List;


/**
 * @author XYC
 */
public interface BaseDao<T> {
    /**
     * 添加：通用的插入方法
     *
     * @param sql
     */
    void insert(String sql);

    /**
     * 查询：通用的查询方法
     *
     * @param sql 查询语句
     * @return List
     */
    List<T> select(String sql);
}
