# springboot redis

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
