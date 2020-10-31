package lambda;

/**
 * <h1>抛出异常时做什么操作</h1>
 * Created by hanqf on 2020/10/31 20:21.
 */

@FunctionalInterface
public interface LambdaOnExceptionToDo<T,E extends Exception> {

    void toDo(T t,E e);
}
