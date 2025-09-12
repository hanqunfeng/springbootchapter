package com.hanqf.demo.controller;

import com.hanqf.demo.entity.Course;
import com.hanqf.demo.service.CourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 *
 * Created by hanqf on 2025/8/27 17:31.
 */

@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private CourseService courseService;

    @RequestMapping("/list")
    public List<Course> list() {
        return courseService.list();
    }
}
