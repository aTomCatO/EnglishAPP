package com.english.service;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author XYC
 */
public interface BaseService<T> {
    ThreadPoolExecutor THREAD_POOL = new ThreadPoolExecutor(
            2,
            Runtime.getRuntime().availableProcessors(),
            10, TimeUnit.SECONDS,
            new LinkedBlockingDeque<Runnable>(6),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.CallerRunsPolicy()
    );

    /**
     * 保存数据业务
     *
     * @param t 封装数据的实体
     */
    void save(T t);

    /**
     * 保存数据业务
     *
     * @param dataList 数据集
     */
    void save(List<T> dataList);

    /**
     * 保存文件中的数据业务
     *
     * @param filePath 文件路劲
     */
    void saveByFile(String filePath);

    /**
     * 查询：随机查询出 n 条数据
     *
     * @param amount 数量
     * @return List
     */

    List<T> queryRandom(int amount);


}
