package com.example.service;

import com.example.dao.LockInfoRepository;
import com.example.model.LockInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * <p>分布式锁服务</p>
 * 基于数据库表字段的唯一索引
 * Created by hanqf on 2020/7/17 15:01.
 */

@Service
public class LockService {

    @Autowired
    private LockInfoRepository lockInfoRepository;

    /**
     * <p>尝试获得锁</p>
     * 只有两种情况下可以获得锁：
     * 1.锁不存在
     * 2.锁过期，正常情况下应该谁创建谁释放，但是又是业务可能异常，导致没有执行释放操作，所以这里通过过期时间判断是否可以获得锁，如果已经过期，则采用先珊后建的方式重新获得锁
     *
     * @param tag            锁标识，类似于redis的key，该字段必须创建唯一索引
     * @param expiredSeconds 过期时间，单位秒
     * @return boolean
     * @author hanqf
     * 2020/7/17 15:23
     */
    @Transactional(rollbackFor = Throwable.class)
    public boolean tryLock(String tag, Integer expiredSeconds) {
        if (StringUtils.isEmpty(tag)) {
            throw new NullPointerException();
        }
        LockInfo lockInfo = lockInfoRepository.findByTag(tag);
        if (Objects.isNull(lockInfo)) {
            //因为创建了唯一索引，所以只有一个线程能够保存成功
            lockInfoRepository.save(new LockInfo(tag, LocalDateTime.now().plusSeconds(expiredSeconds)));
            return true;
        } else {
            LocalDateTime expiredTime = lockInfo.getExpirationTime();
            LocalDateTime now = LocalDateTime.now();
            if (expiredTime.isBefore(now)) {
                //看到好多网上的例子，这里都是用的update过期时间方式，多线程情况下会导致多个线程获得锁，所以这里先删除原来的锁，再通过save创建新的锁
                //删除锁时，避免多线程情况下删除了新创建的数据，所以这里要带上过期时间参数
                lockInfoRepository.deleteByTagAndExpirationTime(tag,expiredTime);
                lockInfoRepository.save(new LockInfo(tag, LocalDateTime.now().plusSeconds(expiredSeconds)));
                return true;
            }
        }
        return false;
    }


    /**
     * <p>释放锁</p>
     *
     * @param tag 锁标识，类似于redis的key，该字段必须创建唯一索引
     * @return void
     * @author hanqf
     * 2020/7/17 15:27
     */
    @Transactional(rollbackFor = Throwable.class)
    public void unlock(String tag) {
        if (StringUtils.isEmpty(tag)) {
            throw new NullPointerException();
        }
        lockInfoRepository.deleteByTag(tag);
    }

}
