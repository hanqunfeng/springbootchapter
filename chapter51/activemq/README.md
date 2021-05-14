# 1. 安装activemq
* [https://activemq.apache.org/getting-started](https://activemq.apache.org/getting-started)
* mac os : `brew search apache-activemq`

# 2. 使用
* 启动、关闭与状态查看：`activemq start`,`activemq stop`,`activemq status`
* 控制台：http://127.0.0.1:8161/admin/, admin/admin
* 查看进程和端口：`ps aux | grep activemq`,`lsof -i :61616`


# 3. 工作目录
## 3.1 配置文件
* 路径：`/usr/local/Cellar/activemq/5.16.2/libexec/conf`
* 登录web控制台的用户：`users.properties`
* 端口配置：`jetty.xml`

## 3.2 日志
* 路径：`/usr/local/Cellar/activemq/5.16.2/libexec/data`

## 3.3 内存
* 路径：`/usr/local/Cellar/activemq/5.16.2/libexec/bin/env`
* 配置参数：`ACTIVEMQ_OPTS_MEMORY="-Xms64M -Xmx1G"`

# 消息模式
* 队列模式/点对点模式(ActiveMQQueue)：生产者发布多个消息，被多个消费方接收，那么这些消费方就会瓜分这些消息，一条消息只会被一个消费方得到
* 发布订阅模式/主题模式(ActiveMQTopic)：生产者发布多个消息，被多个消费方接收，那么这些消费方就会得到全部这些消息，每条消息都会被所有消费方得到

