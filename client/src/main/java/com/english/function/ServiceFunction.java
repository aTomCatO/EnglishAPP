package com.english.function;

/**
 * @author XYC
 */
public interface ServiceFunction<T> {
    /**
     * Service call 的执行程序
     *
     * @return T
     */
    T doCall();

    /**
     * Service updateValue 的执行程序
     *
     * @param value 泛型实例
     */
    void updateUI(T value);
}
