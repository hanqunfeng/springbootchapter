package org.example.registerModel;

import org.example.util.NoRegister;

/**
 * <h1></h1>
 * Created by hanqf on 2020/12/15 15:47.
 */

@NoRegister
public interface Parent {
    String output();

    //接口的default方法，代理时只能重写了
    default String defaultPrint(String s) {
        return s;
    }
}
