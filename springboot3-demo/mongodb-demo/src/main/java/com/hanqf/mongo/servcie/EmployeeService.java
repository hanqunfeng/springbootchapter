package com.hanqf.mongo.servcie;

import com.hanqf.mongo.model.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * <h1></h1>
 * Created by hanqf on 2024/4/7 16:33.
 */

@Service
public class EmployeeService {
    @Autowired
    MongoTemplate mongoTemplate;

    @Transactional
    public void addEmployee() {
        Employee employee = new Employee(100, "张三", 21,
                10000.00, new Date());
        Employee employee2 = new Employee(101, "赵六", 28,
                10000.00, new Date());

        mongoTemplate.save(employee);
//        int i = 1 / 0;
        mongoTemplate.save(employee2);

    }
}
