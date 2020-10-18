# springboot redis 事务 批量执行 Lua脚本 分布式锁 RedisRepository Jackson2HashMapper

```java
@Configuration
public class RedisConfig {

    @Autowired
    private RedisTemplate redisTemplate;

    @PostConstruct
    public void init(){
        RedisSerializer stringSerializer = redisTemplate.getStringSerializer();
        redisTemplate.setKeySerializer(stringSerializer);
        //redisTemplate.setValueSerializer(stringSerializer); //value可以不设置，这里只是为了在终端查看方便
        redisTemplate.setHashKeySerializer(stringSerializer);
        redisTemplate.setHashValueSerializer(stringSerializer); //value可以不设置，这里只是为了在终端查看方便
    }
}
```
## 事务
```java
        //这里设置一个监控key
        redisTemplate.opsForValue().set("key", "yes");
        List list = (List)redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //1.监控key是否发生变化
                redisOperations.watch("key");
                //2.开启事务，在exec命令执行前，全部都只是进入队列
                redisOperations.multi();
                redisOperations.opsForValue().set("t1", "value01");
                //对字符串做自增会报错，但是不会影响事务执行
                redisOperations.opsForValue().increment("key2",1);
                redisOperations.opsForValue().set("t2", "value02");
                redisOperations.opsForValue().set("t3", "value03");
                //3.执行exec命令，先判断key是否在监控后发生变化，如果是则不执行事务操作，否则就执行
                //每成功执行一条命令就会返回true(void方法)或者实际方法返回值，如increment返回Long
                return redisOperations.exec();
            }
        });
```