package com.example;

import com.example.mapper.one.OneUserDao;
import com.example.mapper.two.TwoUserDao;
import com.example.model.one.OneUser;
import com.example.model.two.TwoUser;
import com.example.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class Chapter12ApplicationTests {

    @Autowired
    private OneUserDao oneUserDao;

    @Autowired
    private TwoUserDao twoUserDao;

    @Autowired
    private UserService userService;

    @Test
    void testSave1and2(){
        OneUser oneUser = new OneUser();
        oneUser.setAge(300);
        oneUser.setName("one");
        TwoUser twoUser = new TwoUser();
        twoUser.setAge(400);
        twoUser.setName("two");

        userService.save1And2(oneUser,twoUser);
    }

    @Test
    void testSave1(){
        OneUser oneUser = new OneUser();
        oneUser.setAge(200);
        oneUser.setName("one");


        userService.saveOne(oneUser);
    }


    @Test
    void contextLoads() {
        OneUser oneUser = oneUserDao.selectByPrimaryKey(65L);
        System.out.println(oneUser);
        List<TwoUser> twoUsers = twoUserDao.selectUsers();
        twoUsers.stream().forEach(System.out::println);
    }

    @Test
    void selectUsersByPage(){
        int pageNum = 2;
        int pageSize = 10;

        //结果分页,PageHelper是分页的关键
        PageHelper.startPage(pageNum, pageSize);
        //此时查询的结果已经是按分页的了
        List<OneUser> userDomains = oneUserDao.selectUsers();
        userDomains.stream().forEach(System.out::println);

        //以下是为了获得分页信息
        System.out.println("PageInfo============");
        PageInfo result = new PageInfo(userDomains);
        System.out.println(result);


        System.out.println("PageInfo2============");
        PageHelper.startPage(1, 5);
        List<TwoUser> twoUsers = twoUserDao.selectUsers();
        twoUsers.stream().forEach(System.out::println);
        PageInfo result2 = new PageInfo(twoUsers);
        System.out.println(result2);

    }

}
