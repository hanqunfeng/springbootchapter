package com.example;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.mapper.UserPlusMapper;
import com.example.model.UserPlus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class Chapter15ApplicationTests {

    @Autowired
    private UserPlusMapper userPlusMapper;


    @Test
    void contextLoads() {
        System.out.println("==============mybatis-plus=====================");
        UserPlus userPlus = new UserPlus();
        userPlus.setAge(100);
        userPlus.setEmail("hanqf@163.com");
        userPlus.setName("hanqf");
        userPlusMapper.insert(userPlus);

        List<UserPlus> userList = userPlusMapper.selectList(null);
        //Assert.assertEquals(5, userList.size());
        userList.stream().forEach(System.out::println);
        System.out.println("==============mybatis-plus====myself====name=============");

        userPlusMapper.deleteById(19);

        List<UserPlus> userListByName = userPlusMapper.getUserPlusByName("hanqf");
        //Assert.assertEquals(5, userList.size());
        userListByName.stream().forEach(System.out::println);


        System.out.println("==============mybatis-plus====myself======age1===========");

        List<UserPlus> userListByAge = userPlusMapper.getUserPlusByAge(null);
        //Assert.assertEquals(5, userList.size());
        userListByAge.stream().forEach(System.out::println);

        System.out.println("==============mybatis-plus====myself======age2===========");

        List<UserPlus> userListByAge2 = userPlusMapper.getUserPlusByAge(10);
        //Assert.assertEquals(5, userList.size());
        userListByAge2.stream().forEach(System.out::println);

        System.out.println("==============mybatis-plus====myself======page===========");
        IPage<UserPlus> userPage = new Page<>(2, 2);//参数一是当前页，参数二是每页个数
        userPage = userPlusMapper.selectPage(userPage, null);
        List<UserPlus> listpage = userPage.getRecords();
        listpage.stream().forEach(System.out::println);



        System.out.println("==============mybatis-plus====myself======page2===========");
        QueryWrapper<UserPlus> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("age",9).eq("name","aaa");
        userPage = userPlusMapper.selectPage(userPage, queryWrapper);
        List<UserPlus> listpage2 = userPage.getRecords();
        listpage2.stream().forEach(System.out::println);

    }

}
