package com.example;

import com.example.dao.*;
import com.example.model.Address;
import com.example.model.Book;
import com.example.model.Role;
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
    private XmlBookMapper xmlBookMapper;

    @Autowired
    private XmlAddressMapper xmlAddressMapper;

    @Autowired
    private AnnotationsUserDao annotationsUserDao;

    @Autowired
    private AnnotationsAddressMapper annotationsAddressMapper;

    @Autowired
    private AnnotationsRoleMapper annotationsRoleMapper;

    @Test
    void getRole(){
        Role role = annotationsRoleMapper.getRoleAllWithUsersByRoleId(1L);
        System.out.println(role);
    }

    @Test
    void getUserAddressAndBooksByIdxml(){
        User user = xmlUserDao.getUserAddressAndBooksById(65L);
        System.out.println("======");
        //预先抓取
        System.out.println(user.getUserAddress());
        //延迟加载books，只有用到时才从数据库中获取
        System.out.println(user.getBooks());
    }
    @Test
    void getUserAndBooksByIdxml(){
        User user = xmlUserDao.getUserAndBooksById(65L);
        System.out.println(user);
    }
    @Test
    void getUserByIdxml(){
        User user = xmlUserDao.getUserById(65L);
        System.out.println(user);
    }


    @Test
    void getAddressByUserId(){
        Address address = annotationsAddressMapper.getAddressByUserId(65L);
        System.out.println(address);
    }

    @Test
    void getAddressAndUserByUserId(){
        Address address = xmlAddressMapper.getAddressAndUserByUserId(65L);
        System.out.println(address);
    }

    @Test
    void getBooksByUserId(){
        List<Book> books = xmlBookMapper.getBooksByUserId(65L);
        books.stream().forEach(System.out::println);
    }

    @Test
    void getBooksAndUserByUserId(){
        List<Book> books = xmlBookMapper.getBooksAndUserByUserId(65L);
        books.stream().forEach(System.out::println);
    }


    @Test
    void getUserRoles(){
        User user = annotationsUserDao.getUserRolesById(65L);
        System.out.println(user);
    }

    @Test
    void getUserAndBooksById(){
        User user = annotationsUserDao.getUserAndBooksById(65L);
        System.out.println(user);
    }

    @Test
    void getUserById(){
        User user = annotationsUserDao.getUserById(65L);
        System.out.println(user);
    }

    @Test
    void getUserAddressAndBooksById(){
        User user = annotationsUserDao.getUserAddressAndBooksById(65L);
        System.out.println(user);
    }

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
