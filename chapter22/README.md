# springboot redis注解式缓存

参考：https://hanqunfeng.iteye.com/admin/blogs/1158824

```java
@Service
@CacheConfig(cacheNames = "commonCache")
public class RedisService {

    @Cacheable(key = "'RedisService.getValueByKey_'+#key")
    public String  getValueByKey(String key){
        System.out.println("RedisService getValueByKey_"+key);
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
```