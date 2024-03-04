package com.hanqf.mongo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <h1></h1>
 * Created by hanqf on 2024/3/4 17:25.
 */

@RestController
public class MongoDemoController {

    @Autowired
    MongoTemplate mongoTemplate;

    @GetMapping("/demo")
    public String demo() {
        boolean exists = mongoTemplate.collectionExists("employee");
        if (exists) {
            //删除集合
            mongoTemplate.dropCollection("employee");
        }
        //创建集合
        mongoTemplate.createCollection("employee");
        return "hello mongo";
    }
}
