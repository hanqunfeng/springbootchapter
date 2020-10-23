# Springboot 全局异常处理 全局返回值响应 @Valid jasypt属性值加密 一个相对完整的web接口应用

## jasypt
* [官网](https://github.com/ulisesbocchio/jasypt-spring-boot)
* [参考资料](https://blog.csdn.net/wangmx1993328/article/details/106421101)

## 
```shell script
#启动参数
java -jar target/jasypt-spring-boot-demo-0.0.1-SNAPSHOT.jar --jasypt.encryptor.password=password
#系统属性
java -Djasypt.encryptor.password=password -jar target/jasypt-spring-boot-demo-0.0.1-SNAPSHOT.jar
#环境变量
jasypt:
    encryptor:
        password: ${JASYPT_ENCRYPTOR_PASSWORD:}

JASYPT_ENCRYPTOR_PASSWORD=password java -jar target/jasypt-spring-boot-demo-1.5-SNAPSHOT.jar
```