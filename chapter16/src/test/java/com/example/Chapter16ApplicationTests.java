package com.example;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.one.OneUserDao;
import com.example.mapper.two.TwoUserDao;
import com.example.model.one.OneUser;
import com.example.model.two.TwoUser;
import com.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class Chapter16ApplicationTests {

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
        oneUser.setName("one11111");
        TwoUser twoUser = new TwoUser();
        twoUser.setAge(400);
        twoUser.setName("two22222");

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
        OneUser oneUser = oneUserDao.selectById(65L);
        System.out.println(oneUser);
        List<TwoUser> twoUsers = twoUserDao.selectList(null);
        twoUsers.stream().forEach(System.out::println);
    }

    @Test
    void selectUsersByPage(){
        int pageNum = 2;
        int pageSize = 10;

        //结果分页
        IPage<OneUser> userPage = new Page<>(pageNum, pageSize);//参数一是当前页，参数二是每页个数
        //此时查询的结果已经是按分页的了
        userPage=oneUserDao.selectPage(userPage,null);
        List<OneUser> userDomains = userPage.getRecords();
        System.out.println("PageInfo============");
        userDomains.stream().forEach(System.out::println);


        System.out.println("PageInfo2============");

        IPage<TwoUser> userPage2 = new Page<>(1, 5);//参数一是当前页，参数二是每页个数
        //此时查询的结果已经是按分页的了
        userPage2=twoUserDao.selectPage(userPage2,null);
        List<TwoUser> userDomains2 = userPage2.getRecords();
        System.out.println("PageInfo============");
        userDomains2.stream().forEach(System.out::println);
    }

}
