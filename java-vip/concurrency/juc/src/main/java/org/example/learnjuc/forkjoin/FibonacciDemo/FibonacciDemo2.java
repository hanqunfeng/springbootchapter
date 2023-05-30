package org.example.learnjuc.forkjoin.FibonacciDemo;

/**
 * 使用迭代方式计算Fibonacci数列
 */
public class FibonacciDemo2 {
    public static void main(String[] args) {
        int n = 100000;
        long[] fib = new long[n + 1];
        fib[0] = 0;
        fib[1] = 1;
        for (int i = 2; i <= n; i++) {
            fib[i] = fib[i - 1] + fib[i - 2];
        }
        System.out.println(fib[n]);
    }
}
