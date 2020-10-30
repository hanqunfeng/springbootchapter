package lambda;

import java.util.function.Consumer;

/**
 * 参考：https://blog.csdn.net/lhx13636332274/article/details/100936575
 *
 * <h1>Lambda Funcction</h1>
 * Created by hanqf on 2020/10/30 22:58.
 * <p>
 * 异常处理函数式接口
 */

@FunctionalInterface
public interface LambdaConsumer<T> {

    /**
     * lambda Consumer抛出异常
     * 发生异常时,流的处理会立即停止
     *
     * @param consumer
     * @return
     */
    static <T> Consumer<T> warp(LambdaConsumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    void accept(T t) throws Exception;


}
