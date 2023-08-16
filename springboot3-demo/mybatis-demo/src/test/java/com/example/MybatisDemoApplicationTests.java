package com.example;

import com.example.dao.*;
import com.example.model.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class MybatisDemoApplicationTests {

    @Autowired
    private AnnotationsUserMapper annotationsUserMapper;

    @Autowired
    private AnnotationsAddressMapper annotationsAddressMapper;

    @Autowired
    private AnnotationsRoleMapper annotationsRoleMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Autowired
    private TestOrderMapper testOrderMapper;


    @Test
    void testTestOrder(){
        final TestOrder order = testOrderMapper.selectByPrimaryKey(1);
        System.out.println(order);
    }
    @Test
    void testOrder(){
        final Order order = orderMapper.selectByPrimaryKey(1);
        System.out.println(order);
    }

    @Test
    void getRole(){
        Role role = annotationsRoleMapper.getRoleAllWithUsersByRoleId(1L);
        System.out.println(role);
    }

    @Test
    void getAddressByUserId(){
        Address address = annotationsAddressMapper.getAddressByUserId(65L);
        System.out.println(address);
    }

    @Test
    void getAddressAndUserByUserId(){
        Address address = annotationsAddressMapper.getAddressAndUserByUserId(65L);
        System.out.println(address);
    }

    @Test
    void getUserRoles(){
        User user = annotationsUserMapper.getUserRolesById(65L);
        System.out.println(user);
    }

    @Test
    void getUserAndBooksById(){
        User user = annotationsUserMapper.getUserAndBooksById(65L);
        System.out.println(user);
    }

    @Test
    void getUserById(){
        User user = annotationsUserMapper.getUserById(65L);
        System.out.println(user);
    }

    @Test
    void getUserAddressAndBooksById(){
        User user = annotationsUserMapper.getUserAddressAndBooksById(65L);
        System.out.println(user);
    }

    @Test
    void selectAll(){
        List<User> users = annotationsUserMapper.selectUsers();
        users.stream().forEach(System.out::println);
    }

    @Test
    void insertSelective(){
        User user = new User();
        user.setName("mybatis01");
        user.setAge(1);
        annotationsUserMapper.insertSelective(user);
    }

    @Test
    void selectUsersByPage(){
        // 分页从1开始计算，这里0和1是一回事
        int pageNum = 2;
        int pageSize = 10;

        //结果分页,PageHelper是分页的关键
        PageHelper.startPage(pageNum, pageSize);
        //此时查询的结果已经是按分页的了
        List<User> userDomains = annotationsUserMapper.selectUsers();
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
