package org.example.learnjuc.forkjoin.recursiveaction;



import java.util.Arrays;



/**
 * @author Fox
 *
 * 实现数组的归并排序
 */
public class MergeSort {

    private final int[] arrayToSort; //要排序的数组
    private final int threshold;  //拆分的阈值，低于此阈值就不再进行拆分

    public MergeSort(final int[] arrayToSort, final int threshold) {
        this.arrayToSort = arrayToSort;
        this.threshold = threshold;
    }

    /**
     * 排序
     * @return
     */
    public int[] mergeSort() {
        return mergeSort(arrayToSort, threshold);
    }

    public static int[] mergeSort(final int[] arrayToSort, int threshold) {
        //拆分后的数组长度小于阈值，直接进行排序
        if (arrayToSort.length < threshold) {
            //调用jdk提供的排序方法
            Arrays.sort(arrayToSort);
            return arrayToSort;
        }

        int midpoint = arrayToSort.length / 2;
        //对数组进行拆分
        int[] leftArray = Arrays.copyOfRange(arrayToSort, 0, midpoint);
        int[] rightArray = Arrays.copyOfRange(arrayToSort, midpoint, arrayToSort.length);
        //递归调用
        leftArray = mergeSort(leftArray, threshold);
        rightArray = mergeSort(rightArray, threshold);
        //合并排序结果
        return merge(leftArray, rightArray);
    }

    public static int[] merge(final int[] leftArray, final int[] rightArray) {
        //定义用于合并结果的数组
        int[] mergedArray = new int[leftArray.length + rightArray.length];
        int mergedArrayPos = 0;
        // 利用双指针进行两个数的比较
        int leftArrayPos = 0;
        int rightArrayPos = 0;
        while (leftArrayPos < leftArray.length && rightArrayPos < rightArray.length) {
            if (leftArray[leftArrayPos] <= rightArray[rightArrayPos]) {
                mergedArray[mergedArrayPos] = leftArray[leftArrayPos];
                leftArrayPos++;
            } else {
                mergedArray[mergedArrayPos] = rightArray[rightArrayPos];
                rightArrayPos++;
            }
            mergedArrayPos++;
        }

        while (leftArrayPos < leftArray.length) {
            mergedArray[mergedArrayPos] = leftArray[leftArrayPos];
            leftArrayPos++;
            mergedArrayPos++;
        }

        while (rightArrayPos < rightArray.length) {
            mergedArray[mergedArrayPos] = rightArray[rightArrayPos];
            rightArrayPos++;
            mergedArrayPos++;
        }

        return mergedArray;
    }
}
