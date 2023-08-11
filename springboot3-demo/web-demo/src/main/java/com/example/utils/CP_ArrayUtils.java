package com.example.utils;

import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * ${DESCRIPTION}
 * Created by hanqf on 2018/10/8 16:31.
 */


public class CP_ArrayUtils {

    @SuppressWarnings("unchecked")
    public static String[] mergeStringArrays(String[] array1, String[] array2){
        if (ObjectUtils.isEmpty(array1)) {
            return array2;
        } else if (ObjectUtils.isEmpty(array2)) {
            return array1;
        } else {
            LinkedHashSet set = new LinkedHashSet();
            set.addAll(Arrays.asList(array1));
            set.addAll(Arrays.asList(array2));

            String[] c = (String[]) set.toArray(new String[0]);
            return c;
        }
    }
}
