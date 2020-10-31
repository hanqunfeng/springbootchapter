package lambda;

import java.util.Optional;
import java.util.function.Function;

/**
 * <h1>Lambda异常收集</h1>
 * Created by hanqf on 2020/10/31 09:43.
 */


public class LambdaTry<L, R> {

    private final L left;
    private final R right;

    private LambdaTry(L left, R right) {
        this.left = left;
        this.right = right;
    }

    public static <L,R> LambdaTry<L,R> Left( L value) {
        return new LambdaTry(value, null);
    }

    public static <L,R> LambdaTry<L,R> Right( R value) {
        return new LambdaTry(null, value);
    }

    public Optional<L> getLeft() {
        return Optional.ofNullable(left);
    }

    public Optional<R> getRight() {
        return Optional.ofNullable(right);
    }

    public boolean isLeft() {
        return left != null;
    }

    public boolean isRight() {
        return right != null;
    }

    public <T> Optional<T> mapLeft(Function<? super L, T> mapper) {
        if (isLeft()) {
            return Optional.of(mapper.apply(left));
        }
        return Optional.empty();
    }

    public <T> Optional<T> mapRight(Function<? super R, T> mapper) {
        if (isRight()) {
            return Optional.of(mapper.apply(right));
        }
        return Optional.empty();
    }


}
