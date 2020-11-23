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
### 依赖
```gradle
//r2dbc mysql 库，虽然是第三方库，但是已经被springboot纳入版本管理
implementation 'dev.miku:r2dbc-mysql'
//Spring r2dbc 抽象层
implementation 'org.springframework.boot:spring-boot-starter-data-r2dbc'
```

### 配置
```yaml
spring:
  r2dbc:
    url: r2dbc:mysql://localhost:3306/springboot?useUnicode=true&characterEncoding=utf-8&useTimezone=true&serverTimezone=GMT%2B8
    username: root
    password: newpwd
    pool:
      enabled: true
      initial-size: 5
      max-size: 20
      max-idle-time: 30m

# 可以打印sql
logging:
  level:
    org.springframework.data.r2dbc: DEBUG
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


## 响应式编程注意事项
* 返回值为空时要有对应的处理方式
  - switchIfEmpty()
    ```java
        @GetMapping("/{userName}")
        public Mono<AjaxResponse> getSysUser(@PathVariable String userName) {
            Mono<SysUser> sysUserMono = sysUserServcie.findUserByUsername(userName);
            return sysUserMono
                    .map(AjaxResponse::success)
                    .switchIfEmpty(Mono.just(AjaxResponse.success(null)));
        }
    ```
  - defaultIfEmpty()，注意这种方式不能设置null
    ```java
          @GetMapping("/{userName}")
          public Mono<AjaxResponse> getSysUser(@PathVariable String userName) {
              Mono<SysUser> sysUserMono = sysUserServcie.findUserByUsername(userName);
              return sysUserMono
                      .defaultIfEmpty(new SysUser()) //不能设置null
                      .map(AjaxResponse::success);
          }
 
     ```
    
* 执行过程抛出异常的情况
  - 方法中处理
    ```java
        @PutMapping
        public Mono<AjaxResponse> updateSysUser(@RequestBody SysUser sysUser) {
            if (StringUtils.isEmpty(sysUser.getId())) {
                throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "更新操作主键不能为空");
            }
            sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
            Mono<SysUser> sysUserMono = sysUserServcie.update(sysUser);
            return sysUserMono.map(AjaxResponse::success);
                    .doOnError(e -> e.printStackTrace()) //打印异常信息
                    //异常时将异常信息封装到返回值中
                    .onErrorResume(throwable -> Mono.just(AjaxResponse.error(new CustomException(CustomExceptionType.USER_INPUT_ERROR,throwable.getMessage(),throwable.getClass().getName()))));
        }
    ```
  - 统一异常拦截器处理
    ```java
        @ExceptionHandler(Exception.class)
        public AjaxResponse exception(Exception e) {  
            e.printStackTrace();
            return AjaxResponse.error(new CustomException(CustomExceptionType.OTHER_ERROR,e.getMessage(),e.getClass().getName()));
        }
    ```
    
* 以上两种情况，可以通过aop拦截器统一实现----RestControllerAspect
```java
@Component
@Aspect
@Slf4j
public class RestControllerAspect {
    @Pointcut("execution(* com.example.jwtresourcewebfluxdemo.controller.*.*(..))")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        log.info("RestControllerAspect around....");
        MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
        Method method = methodSignature.getMethod();
        String returnTypeName = method.getReturnType().getSimpleName();

        //实际执行的方法
        Object proceed = proceedingJoinPoint.proceed();
        if (returnTypeName.equals("Mono")) {
            return ((Mono) proceed)
                    .doOnError((Consumer<Throwable>) throwable -> throwable.printStackTrace())
                    .onErrorResume((Function<Throwable, Mono>) throwable -> Mono.just(AjaxResponse.error(new CustomException(CustomExceptionType.USER_INPUT_ERROR,throwable.getMessage(),throwable.getClass().getName()))))
                    .switchIfEmpty(Mono.just(AjaxResponse.success(null)));
        } else {
            return proceed;
        }
    }
}
```