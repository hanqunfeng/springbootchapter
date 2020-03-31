# springboot redis注解式缓存

参考：https://hanqunfeng.iteye.com/admin/blogs/1158824

```java
@Service
//缓存key的前缀
@CacheConfig(cacheNames = "commonCache")
public class RedisService {

    //先查询缓存，如果存在则返回缓存的值，不存在则执行方法，并将返回值存储到缓存
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

    //会执行方法，并将结果放入缓存，下次依然是先执行方法，这里有个条件判断，就是当返回结果不为null时才会缓存结果
    @CachePut(key = "'RedisService.getValueByKey3_'+#key",condition = "#result != 'null'")
    public String  getValueByKey3(String key){
        System.out.println("RedisService getValueByKey3_"+key);
        return key + "aa";
    }

    //清除掉指定key的缓存
    @CacheEvict(key="'RedisService.getValueByKey_'+#key")
    public void deleteByKey(String key) {
        System.out.println("RedisService deleteByKey_"+key);
    }

    //beforeInvocation = true 调用方法前就清空缓存，默认false
    @CacheEvict(allEntries = true, beforeInvocation = true)
    public void deleteAllkey() {
        System.out.println("RedisService deleteAllkey");
    }
}
```