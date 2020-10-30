package lambda;

import java.util.function.Function;

/**
 * <h1>Lambda Funcction</h1>
 * Created by hanqf on 2020/10/30 22:58.
 * <p>
 * 异常处理函数式接口
 */

@FunctionalInterface
public interface LambdaFunction<T, R> {

    /**
     * lambda Function抛出异常
     * 发生异常时,流的处理会立即停止
     *
     * @param function
     * @return
     */
    static <T, R> Function<T, R> warp(LambdaFunction<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }


    R apply(T t) throws Exception;


}
