package org.example.learnjuc.forkjoin.recursivetask;

import java.util.concurrent.RecursiveTask;

public class LongSum extends RecursiveTask<Long> {
    // 任务拆分最小阈值
    static final int SEQUENTIAL_THRESHOLD = 1000;

    int low;
    int high;
    int[] array;

    LongSum(int[] arr, int lo, int hi) {
        array = arr;
        low = lo;
        high = hi;
    }

    @Override
    protected Long compute() {

        //当任务拆分到小于等于阀值时开始求和
        if (high - low <= SEQUENTIAL_THRESHOLD) {

            long sum = 0;
            for (int i = low; i < high; ++i) {
                sum += array[i];
            }
            return sum;
        } else {  // 任务过大继续拆分
            int mid = low + (high - low) / 2;
            LongSum left = new LongSum(array, low, mid);
            LongSum right = new LongSum(array, mid, high);
            // 提交任务
            left.fork();
            right.fork();
            //获取任务的执行结果
            long rightResult = right.join();
            long leftResult = left.join();
            //合并结果
            return rightResult + leftResult;
        }
    }
}

