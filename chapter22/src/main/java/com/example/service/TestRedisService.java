package com.example.service;/**
 * Created by hanqf on 2020/3/30 15:19.
 */


import com.example.support.CP_CacheEvict;
import org.springframework.cache.annotation.*;
import org.springframework.stereotype.Service;

/**
 * @author hanqf
 * @date 2020/3/30 15:19
 */
@Service
//缓存key的前缀
@CacheConfig(cacheNames = "commonCache")
public class TestRedisService {

    //先查询缓存，如果存在则返回缓存的值，不存在则执行方法，并将返回值存储到缓存
    @Cacheable(key = "'RedisService.getValueByKey_'+#key")
    public String getValueByKey(String key) {
        System.out.println("TestRedisService getValueByKey_" + key);
        return key + "aa";
    }

    /**
     * sync = true 表示多个线程同时访问该方法时，如果缓存里没有值，则只有一个线程可以访问，其它线程等待，也就是说其它线程会直接返回redis缓存的值
     * 设置sync = true，可以防止"缓存击穿"
     */
    @Cacheable(key = "'RedisService.getValueByKey2_'+#key", sync = true)
    public String getValueByKey2(String key) {
        System.out.println("TestRedisService getValueByKey2_" + key);
        return key + "aa";
    }

    //会执行方法，并将结果放入缓存，下次依然是先执行方法，这里有个条件判断，就是当返回结果不为null时才会缓存结果
    @CachePut(key = "'RedisService.getValueByKey3_'+#key", condition = "#result != 'null'")
    public String getValueByKey3(String key) {
        System.out.println("TestRedisService getValueByKey3_" + key);
        return key + "aa";
    }

    //清除掉指定key的缓存

    /**
     * 注意，这里@CacheEvict不支持模糊匹配
     */
    @CacheEvict(key = "'RedisService.getValueByKey_'+#key")
    public void deleteByKey(String key) {
        System.out.println("TestRedisService deleteByKey_" + key);
    }

    //beforeInvocation = true 调用方法前就清空缓存，默认false

    /**
     * 注意，这里@CacheEvict不支持模糊匹配
     */
    @CacheEvict(allEntries = true, beforeInvocation = true)
    public void deleteAllkey() {
        System.out.println("TestRedisService deleteAllkey");
    }

    //一次可以配置多个类型

    /**
     * 注意，这里@CacheEvict不支持模糊匹配
     */
    @Caching(evict = {
            @CacheEvict(key = "'RedisService.getValueByKey_'+#key"),
            @CacheEvict(key = "'RedisService.getValueByKey2_'+#key")
    }, put = {
            @CachePut(key = "'RedisService.getValueByKey3_'+#key", condition = "#result != 'null' and #key.length() > 2")
    })
    public String deleteCache(String key) {
        return key + "aa";
    }


    /**
     * 支持模糊匹配
     */
    @CP_CacheEvict(key = {
            "'RedisService.getValueByKey_'+#key+'*'",
            "'RedisService.getValueByKey2_*'"
    })
    public String deleteCache2(String key) {
        return key + "aa";
    }
}
