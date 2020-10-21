# Springboot 日志 logback 
* 单独的logback.xml优先级最高，其次是application.yml中的log配置信息，优先级最低的是logback-spring.xml
* 使用时，根据需要任意设置一种即可，个人推荐使用logback-spring.xml

```
格式化输出：%d表示日期，%thread表示线程名，%-5level：级别从左显示5个字符宽度 %logger{50}：类路径，最大显示50个字符 %M：方法名称 %L：行号 %msg：日志消息，%n是换行符
          %red红色，%green绿色，%highlight高亮蓝色，%boldMagenta粗体洋红色，%cyan蓝绿色，默认白色 只有控制台才需要设置颜色
'%red(%d{yyyy-MM-dd HH:mm:ss.SSS}) %green([%thread]) %highlight(%-5level) %boldMagenta(%logger{50}) - %L - %cyan(%msg%n)'

```


