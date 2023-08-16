package com.example.controller;

import com.example.dao.AnnotationsUserMapper;
import com.example.model.User;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * <h1></h1>
 * Created by hanqf on 2023/8/15 12:20.
 */

@RestController
public class IndexController {

    @Autowired
    private AnnotationsUserMapper userMapper;

    @GetMapping("/")
    public List<User> index(){
        int pageNum = 2;
        int pageSize = 10;

        //结果分页,PageHelper是分页的关键
        PageHelper.startPage(pageNum, pageSize);
        //此时查询的结果已经是按分页的了
        return userMapper.selectUsers();
    }
}
