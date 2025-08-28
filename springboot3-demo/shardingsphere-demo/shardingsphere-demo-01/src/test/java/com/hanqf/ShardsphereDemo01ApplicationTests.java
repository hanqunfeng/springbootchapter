package com.hanqf;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.hanqf.demo.entity.Course;
import com.hanqf.demo.mapper.CourseMapper;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class ShardsphereDemo01ApplicationTests {

    @Test
    void contextLoads() {
    }

    @Resource
    private CourseMapper courseMapper;

    @Test
    public void addcourse() {
        for (int i = 0; i < 10; i++) {
            Course c = new Course();
            c.setCname("java");
            c.setUserId(1001L);
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

}
