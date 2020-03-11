# 声明、注册，获取bean对象，关联spring-xml配置文件
## @Configuration
```java
@Configuration
@ComponentScan("org.example")
public class AppConfig {

    @Bean(name = "user2")
    //bean的作用域，这里ConfigurableBeanFactory只提供了Singleton和Prototype两种选择
    //如果是web项目，则可以使用WebApplicationContext提供的选项
    //这里使用SCOPE_PROTOTYPE，表示每次获取对象都新创建一个
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public User initUser() {
        return new User();
    }
}
```

## @ImportResource(value = {"classpath:spring-other.xml"})
```java
@Configuration
@ImportResource(value = {"classpath:spring-other.xml"})
public class OtherConfig {
}
```
