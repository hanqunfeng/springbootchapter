## seata官网
https://seata.io/zh-cn/index.html
seata从1.7.0版本开始兼容jdk17和spring6，这就意味着它支持springboot3

## 每个数据库都要创建undo_log表，建表语句在 `sql`目录下
## 启动seata-server
```shell
~/develop_soft/seata/bin/seata-server.sh
```

## 查看日志
```shell
tail -f ~/develop_soft/seata/logs/start.out
```
## 控制台
http://127.0.0.1:7091  seata/seata

## 服务连接地址
127.0.0.1:8091

## 关闭seata-seata
```shell
kill -9 `jps | grep seata-server | awk '{print $1}'`
```

## 使用方法
* 依赖
```xml
<!-- seata 1.7.0开始才支持springboot3 -->
<dependency>
    <groupId>io.seata</groupId>
    <artifactId>seata-spring-boot-starter</artifactId>
    <version>1.7.0</version>
</dependency>
```
* 方法或类上添加如下注解
```java
// timeoutMills: 超时时间，单位毫秒，name: 事务名称
@GlobalTransactional(timeoutMills = 30000,name = "business-tx")
```
* application.properties
```properties
# 开启seata，分布式事务
seata.enabled=true
# seata应用的编号
seata.application-id=${spring.application.name}
# seata事务组编号，用于TC集群名
seata.tx-service-group=${spring.application.name}-group
# 虚拟组和分组的映射，注意这里 mybatis-plus-multi-db-demo-group 与上面 seata.tx-service-group的值相同
seata.service.vgroup-mapping.mybatis-plus-multi-db-demo-group=default

# 分组和seata服务的映射，注意这里 default 与上面虚拟组 seata.service.vgroup-mapping.mybatis-plus-multi-db-demo-group 的值要相同
seata.service.grouplist.default=127.0.0.1:8091

# 以下为默认值
seata.config.type=file
seata.registry.type=file
```

