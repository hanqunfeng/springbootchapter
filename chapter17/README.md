# springboot mongoDB

## 1.创建新库及初始化数据
```bash
mongo --host 127.0.0.1:27017 -uadminUser -padminPass
# 查询所有的数据库：show dbs 一定是管理员登录才可以查看，刚创建的新库，如果没有创建集合则不会显示
# 这个步骤很重要，首先要切换到对应的数据库，然后再创建用户，否则不能指定数据库登录
> use springboot01

# 在哪个库下创建的用户，就要切换到哪个库下删除用户 use dbname, db.dropUser("username")
# 查看当前库下的用户：show users
> db.createUser(
   {
     user: "springboot01",
     pwd: "123456",
     roles: [ { role: "dbOwner", db: "springboot01" } ]
   }
  );      

> exit

mongo --host 127.0.0.1:27017 -uspringboot01 -p123456 springboot01

> use springboot01

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


> db.user.find()

> db.address.find()

> db.books.find()
```

### 尽量不要使用@DBRef，依赖代码维护集合关联关系
