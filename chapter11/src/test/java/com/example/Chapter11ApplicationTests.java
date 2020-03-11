package com.example;

import com.example.dao.AnnotationsUserDao;
import com.example.dao.XmlUserDao;
import com.example.model.User;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class Chapter11ApplicationTests {

    @Autowired
    private XmlUserDao xmlUserDao;

    @Autowired
    private AnnotationsUserDao annotationsUserDao;

    @Test
    void selectAll(){
        List<User> users = annotationsUserDao.selectUsers();
        users.stream().forEach(System.out::println);
    }

    @Test
    void insertSelective(){
        User user = new User();
        user.setName("mybatis01");
        user.setAge(1);
        annotationsUserDao.insertSelective(user);
    }

    @Test
    void selectByPrimaryKey() {
        User user = xmlUserDao.selectByPrimaryKey(65L);
        System.out.println(user);
    }

    @Test
    void selectUsers(){
        List<User> users = xmlUserDao.selectUsers();
        users.stream().forEach(System.out::println);

    }

    @Test
    void selectUsersByPage(){
        int pageNum = 2;
        int pageSize = 10;

        //结果分页,PageHelper是分页的关键
        PageHelper.startPage(pageNum, pageSize);
        //此时查询的结果已经是按分页的了
        List<User> userDomains = xmlUserDao.selectUsers();
        userDomains.stream().forEach(System.out::println);

        //以下是为了获得分页信息
        System.out.println("PageInfo============");
        PageInfo result = new PageInfo(userDomains);
        System.out.println(result);
        System.out.println("getTotal = " + result.getTotal());
        System.out.println("getPages = " + result.getPages());
        List<User> users = result.getList();
        users.stream().forEach(System.out::println);

    }



}
