## rocketmq 4.8.0 新版 启动

### server 有日志目录映射
* 设置目录权限
```shell
chmod 777 $(pwd)/logs
```
* 启动
```shell
docker run -d -v $(pwd)/logs:/home/rocketmq/logs \
-v $(pwd)/logs:/home/rocketmq/logs \
--name rmqnamesrv \
-e "JAVA_OPT_EXT=-Xms512M -Xmx512M -Xmn128m" \
-p 9876:9876 \
foxiswho/rocketmq:4.8.0 \
sh mqnamesrv
```


### broker 目录映射
* 设置目录权限
```shell
chmod 777 $(pwd)/logs
chmod 777 $(pwd)/store
chmod 777 $(pwd)/conf
```

* 添加文件：$(pwd)/conf/broker.conf
```shell
brokerClusterName = DefaultCluster
brokerName = broker-a
brokerId = 0
deleteWhen = 04
fileReservedTime = 48
brokerRole = ASYNC_MASTER
flushDiskType = ASYNC_FLUSH
brokerIP1 = {宿主机 IP}
```
* 启动
```shell
docker run -d  
-v $(pwd)/logs:/home/rocketmq/logs
-v $(pwd)/store:/home/rocketmq/store \
-v $(pwd)/conf:/home/rocketmq/conf \
--name rmqbroker --link rmqnamesrv:rmqnamesrv \
-e "NAMESRV_ADDR=rmqnamesrv:9876" \
-e "JAVA_OPT_EXT=-Xms512M -Xmx512M -Xmn128m" \
-p 10911:10911 -p 10912:10912 -p 10909:10909 \
foxiswho/rocketmq:4.8.0 \
sh mqbroker -c /home/rocketmq/conf/broker.conf
```

### console
* 启动
```shell
docker run --name rmqconsole --link rmqnamesrv:rmqnamesrv \
-e "JAVA_OPTS=-Drocketmq.namesrv.addr=rmqnamesrv:9876 -Dcom.rocketmq.sendMessageWithVIPChannel=false" \
-p 8180:8080 -t styletang/rocketmq-console-ng
```

### 访问地址
http://localhost:8180/
