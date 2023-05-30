package org.example.learnjuc.forkjoin.recursiveaction;

import java.util.Arrays;
import java.util.concurrent.RecursiveAction;

/**
 * 利用fork-join实现数组排序
 */
public class MergeSortTask extends RecursiveAction {

	private final int threshold; //拆分的阈值，低于此阈值就不再进行拆分
	private int[] arrayToSort; //要排序的数组

	public MergeSortTask(final int[] arrayToSort, final int threshold) {
		this.arrayToSort = arrayToSort;
		this.threshold = threshold;
	}

	@Override
	protected void compute() {
		//拆分后的数组长度小于阈值，直接进行排序
		if (arrayToSort.length <= threshold) {
			// 调用jdk提供的排序方法
			Arrays.sort(arrayToSort);
			return;
		}

		// 对数组进行拆分
		int midpoint = arrayToSort.length / 2;
		int[] leftArray = Arrays.copyOfRange(arrayToSort, 0, midpoint);
		int[] rightArray = Arrays.copyOfRange(arrayToSort, midpoint, arrayToSort.length);

		MergeSortTask leftTask = new MergeSortTask(leftArray, threshold);
		MergeSortTask rightTask = new MergeSortTask(rightArray, threshold);

		//调用任务,阻塞当前线程，直到所有子任务执行完成
		invokeAll(leftTask,rightTask);
		//提交任务
//		leftTask.fork();
//		rightTask.fork();
//		//合并结果
//		leftTask.join();
//		rightTask.join();

		// 合并排序结果
		arrayToSort = MergeSort.merge(leftTask.getSortedArray(), rightTask.getSortedArray());
	}

	private int[] getSortedArray() {
		return arrayToSort;
	}

	private int[] merge(final int[] leftArray, final int[] rightArray) {
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
