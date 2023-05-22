package org.example.safe.dcl;

/**
 * 懒汉式-延迟初始化占位类模式
 */
public class SingleInit {
    private SingleInit(){}

    //只有第一次用到这个类时才会初始化static对象或方法
    private static class InstanceHolder{
        private static SingleInit instance = new SingleInit();
    }

    public static SingleInit getInstance(){
        return InstanceHolder.instance;
    }

}
