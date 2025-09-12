package com.hanqf;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanqf.demo.entity.*;
import com.hanqf.demo.mapper.CourseMapper;
import com.hanqf.demo.service.*;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ShardingsphereDemo03ApplicationTests {

    @Test
    void contextLoads() {
    }

    @Resource
    private CourseMapper courseMapper;

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private OrderComplexService orderComplexService;
    @Autowired
    private OrderItemComplexService orderItemComplexService;

    @Autowired
    private UserService userService;
    @Autowired
    private AddressService addressService;
    @Autowired
    private DictService dictService;

    @Test
    public void addcourse() {
        for (int i = 0; i < 10; i++) {
            Course c = new Course();
            c.setCname("java");
            c.setUserId(1001L + i % 2);
            c.setCstatus("1");
            courseMapper.insert(c);
            // insert into course values ....
            System.out.println(c);
        }
    }

    @Test
    public void queryCourse() {
        QueryWrapper<Course> wrapper = new QueryWrapper<Course>();
        // // 分表键 cid，会查询所有库的指定表，会根据分片键的算法进行匹配
        // wrapper.eq("cid", 1L);
        // // 分库键 user_id,分表键 cid，同时匹配就会走同路由，只查询指定库的指定表，会根据分片键的算法进行匹配
        // wrapper.eq("cid", 1L).eq("user_id", 1002L);

        // 因为查询的分表键值都是奇数，符合分表键的规则，会走同路由，查询所有库的指定表，会根据分片键的算法进行匹配
        // wrapper.in("cid", 1L,3L,5L);

        // 不符合规则，多表扫描
        // wrapper.in("cid", 1L,3L,6L);

        // 默认情况下不支持 between 这种范围查询，需要开启 allow-range-query-with-inline-sharding
        // 范围查询就是多表扫描
        wrapper.between("cid", 1L, 3L);
        List<Course> courses = courseMapper.selectList(wrapper);
        courses.forEach(course -> System.out.println(course));
    }

    @Test
    public void addOrder() {
        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            order.setUserId(1001L + i);
            order.setStatus("1");

            orderService.save(order);

        }
    }

    @Test
    public void addOrderItem() {
        for (int i = 0; i < 10; i++) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(2001L + i);
            orderItem.setUserId(1001L + i);
            orderItem.setStatus("1");

            orderItemService.save(orderItem);

        }
    }

    @Test
    public void addOrderComplex() {
        for (int i = 0; i < 10; i++) {
            OrderComplex order = new OrderComplex();
            order.setUserId(1001L + i);
            order.setStatus("1");

            orderComplexService.save(order);

        }
    }

    @Test
    public void addOrderItemComplex() {
        for (int i = 0; i < 10; i++) {
            OrderItemComplex orderItem = new OrderItemComplex();
            orderItem.setOrderId(2001L + i);
            orderItem.setUserId(1001L + i % 3);
            orderItem.setStatus("1");

            orderItemComplexService.save(orderItem);

        }
    }

    @Test
    public void addUser() {
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setName("user" + i);
            user.setPassword("password" + i);
            user.setEmail("email" + i + "@163.com");
            user.setTelephone("180701" + i + "8080");

            userService.save(user);

        }
    }

    @Test
    public void queryUser() {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        // 不分库分表的数据表支持  between
        wrapper.eq("password", "password" + 1);
        List<User> users = userService.list(wrapper);
        users.forEach(System.out::println);
    }

    @Test
    public void addAddress() {
        for (int i = 0; i < 10; i++) {
            Address address = new Address();
            address.setAddress("address" + i);
            address.setUserId(1001L + i);

            addressService.save(address);

        }
    }

    @Test
    public void queryAddress() {
        QueryWrapper<Address> wrapper = new QueryWrapper<>();
        // 不分库分表的数据表支持  between
        wrapper.between("id", 1L, 3L);
        List<Address> courses = addressService.list(wrapper);
        courses.forEach(System.out::println);
    }


    @Test
    public void addDict() {
        for (int i = 0; i < 10; i++) {
            Dict dict = new Dict();
            dict.setDictval("value_" + i);
            dict.setDictkey("key_" + i);

            dictService.save(dict);

        }
    }
}
