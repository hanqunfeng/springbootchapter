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
http://127.0.0.1:8091

## 关闭seata-seata
```shell
kill -9 `jps | grep seata-server | awk '{print $1}'`
```
