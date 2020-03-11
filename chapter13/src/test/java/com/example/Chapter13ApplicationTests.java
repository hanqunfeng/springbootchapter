package com.example;

import com.example.dao.UserDao;
import com.example.model.User;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class Chapter13ApplicationTests {

    @Autowired
    private UserDao userDao;

    @Test
    void selectAll() {
        List<User> users = userDao.selectAll();
        users.stream().forEach(System.out::println);
    }

    @Test
    void selectUsersByPage(){
        int pageNum = 2;
        int pageSize = 10;

        //结果分页,PageHelper是分页的关键
        PageHelper.startPage(pageNum, pageSize);
        //此时查询的结果已经是按分页的了
        List<User> userList = userDao.selectAll();
        userList.stream().forEach(System.out::println);

        //以下是为了获得分页信息
        System.out.println("PageInfo============");
        PageInfo result = new PageInfo(userList);
        System.out.println(result);

    }

    @Test
    void insert(){
        User user = new User();
        user.setName("mapper01");
        user.setAge(100);
        userDao.insert(user);
    }


}
