package com.example.service;/**
 * Created by hanqf on 2020/3/30 15:19.
 */


import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * @author hanqf
 * @date 2020/3/30 15:19
 */
@Service
@CacheConfig(cacheNames = "commonCache")
public class RedisService {

    @Cacheable(key = "'RedisService.getValueByKey_'+#key")
    public String  getValueByKey(String key){
        System.out.println("RedisService getValueByKey_"+key);
        return key + "aa";
    }

    @Cacheable(key = "'RedisService.getValueByKey2_'+#key")
    public String  getValueByKey2(String key){
        System.out.println("RedisService getValueByKey2_"+key);
        return key + "aa";
    }
    //清除掉指定key的缓存
    @CacheEvict(key="'RedisService.getValueByKey_'+#key")
    public void deleteByKey(String key) {
        System.out.println("RedisService deleteByKey_"+key);
    }

    @CacheEvict(allEntries = true, beforeInvocation = true)
    public void deleteAllkey() {
        System.out.println("RedisService deleteAllkey");
    }
}
