package com.hanqf;

import com.hanqf.mongo.model.Employee;
import com.mongodb.client.result.UpdateResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest
class EmployeeTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void testCollection() {
        // db.getCollectionNames().includes("employee")
        boolean exists = mongoTemplate.collectionExists("employee");
        if (exists) {
            //删除集合
            // db.employee.drop()
            mongoTemplate.dropCollection("employee");
        }
        //创建集合
        // db.createCollection("employee")
        mongoTemplate.createCollection("employee");
    }

    @Test
    void testInsert() {
        /*
        db.employee.insertOne({
            "_id": 1,
            "username": "小明",
            "age": 30,
            "salary": 10000.00,
            "entryDay": new Date()
        })

         */
        Employee employee = new Employee(1, "小明", 30, 10000.00, new Date());

        //添加文档
        // sava:  _id存在时更新数据
        mongoTemplate.save(employee);
        // insert： _id存在抛出异常   支持批量操作
        mongoTemplate.insert(employee);

        /*
        db.employee.insertMany([
            {
                "_id": 2,
                "username": "张三",
                "age": 21,
                "salary": 5000.00,
                "entryDay": new Date()
            },
            {
                "_id": 3,
                "username": "李四",
                "age": 26,
                "salary": 8000.00,
                "entryDay": new Date()
            },
            {
                "_id": 4,
                "username": "王五",
                "age": 22,
                "salary": 8000.00,
                "entryDay": new Date()
            },
            {
                "_id": 5,
                "username": "张龙",
                "age": 28,
                "salary": 6000.00,
                "entryDay": new Date()
            },
            {
                "_id": 6,
                "username": "赵虎",
                "age": 24,
                "salary": 7000.00,
                "entryDay": new Date()
            },
            {
                "_id": 7,
                "username": "赵六",
                "age": 28,
                "salary": 12000.00,
                "entryDay": new Date()
            }
        ])

         */
        List<Employee> list = Arrays.asList(
                new Employee(2, "张三", 21, 5000.00, new Date()),
                new Employee(3, "李四", 26, 8000.00, new Date()),
                new Employee(4, "王五", 22, 8000.00, new Date()),
                new Employee(5, "张龙", 28, 6000.00, new Date()),
                new Employee(6, "赵虎", 24, 7000.00, new Date()),
                new Employee(7, "赵六", 28, 12000.00, new Date()));
        //插入多条数据
        mongoTemplate.insert(list, Employee.class);
    }

    @Test
    void testFind() {

        System.out.println("==========查询所有文档===========");
        //查询所有文档
        // db.employee.find()
        List<Employee> list = mongoTemplate.findAll(Employee.class);
        list.forEach(System.out::println);

        System.out.println("==========根据_id查询===========");
        //根据_id查询
        // db.employee.findOne({ "_id": 1 })
        Employee e = mongoTemplate.findById(1, Employee.class);
        System.out.println(e);

        System.out.println("==========findOne返回第一个文档===========");
        //如果查询结果是多个，返回其中第一个文档对象
        // db.employee.findOne({})
        Employee one = mongoTemplate.findOne(new Query(), Employee.class);
        System.out.println(one);

        System.out.println("==========条件查询===========");
        //new Query() 表示没有条件

        //查询薪资大于等于8000的员工
        // db.employee.find({ "salary": { "$gte": 8000 } })
        //Query query = new Query(Criteria.where("salary").gte(8000));

        //查询薪资大于4000小于10000的员工
        // db.employee.find({ "salary": { "$gt": 4000, "$lt": 10000 } })
        //Query query = new Query(Criteria.where("salary").gt(4000).lt(10000));

        //正则查询（模糊查询）  java中正则不需要有//
        // db.employee.find({ "username": { "$regex": "张" } })
        //Query query = new Query(Criteria.where("name").regex("张"));


        /*
        db.employee.find({
            "$or": [
                { "username": "张三" },
                { "salary": { "$gt": 5000 } }
            ]
        }).sort({ "salary": -1 }).skip(2).limit(4)
         */
        //and  or  多条件查询
        Criteria criteria = new Criteria();
        //and  查询年龄大于25&薪资大于8000的员工
        //criteria.andOperator(Criteria.where("age").gt(25),Criteria.where("salary").gt(8000));
        //or 查询姓名是张三或者薪资大于8000的员工
        criteria.orOperator(Criteria.where("name").is("张三"), Criteria.where("salary").gt(5000));
        Query query = new Query(criteria);

        //sort排序
        //query.with(Sort.by(Sort.Order.desc("salary")));


        //skip limit 分页  skip用于指定跳过记录数，limit则用于限定返回结果数量。
        query.with(Sort.by(Sort.Order.desc("salary")))
                .skip(2)  //指定跳过记录数
                .limit(4);  //每页显示记录数


        //查询结果
        List<Employee> employees = mongoTemplate.find(
                query, Employee.class);
        employees.forEach(System.out::println);
    }

    @Test
    void testFindByJson() {

        //使用json字符串方式查询
        //等值查询
        //String json = "{name:'张三'}";
        //多条件查询
        /*
        db.employee.find({
            "$or": [
                { "age": { "$gt": 25 } },
                { "salary": { "$gte": 8000 } }
            ]
        })
         */
        String json = "{$or:[{age:{$gt:25}},{salary:{$gte:8000}}]}";
        Query query = new BasicQuery(json);
        query.with(Sort.by(Sort.Order.asc("salary")))
                .skip(2)  //指定跳过记录数
                .limit(4);  //每页显示记录数


        System.out.println(query); // 打印出生成的查询字符串
        //查询结果
        List<Employee> employees = mongoTemplate.find(
                query, Employee.class);
        employees.forEach(System.out::println);
    }

    /**
     * 在Mongodb中无论是使用客户端API还是使用Spring Data，更新返回结果一定是受行数影响。如果更新后的结果和更新前的结果是相同，返回0。
     * - updateFirst() 只更新满足条件的第一条记录
     * - updateMulti() 更新所有满足条件的记录
     * - upsert() 没有符合条件的记录则插入数据，有符合条件的记录则更新第一条记录
     */
    @Test
    void testUpdate() {

        //query设置查询条件
        /*
        db.employee.find({ "salary": { "$gte": 8000 } })
         */
        Query query = new Query(Criteria.where("salary").gte(8000));

        System.out.println("==========更新前===========");
        List<Employee> employees = mongoTemplate.find(query, Employee.class);
        employees.forEach(System.out::println);


        Update update = new Update();
        //设置更新属性
        update.set("salary", 13000);

        //updateFirst() 只更新满足条件的第一条记录
        /*
        db.employee.updateOne(
           { "salary": { "$gte": 8000 } },
           { "$set": { "salary": 13000 } }
        )
         */
        //UpdateResult updateResult = mongoTemplate.updateFirst(query, update, Employee.class);
        //updateMulti() 更新所有满足条件的记录
        /*
        db.employee.updateMany(
           { "salary": { "$gte": 8000 } },
           { "$set": { "salary": 13000 } }
        )
         */
        UpdateResult updateResult = mongoTemplate.updateMulti(query, update, Employee.class);

        /*
        db.employee.updateOne(
           { "salary": { "$gte": 8000 } },
           { "$set": { "salary": 13000 }, "$setOnInsert": { "id": 11 } },
           { "upsert": true }
        )
         */
        //upsert() 没有符合条件的记录则插入数据
        //update.setOnInsert("id",11);  //指定_id，只有没有匹配到时才插入
        UpdateResult updateResult2 = mongoTemplate.upsert(query, update, Employee.class);

        //返回修改的记录数
        System.out.println(updateResult.getModifiedCount());


        System.out.println("==========更新后===========");
        employees = mongoTemplate.find(query, Employee.class);
        employees.forEach(System.out::println);
    }

    @Test
    void testDelete() {

        //删除所有文档
        // db.employee.deleteMany({})
        //mongoTemplate.remove(new Query(),Employee.class);

        //条件删除
        // db.employee.deleteMany({ "salary": { "$gte": 10000 } })
        Query query = new Query(Criteria.where("salary").gte(10000));
        mongoTemplate.remove(query, Employee.class);

//        mongoTemplate.dropCollection(Employee.class);




    }

    @Test
    void testProject() {
        Query query = new Query();
        query.addCriteria(Criteria.where("age").gte(18)); // 筛选条件，年龄大于等于18岁的文档
        query.fields().include("name", "age").exclude("id");// 指定返回的字段，只包含"name"和"age"
        System.out.println(query); // 打印出生成的查询字符串
        List<Employee> results = mongoTemplate.find(query, Employee.class);
        results.forEach(System.out::println);
    }



}
