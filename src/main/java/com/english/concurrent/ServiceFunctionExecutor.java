package com.english.concurrent;

import com.english.function.ServiceFunction;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

/**
 * @author XYC
 * 初始化场景数据
 */
public class ServiceFunctionExecutor<T> extends Service<T> {
    private final ServiceFunction<T> function;

    public ServiceFunctionExecutor(ServiceFunction<T> function) {
        this.function = function;
    }

    @Override
    protected Task<T> createTask() {
        return new Task<>() {
            @Override
            protected T call() throws Exception {
                return function.doCall();
            }

            @Override
            protected void updateValue(T value) {
                super.updateValue(value);
                function.updateUI(value);
            }
        };
    }

    // protected abstract T doCall();

    // protected abstract void updateUI(T value);
}
