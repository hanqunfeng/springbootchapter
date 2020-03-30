# springboot redis集群 不支持事务

```
#设置密码
spring.redis.password=password
#集群节点
spring.redis.cluster.nodes=127.0.0.1:30001,127.0.0.1:30002,127.0.0.1:30003,127.0.0.1:30004,127.0.0.1:30005,127.0.0.1:30006
#最大重连次数
spring.redis.cluster.max-redirects=3
```
