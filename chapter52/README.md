# 实际上war包的构建方式，将其打包方式修改为jar就可以转为jar运行方式

# 核心的关键就是tomcat依赖的引入方式
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-tomcat</artifactId>
    <scope>provided</scope>
</dependency>
```

* 说明
```
默认compile，这里provided表示，如果打成war包，
则该依赖不会被放到WEB-INF/lib下，而是将其放到WEB-INF/lib-provided下，
这么做的目的就是表示该依赖要求由容器提供，比如tomcat相关的包，
只不过此时war包中会包含不必要的依赖，体积会大，
打成jar则不受影响，因为spring-boot-starter-web已经包含该依赖。
```
