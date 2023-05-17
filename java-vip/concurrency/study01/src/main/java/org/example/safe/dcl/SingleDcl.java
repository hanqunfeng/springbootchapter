package org.example.safe.dcl;

/**
 * 懒汉式-双重检查
 */
public class SingleDcl {
    private volatile static SingleDcl singleDcl;
    //私有化
    private SingleDcl(){
    }

    public static SingleDcl getInstance(){
        if (singleDcl == null){ //第一次检查，不加锁
            System.out.println(Thread.currentThread()+" is null");
            synchronized(SingleDcl.class){ //加锁
                if (singleDcl == null){ //第二次检查，加锁情况下
                    System.out.println(Thread.currentThread()+" is null");
                    //内存中分配空间  1
                    //空间初始化 2
                    //把这个空间的地址给我们的引用  3
                    singleDcl = new SingleDcl();
                }
            }
        }
        return singleDcl;
    }
}
