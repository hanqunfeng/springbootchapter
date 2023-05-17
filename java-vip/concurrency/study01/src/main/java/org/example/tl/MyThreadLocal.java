package org.example.tl;

import java.util.HashMap;
import java.util.Map;

/**
 * 类说明：自己实现的ThreadLocal
 */
public class MyThreadLocal<T> {
    /*存放变量副本的map容器，以Thread为键，变量副本为value*/
    private Map<Thread,T> threadTMap = new HashMap<>();

    public synchronized T get(){
        return  threadTMap.get(Thread.currentThread());
    }

    public synchronized void set(T t){
        threadTMap.put(Thread.currentThread(),t);
    }

}
