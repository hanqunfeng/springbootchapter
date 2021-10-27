package com.example.csv.demo;

import com.opencsv.bean.CsvBindAndSplitByName;
import com.opencsv.bean.CsvBindByName;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1>按表头写csv时的表头排序工具类</h1>
 * Created by hanqf on 2021/10/19 17:37.
 *
 * @CsvBindByName
 * @CsvBindAndSplitByName
 */


public class CsvColumnOrderOnWriteHelper {

    //比较方法
    public static int compare(String o1, String o2, Class clazz) {
        final Field[] declaredFields = clazz.getDeclaredFields();
        Map<String, Integer> orderHeader = new HashMap<>();
        for (Field field : declaredFields) {
            String column = null;
            int order = 0;
            CsvBindByName csvBindByName = field.getAnnotation(CsvBindByName.class);
            if (csvBindByName != null) {
                column = csvBindByName.column();
            }

            CsvBindAndSplitByName csvBindAndSplitByName = field.getAnnotation(CsvBindAndSplitByName.class);
            if (csvBindAndSplitByName != null) {
                column = csvBindAndSplitByName.column();
            }

            CsvOrder csvOrder = field.getAnnotation(CsvOrder.class);
            if (csvOrder != null) {
                order = csvOrder.order();
            }
            //数组排序
            orderHeader.put(column.toUpperCase(), order);
        }

        //比较顺序，order越小越靠前
        if (orderHeader.get(o1) > orderHeader.get(o2)) {
            return 1;
        } else if (orderHeader.get(o1) < orderHeader.get(o2)) {
            return -1;
        } else {
            return 0;
        }
    }
}
