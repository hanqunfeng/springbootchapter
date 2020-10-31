package com.example.websocketdemo.wsserver;

import java.util.function.BiConsumer;

/**
 * <h1></h1>
 * Created by hanqf on 2020/10/30 23:39.
 */

@FunctionalInterface
public interface LambdaBiConsumer<T, U> {

    /**
     * lambda BiConsumer抛出异常
     * 发生异常时,流的处理会立即停止
     *
     * @param consumer
     * @return
     */
    static <T,U> BiConsumer<T,U> wrapper(LambdaBiConsumer<T,U> consumer) {
        return (t,u) -> {
            try {
                consumer.accept(t,u);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    void accept(T t, U u) throws Exception;
}
