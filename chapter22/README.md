# springboot redis注解式缓存，分组缓存配置

# 名词解释
* 缓存穿透：redis和数据库都不含有对应的值，每次请求都会查询数据库。解决方案是redis缓存null值。
* 缓存击穿：redis中的某个热点数据到期，刚好此时有大量的请求进行访问该数据，会导致大量请求传递的数据库。解决方案，sync = true，即某只允许一个线程获取数据，带缓存生效后其它线程从缓存获取数据
* 缓存雪崩：大量的缓存数据在同一个时刻过期，导致大量的请求进行访问该数据。解决方案，不同类型的数据设置不同的到期时间，避免使用统一的过期时间，可以使用分组缓存配置策略。

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