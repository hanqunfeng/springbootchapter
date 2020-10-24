# Springboot 全局异常处理(json，view) 全局返回值响应 @Valid jasypt属性值加密 一个相对完整的web接口应用

## jasypt
* [官网](https://github.com/ulisesbocchio/jasypt-spring-boot)
* [参考资料](https://blog.csdn.net/wangmx1993328/article/details/106421101)

## 密码传递方式
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

## 命令行加解密
```shell script
# jar下载地址：https://repo1.maven.org/maven2/org/jasypt/jasypt/1.9.3/jasypt-1.9.3.jar
# 实际上使用maven或者gradle配置jasypt-spring-boot-starter依赖后，这个jar就已经下载到本地仓库了，去本地仓库找找吧
#加密
java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringEncryptionCLI password=123456 algorithm=PBEWithMD5AndDES input=newpwd

#解密
java -cp jasypt-1.9.3.jar org.jasypt.intf.cli.JasyptPBEStringDecryptionCLI password=123456 algorithm=PBEWithMD5AndDES input=+Dgb76G78U6GIALlLDJUfw==
```

## Thymeleaf 如何支持java8的时间LocalDate和LocalDatetime
* 参考：[https://www.cnblogs.com/asker009/p/9370603.html](https://www.cnblogs.com/asker009/p/9370603.html)
* 注意，不需要引入jar
```
在java8的java.time中使用：

${#temporals.day(date)}

${#temporals.month(date)}

${#temporals.monthName(date)}

${#temporals.monthNameShort(date)}

${#temporals.year(date)}

${#temporals.dayOfWeek(date)}

${#temporals.dayOfWeekName(date)}

${#temporals.dayOfWeekNameShort(date)}

${#temporals.hour(date)}

${#temporals.minute(date)}

${#temporals.second(date)}

${#temporals.millisecond(date)}
```