package lambda;

import java.util.function.Consumer;

/**
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
    static <T> Consumer<T> wrapper(LambdaConsumer<T> consumer) {
        return t -> {
            try {
                consumer.accept(t);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * <h2>lambda Consumer抛出异常</h2>
     * Created by hanqf on 2020/10/31 22:12. <br>
     * <p>
     * 发生异常时，如果异常类型与指定的异常类型匹配，则做自定义的处理
     *
     * @param consumer
     * @param exceptionClass
     * @param lambdaOnExceptionToDo
     * @return java.util.function.Consumer&lt;T&gt;
     * @author hanqf
     */
    static <T, E extends Exception> Consumer<T> wrapperWithExceptionToDo(LambdaConsumer<T> consumer, Class<E> exceptionClass, LambdaOnExceptionToDo<T, E> lambdaOnExceptionToDo) {
        return i -> {
            try {
                consumer.accept(i);
            } catch (Exception e) {
                try {
                    E exCast = exceptionClass.cast(e);
                    lambdaOnExceptionToDo.toDo(i, exCast);
                } catch (ClassCastException ccEx) {
                    throw new RuntimeException(e);
                }
            }
        };
    }

    static <T, E extends Exception> Consumer<T> wrapperWithExceptionToDo(LambdaConsumer<T> consumer, Class<? extends Exception>[] exceptionClass, LambdaOnExceptionToDo<T, E> lambdaOnExceptionToDo) {
        return i -> {
            try {
                consumer.accept(i);
            } catch (Exception e) {
                boolean check = true;
                for (Class clazz : exceptionClass) {
                    try {
                        E exCast = (E)clazz.cast(e);
                        check = false;
                        lambdaOnExceptionToDo.toDo(i, exCast);
                        break;
                    } catch (ClassCastException ccEx) {

                    }
                }

                if(check) {
                    throw new RuntimeException(e);
                }
            }
        };
    }


    void accept(T t) throws Exception;


}
