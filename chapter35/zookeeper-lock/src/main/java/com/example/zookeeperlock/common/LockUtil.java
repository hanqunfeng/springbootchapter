package com.example.zookeeperlock.common;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.recipes.locks.InterProcessMutex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * <p></p>
 * Created by hanqf on 2020/7/18 15:55.
 */

@Component
public class LockUtil {

    public String path = "/path/test";
    @Autowired
    private CuratorFramework curatorFramework;
    private Map<String, InterProcessMutex> map = new HashMap<>();

    @PostConstruct
    public void init() {
        map.put(path, new InterProcessMutex(this.curatorFramework, path));
    }


    /**
     * <p>获得锁</p>
     *
     * @param path
     * @param timeOut
     * @param timeUnit
     * @return boolean
     * @author hanqf
     * 2020/7/20 22:28
     */
    public boolean tryLock(String path, long timeOut, TimeUnit timeUnit) {
        //用于标识是否获取了锁
        boolean acquire = false;
        try {
            acquire = map.get(path).acquire(timeOut, timeUnit);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return acquire;
    }

    /**
     * <p>释放锁</p>
     *
     * @param path
     * @author hanqf
     * 2020/7/20 22:28
     */
    public void releaseLock(String path) {
        try {
            map.get(path).release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
