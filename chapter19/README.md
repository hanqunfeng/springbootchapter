# springboot mongoDB 复制集 事务

* 读写分离配置：spring.data.mongodb.uri=mongodb://127.0.0.1:27011,127.0.0.1:27012,127.0.0.1:27013/springboot?readPreference=secondaryPreferred&connectTimeoutMS=300000
* 事务中只能读主库，从库不能读，开启读从库会报错（Read preference in a transaction must be primary）
* 如果集合不存在，插入数据会报错，需事先好创建集合，报错信息："Cannot create namespace test.user in multi-document transaction.; nested exception is com.mongodb.MongoWriteException: Cannot create namespace test.user in multi-document transaction.",



```bash
配置三个mongod.conf 注意替换不同的路径和端口，提前创建好相关目录

systemLog:
  destination: file
  path: /Users/hanqf/myservice_dir/mongodb-replSet/node1/logs/mongo.log
  logAppend: true
storage:
  dbPath: /Users/hanqf/myservice_dir/mongodb-replSet/node1/db
net:
  port: 27011
  bindIp: 127.0.0.1
replication:
  replSetName: configRS
  oplogSizeMB: 50
processManagement:
   fork: true


启动：
mongod --config /Users/hanqf/myservice_dir/mongodb-replSet/node1/mongod.conf
mongod --config /Users/hanqf/myservice_dir/mongodb-replSet/node2/mongod.conf
mongod --config /Users/hanqf/myservice_dir/mongodb-replSet/node3/mongod.conf

注意，每次重启，主从库都可能发生变更，具体哪个是主库，要登录后查看。

任意登录一个：
mongo --host 127.0.0.1:27011

配置复制集成员：
>rs.initiate({
_id: "configRS",
version: 1,
members: [{_id: 0, host: "127.0.0.1:27011"}]
});

其实可以都配置到上面的json中一起添加
>rs.add("127.0.0.1:27012");
>rs.add("127.0.0.1:27013");


>rs.isMaster()
>rs.status()

创建数据库
>use springboot


#集合创建后会自动创建id索引,查看索引：db.user.getIndexes()
#删除集合 db.user.drop()
> db.createCollection("user")
> db.createCollection("books")
> db.createCollection("address")

> show collections

#如果插入数据时集合没有创建，则会自动创建
> db.user.insert({
    name: 'zhangsan', 
    age: 25,
    email: 'zhangsan@email.com',
    blog: 'http://zhangsan.blob.com',
    tags: ['mongodb', 'database', 'NoSQL']
});

#创建索引：1为升序，-1为降序
#查看索引：db.user.getIndexes()
#删除索引：
# 删除所有索引：db.user.dropIndexes() 
# 删除指定名称索引: db.col.dropIndex("索引名称")
> db.user.createIndex({"name":1})
> db.user.createIndex({"age":-1})


> db.address.createIndex({"userId":1})

> db.books.createIndex({"userId":1})



> db.user.find()

> db.address.find()

> db.books.find()

设置从节点可以读数据，默认从库不可以读
mongo --host 127.0.0.1:27012
注意这里一定要切换到对应的数据库在设置从库可读
use springboot
>rs.slaveOk()
>show tables
```



## 开启认证：

登录主库：mongo --host 127.0.0.1:27011
```bash
> use admin

> db.createUser(
   {
     user: "adminUser",
     pwd: "adminPass",
     roles: [ { role: "userAdminAnyDatabase", db: "admin" } ]
   }
 )

```

openssl rand -base64 753 > keyFile.key 

chmod 600 keyFile.key 

三个mongod.conf中都添加如下内容

```bash
security:
  authorization: "enabled"
  keyFile: /Users/hanqf/myservice_dir/mongodb-replSet/keyFile.key
```

重启服务器之后就需要认证才能操作了，然后在springboot库中添加用户

```bash
mongo --host 127.0.0.1:27011 -uadminUser -padminPass

> use springboot01

# 在哪个库下创建的用户，就要切换到哪个库下删除用户 use dbname, db.dropUser("username")
# 查看当前库下的用户：show users
> db.createUser(
   {
     user: "springboot",
     pwd: "123456",
     roles: [ { role: "dbOwner", db: "springboot" } ]
   }
  );      

> exit

mongo --host 127.0.0.1:27011 -uspringboot -p123456 springboot
```