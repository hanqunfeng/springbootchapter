package com.hanqf;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanqf.demo.entity.Course;
import com.hanqf.demo.entity.Order;
import com.hanqf.demo.entity.OrderItem;
import com.hanqf.demo.mapper.CourseMapper;
import com.hanqf.demo.service.OrderItemService;
import com.hanqf.demo.service.OrderService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ShardsphereDemo01ApplicationTests {

    @Test
    void contextLoads() {
    }

    @Resource
    private CourseMapper courseMapper;

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderItemService orderItemService;

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
        // wrapper.eq("cid", 1L);
        List<Course> courses = courseMapper.selectList(wrapper);
        courses.forEach(course -> System.out.println(course));
    }

    @Test
    public void addOrder() {
        for (int i = 0; i < 10; i++) {
            Order order = new Order();
            order.setUserId(1001L+ i);
            order.setStatus("1");

            orderService.save( order);

        }
    }

    @Test
    public void addOrderItem() {
        for (int i = 0; i < 10; i++) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(2001L+ i);
            orderItem.setUserId(1001L+ i);
            orderItem.setStatus("1");

            orderItemService.save(orderItem);

        }
    }

}
