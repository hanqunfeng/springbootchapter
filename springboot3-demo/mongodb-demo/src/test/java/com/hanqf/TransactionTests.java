package com.hanqf;

import com.hanqf.mongo.servcie.EmployeeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * <h1></h1>
 * Created by hanqf on 2024/4/7 16:35.
 */

@SpringBootTest
public class TransactionTests {
    @Autowired
    EmployeeService employeeService;


    @Test
    public void test() {
        employeeService.addEmployee();
    }
}

