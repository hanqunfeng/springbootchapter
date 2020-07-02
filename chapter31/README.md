# SpringBoot 优雅关闭服务 springboot2.3+支持

## pom.xml
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

## application.properties
```properties
# 开启优雅关闭，Spring Boot 优雅关闭需要配合 Actuator 的 /shutdown 端点来进行触发
# 需要说明的是，Tomcat、Jetty 在网络层会立即停止接收请求，而 Undertow 可以继续接收请求，但会立即返回 503 服务不可用错误。Tomcat 生效版本需要：9.0.33+
server.shutdown=graceful
# 关闭的缓冲时间，默认就是30s，当配置了一个优雅关闭的缓冲时间，直到应用程序关闭时，Web 服务器都不再允许接收新的请求，缓冲时间是为了等待目前所有进行中的活动请求处理完成。
spring.lifecycle.timeout-per-shutdown-phase=30s
# 必须post请求
management.endpoint.shutdown.enabled=true
management.endpoints.web.exposure.include=*
management.endpoints.web.base-path=/actuator
management.server.address=127.0.0.1
management.server.port=8888
```

## 关机方式
```properties
http://localhost:8888/actuator/ # 查看所有的检测点
http://localhost:8888/actuator/shutdown # 必须post访问

curl http://localhost:8888/actuator/shutdown -X POST   
```
