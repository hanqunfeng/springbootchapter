package lambda;

import com.sun.tools.javac.util.Pair;

import java.util.function.Function;

/**
 * 参考：https://blog.csdn.net/lhx13636332274/article/details/100936575
 *
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
    static <T, R> Function<T, R> wrapper(LambdaFunction<T, R> function) {
        return t -> {
            try {
                return function.apply(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * lambda Function抛出异常
     * 发生异常时,流的处理会继续
     * 不保存原始值
     *
     * @param function
     * @return
     */
    static <T, R> Function<T, LambdaTry> left(LambdaFunction<T, R> function) {
        return t -> {
            try {
                return LambdaTry.Right(function.apply(t));
            } catch (Exception e) {
                return LambdaTry.Left(e);
            }
        };
    }


    /**
     * lambda Function抛出异常
     * 发生异常时,流的处理会继续
     * 异常和原始值都保存在左侧
     *
     * @param function
     * @return
     */
    static <T, R> Function<T, LambdaTry> leftWithValue(LambdaFunction<T, R> function) {
        return t -> {
            try {
                return LambdaTry.Right(function.apply(t));
            } catch (Exception ex) {
                return LambdaTry.Left(Pair.of(ex, t));
            }
        };
    }


    R apply(T t) throws Exception;


}
