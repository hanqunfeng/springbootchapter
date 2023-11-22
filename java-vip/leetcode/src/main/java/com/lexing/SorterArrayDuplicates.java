package com.lexing;

/**
 * <h1>删除排序数组中的重复项，获取去重后的元素个数</h1>
 * 一个有序数组array，原地删除重复出现的元素，使每个元素只出现一次，返回删除后数组的新长度
 * 不能使用额外的数组，必须原地修改输入数组并使用o(1)额外空间的条件下完成
 * Created by hanqf on 2023/10/8 17:53.
 */


public class SorterArrayDuplicates {

    /**
     * 双指针法
     * 一个慢指针，负责记录不重复元素的位置
     * 一个快指针，负责遍历全部元素
     * <br>
     * 它接受一个整数数组作为输入，并返回删除所有重复元素后的新数组的长度。
     * 该方法使用两个指针 `i` 和 `j` 来遍历数组。
     * `i` 指针用于跟踪最后一个非重复元素的索引，而 `j` 指针用于将每个元素与它前面的元素进行比较。
     * 如果两个元素不同，则将 `i` 指针加1，并将 `j` 元素的值赋给数组中 `i` 位置的元素。
     * 这确保了 `i` 指针始终指向最后一个非重复元素。
     * 该方法在所有重复元素都被删除后返回新数组的长度。
     * 如果输入数组为空，则该方法返回 0。
    */
    public static int removeDuplicates(int[] array) {
        if (array.length == 0) {
            return 0;
        }
        int i = 0; //慢指针
        //j为快指针
        for (int j = 1; j < array.length; j++) {
            if (array[j] != array[i]) { //比较前后两个元素是否相同
                i++; //不相同时，i向前移动一位
                array[i] = array[j]; //不相同时，将j位置的元素赋值给移动后的i位置的元素，这样保证i经过的元素都是不重复的
            }
        }
        return i + 1;
    }

    public static void main(String[] args) {
        //定义一个有序数组
        int[] array = new int[]{1,2,2,3,3,4,5};
        System.out.println(removeDuplicates(array));
    }
}
