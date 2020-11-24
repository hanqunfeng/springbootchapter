# reactive-redis-cache-annotation-spring-boot-starter----响应式Redis方法缓存注解，starter
## 说明
本项目是为webflux项目提供方法缓存的支持，只支持响应式方法，即方法返回类型必须是Mono或者Flux。

## 使用方法
### 加入依赖
```maven
<dependency>
  <groupId>com.hanqf</groupId>
  <artifactId>reactive-redis-cache-annotation-spring-boot-starter</artifactId>
  <version>1.0.0</version>
</dependency>
```

```gradel
implementation 'com.hanqf:reactive-redis-cache-annotation-spring-boot-starter:1.0.0'
```


### 其它依赖
```gradle
    //webflux
    implementation 'org.springframework.boot:spring-boot-starter-webflux'
    
    //Spring Boot Redis 依赖
    implementation 'org.springframework.boot:spring-boot-starter-data-redis'
    //redis lettuce连接池依赖
    implementation 'org.apache.commons:commons-pool2'

    //aop 面向方面编程 支持@Aspect
    implementation 'org.springframework.boot:spring-boot-starter-aop'
```

### 示例
使用方式与springboot提供的Cacheable，CachePut，CacheEvict和Caching类似。
```java
@Service
public class SysUserService{
    @Autowired
    SysUserRepository sysUserRepository;
    
    //方法缓存，key支持EL表达式
    @ReactiveRedisCacheable(cacheName = "sysuser", key = "'find_' + #username")
    public Mono<SysUser> findUserByUsername(String username) {
        return sysUserRepository.findByUsername(username);
    }

    @ReactiveRedisCacheable(cacheName = "sysuser", key = "all")
    public Flux<SysUser> findAll() {
        return sysUserRepository.findAll();
    }

    //清除以"sysuser_"开头的全部key
    @ReactiveRedisCacheEvict(cacheName = "sysuser", allEntries = true)
    public Mono<SysUser> add(SysUser sysUser) {
        return sysUserRepository.addSysUser(sysUser.getId(), sysUser.getUsername(), sysUser.getPassword(), sysUser.getEnable()).flatMap(data -> sysUserRepository.findById(sysUser.getId()));
    }

    //组合
    @ReactiveRedisCaching(
            evict = {@ReactiveRedisCacheEvict(cacheName = "sysuser", key = "all")},
            put = {@ReactiveRedisCachePut(cacheName = "sysuser", key = "'find_' + #sysUser.username")}
    )
    public Mono<SysUser> update(SysUser sysUser) {
        Mono<SysUser> save = sysUserRepository.save(sysUser);
        return save;
    }

    @ReactiveRedisCacheEvict(cacheName = "sysuser", allEntries = true)
    @Transactional(rollbackFor = {Throwable.class})
    public Mono<Boolean> deleteByUserName(String username) {
        return sysUserRepository.deleteByUsername(username);
    }
}
```

