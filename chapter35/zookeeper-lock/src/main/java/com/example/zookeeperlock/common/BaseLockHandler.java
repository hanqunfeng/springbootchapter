package com.example.zookeeperlock.common;

import java.util.concurrent.TimeUnit;

/**
 * <p>业务逻辑抽象类</p>
 * Created by hanqf on 2020/7/18 15:33.
 */

public abstract class BaseLockHandler<T> {
    //获得锁的默认超时时间，默认为 200ms
    private static final int DEFAULT_TIME_OUT = 200;
    //加锁的资源路径
    private String path;

    public BaseLockHandler(String path) {
        this.path = path;
    }

    /**
     * 具体的业务实现逻辑，重写该方法
     * @return
     */
    public abstract T handler();

    /**
     * 返回加锁的路径
     * @return
     */
    public String getPath() {
        return this.path;
    }

    /**
     * 返回加锁的超时时间
     * @return
     */
    public int getTimeOut() {
        return DEFAULT_TIME_OUT;
    }

    /**
     * 时间单位
     * @return
     */
    public TimeUnit getTimeUnit() {
        return TimeUnit.MILLISECONDS;
    }
}
