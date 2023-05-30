package org.example.learnjuc.forkjoin.recursivetask;


import org.example.learnjuc.forkjoin.util.Utils;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;


/**
 * @author Fox
 *
 * 利用ForkJoinPool计算1亿个整数的和
 */
public class LongSumMain {
	// 获取逻辑处理器数量 12
	static final int NCPU = Runtime.getRuntime().availableProcessors();


	public static void main(String[] args) throws Exception {
		//准备数组
		int[] array = Utils.buildRandomIntArray(100000000);
		//构建任务
		LongSum ls = new LongSum(array, 0, array.length);
		// 构建ForkJoinPool
  		ForkJoinPool fjp  = new ForkJoinPool(NCPU);
		//ForkJoin计算数组总和
		ForkJoinTask<Long> result = fjp.submit(ls);
		System.out.println("forkjoin sum=" + result.get());
		fjp.shutdown();

	}

}
