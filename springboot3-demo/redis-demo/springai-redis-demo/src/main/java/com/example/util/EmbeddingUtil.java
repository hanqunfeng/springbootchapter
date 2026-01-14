package com.example.util;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * Created by hanqf on 2026/1/14 13:40.
 */


public class EmbeddingUtil {

    public static List<Float> vectorAsList(float[] vector) {
        List<Float> list = new ArrayList(vector.length);
        for(float f : vector) {
            list.add(f);
        }
        return list;
    }
}
