# Springboot 日志 log4j2 
* 单独的log4j2.xml优先级最高，其次是log4j2-spring.xml，如果分环境配置，可以将名称设置为log4j2-dev.xml的形式，然后在各自环境配置文件中指定
```yaml
# 日子配置
logging:
  config: classpath:log4j2-prod.xml
```
* 使用时，根据需要任意设置一种即可，个人推荐使用log4j2-dev.xml

```xml
<!--            输出日志的格式-->
<!--            %d,%d{yyyy-MM-dd HH:mm:ss.SSS} 表示输出到毫秒的时间-->
<!--            %t 输出当前线程名称-->
<!--            %p 输出日志级别-->
<!--            %-5level 输出日志级别，-5表示左对齐并且固定输出5个字符，如果不足在右边补0-->
<!--            %logger 输出logger名称，因为Root Logger没有名称，所以没有输出-->
<!--            %msg, %m 日志文本-->
<!--            %n 换行-->
<!--            %C 输出类全路径名称-->
<!--            %F 输出所在的类文件名，如Log4j2Test.java-->
<!--            %L 输出行号-->
<!--            %M 输出所在方法名-->
<!--            %l 输出语句所在的行数, 包括类名、方法名、文件名、行数-->
<!--            disableAnsi="false" noConsoleNoAnsi="false" : 彩色日志的效果，实测不设置也可以彩色输出，应该是默认值-->
            <PatternLayout pattern="[%style{%d{yyyy-MM-dd HH:mm:ss.SSS}}{green}] [%highlight{%-5level}] [%style{%t}{blue}] - %style{%C}{bright,magenta} - %L : %style{%m%n}{cyan}%style{%throwable}{red}"
                           disableAnsi="false" noConsoleNoAnsi="false"/>

```


