package com.example.zookeeperlock.common;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * <p>可重入共享锁组件</p>
 * Created by hanqf on 2020/7/18 15:34.
 */


@Component
public class ShardReentrantLockComponent {

    @Autowired
    private CuratorFramework curatorFramework;

    /**
     * 该方法为模板方法，获得锁后回调 BaseLockHandler 中的 handler 方法
     * @return
     */
    public <T> T acquireLock(BaseLockHandler<T> baseLockHandler) {
        //获取要加锁的路径
        String path = baseLockHandler.getPath();
        //获取超时时间
        int timeOut = baseLockHandler.getTimeOut();
        //时间单位
        TimeUnit timeUnit = baseLockHandler.getTimeUnit();
        //通过 InterProcessMutex 该类来获取可重入共性锁
        InterProcessMutex lock = new InterProcessMutex(this.curatorFramework, path);
        //用于标识是否获取了锁
        boolean acquire = false;
        try {
            try {
                //成功获得锁后返回 true
                acquire = lock.acquire(timeOut, timeUnit);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(acquire) {
                //获得锁后回调具体的业务逻辑
                return baseLockHandler.handler();
            } else {
                //没有获得锁返回 null
                return null;
            }
        } finally {
            try {
                if(acquire) {
                    //释放锁
                    lock.release();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}
