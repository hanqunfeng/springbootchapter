# Springboot SpringSecurity JWT
## web-flux

## 核心流程
```
用户请求
    --》JwtAuthenticationTokenFilter （将获取到的token放入上下文中），.addFilterAfter(jwtAuthenticationTokenFilter, SecurityWebFiltersOrder.FIRST)
    --》JwtSecurityContextRepository （获取上下文中的token，调用JwtAuthenticationManager的authenticate方法），.securityContextRepository(jwtSecurityContextRepository)
    --》JwtAuthenticationManager （从token中获取用户信息，然后封装用户令牌，验证用户是否有效）
```

## 响应式Mysql
```gradle
//r2dbc mysql 库，虽然是第三方库，但是已经被springboot纳入版本管理
implementation 'dev.miku:r2dbc-mysql'
//Spring r2dbc 抽象层
implementation 'org.springframework.boot:spring-boot-starter-data-r2dbc'
```

### 注意事项
* 推荐继承ReactiveSortingRepository
* 新增操作时，如果主键使用的不是数据库自动维护的主键（比如自增主键），而是需要在新增时指定主键的时候，使用save方法，会被认为是更新操作，也许是个bug，目前的解决方案可以是自定义一个新增方法，如
```java
/**
 * 返回值可以是Mono<Integer>或Mono<Boolean>。
*/
@Modifying
@Query("insert into sys_user (id,username,password,enable) values (:id,:username,:password,:enable)")
Mono<Boolean> addSysUser(String id, String username, String password, Boolean enable);
```
* 事务，使用方式和非响应式编程一致，SpringBoot会根据方法返回类型来决定事务的控制方式
