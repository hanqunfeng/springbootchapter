package org.example.learnjuc.forkjoin.util;


import java.util.Random;

public class Utils {

	/**
	 * 随机生成数组
	 * @param size 数组的大小
	 * @return
	 */
	public static int[] buildRandomIntArray(final int size) {
		int[] arrayToCalculateSumOf = new int[size];
		Random generator = new Random();
		for (int i = 0; i < arrayToCalculateSumOf.length; i++) {
			arrayToCalculateSumOf[i] = generator.nextInt(100000000);
		}
		return arrayToCalculateSumOf;
	}
}
