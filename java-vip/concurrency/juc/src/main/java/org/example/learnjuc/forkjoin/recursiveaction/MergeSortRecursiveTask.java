package org.example.learnjuc.forkjoin.recursiveaction;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * 利用fork-join实现数组排序
 */
public class MergeSortRecursiveTask extends RecursiveTask<int[]> {

    private final int threshold; //拆分的阈值，低于此阈值就不再进行拆分
    private int[] arrayToSort; //要排序的数组

    public MergeSortRecursiveTask(final int[] arrayToSort, final int threshold) {
        this.arrayToSort = arrayToSort;
        this.threshold = threshold;
    }
    @Override
    protected int[] compute() {
        //拆分后的数组长度小于阈值，直接进行排序
        if (arrayToSort.length <= threshold) {
            // 调用jdk提供的排序方法
            Arrays.sort(arrayToSort);
            return arrayToSort;
        }

        // 对数组进行拆分
        int midpoint = arrayToSort.length / 2;
        int[] leftArray = Arrays.copyOfRange(arrayToSort, 0, midpoint);
        int[] rightArray = Arrays.copyOfRange(arrayToSort, midpoint, arrayToSort.length);

        MergeSortRecursiveTask leftTask = new MergeSortRecursiveTask(leftArray, threshold);
        MergeSortRecursiveTask rightTask = new MergeSortRecursiveTask(rightArray, threshold);

        //调用任务,阻塞当前线程，直到所有子任务执行完成
        invokeAll(leftTask, rightTask);

        // 合并排序结果
        arrayToSort = merge(leftTask.getSortedArray(), rightTask.getSortedArray());
        return arrayToSort;
    }

    private int[] getSortedArray() {
        return arrayToSort;
    }

    /**
     * 合并两个有序数组，并返回合并后的有序数组
     */
    private int[] merge(final int[] leftArray, final int[] rightArray) {
        // 定义用于合并结果的数组
        int[] mergedArray = new int[leftArray.length + rightArray.length];
        int mergedArrayPos = 0;
        // 利用双指针进行两个数的比较
        int leftArrayPos = 0;
        int rightArrayPos = 0;
        while (leftArrayPos < leftArray.length && rightArrayPos < rightArray.length) {
            // 比较左右数组中的元素大小，并将较小的元素放入合并结果数组中
            if (leftArray[leftArrayPos] <= rightArray[rightArrayPos]) {
                mergedArray[mergedArrayPos] = leftArray[leftArrayPos];
                leftArrayPos++;
            } else {
                mergedArray[mergedArrayPos] = rightArray[rightArrayPos];
                rightArrayPos++;
            }
            mergedArrayPos++;
        }

        // 将剩余的左数组元素放入合并结果数组中
        while (leftArrayPos < leftArray.length) {
            mergedArray[mergedArrayPos] = leftArray[leftArrayPos];
            leftArrayPos++;
            mergedArrayPos++;
        }

        // 将剩余的右数组元素放入合并结果数组中
        while (rightArrayPos < rightArray.length) {
            mergedArray[mergedArrayPos] = rightArray[rightArrayPos];
            rightArrayPos++;
            mergedArrayPos++;
        }

        // 返回合并后的有序数组
        return mergedArray;
    }

    /**
     * 随机生成数组
     * @param size 数组的大小
     */
    private static int[] buildRandomIntArray(final int size) {
        int[] arrayToCalculateSumOf = new int[size];
        Random generator = new Random();
        for (int i = 0; i < arrayToCalculateSumOf.length; i++) {
            arrayToCalculateSumOf[i] = generator.nextInt(10000);
        }
        return arrayToCalculateSumOf;
    }

    public static void main(String[] args) {
        //拆分的阈值
        int threshold = 20;
        int[] arrayToSortByMergeSort = buildRandomIntArray(2000);
        System.out.print("排序前: ");
        for (int element : arrayToSortByMergeSort) {
            System.out.print(element + " ");
        }
        System.out.println();
        //利用forkjoin排序
        MergeSortRecursiveTask mergeSortRecursiveTask = new MergeSortRecursiveTask(arrayToSortByMergeSort, threshold);
        //构建forkjoin线程池
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        long startTime = System.nanoTime();
        //执行排序任务
        final int[] mergeSortArray = forkJoinPool.invoke(mergeSortRecursiveTask);
        System.out.print("排序后: ");
        for (int element : mergeSortArray) {
            System.out.print(element + " ");
        }
        System.out.println();

        long duration = System.nanoTime() - startTime;
        System.out.println("forkjoin排序时间: " + (duration / (1000f * 1000f)) + "毫秒");
    }

}
