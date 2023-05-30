package org.example.learnjuc.jmm;

/**
 * @author Fox
 * DCL为什么要使用volatile
 */
public class Singleton {

    private volatile static Singleton singleton;

    private Singleton() {
    }

    /**
     * 双重检查锁定（Double-checked Locking）实现单例对象的延迟初始化
     *
     * @return
     */
    public static Singleton getSingleton() {
        if (singleton == null) {
            synchronized (Singleton.class) {
                if (singleton == null) {
//                    memory = allocate(); //1. 分配对象内存空间
//                    instance = memory; //3.设置instance指向刚刚分配的内存地址
//                    ctorInstance(memory);  //2.初始化对象

                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }

}
