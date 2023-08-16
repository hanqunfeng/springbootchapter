package com.example.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.plugins.pagination.PageDTO;
import com.example.mapper.UserPlusMapper;
import com.example.model.UserPlus;
import com.example.service.UserPlusService;
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
    private UserPlusMapper userMapper;

    @Autowired
    private UserPlusService userPlusService;

    @GetMapping("/")
    public List<UserPlus> index() {
        IPage<UserPlus> userPage = new Page<>(2, 2);//参数一是当前页，参数二是每页个数
        userPage = userMapper.selectPage(userPage, null);
        return userPage.getRecords();
    }

    @GetMapping("/service")
    public List<UserPlus> service() {
        Page<UserPlus> userPage = new Page<>(1, 2);//参数一是当前页，参数二是每页个数
        userPage = userPlusService.page(userPage);

        return userPage.getRecords();
    }

    @GetMapping("/servicePage")
    public Page<UserPlus> servicePage() {
        Page<UserPlus> userPage = new Page<>(2, 2);//参数一是当前页，参数二是每页个数
        userPage = userPlusService.page(userPage,new LambdaQueryWrapper<>(UserPlus.class).orderByDesc(UserPlus::getId));
        return userPage;
    }

    @GetMapping("/serviceDto")
    public PageDTO<UserPlus> serviceDto() {
        PageDTO<UserPlus> userPage = new PageDTO<>(2, 2);//参数一是当前页，参数二是每页个数
        userPage = userPlusService.page(userPage,new LambdaQueryWrapper<>(UserPlus.class).orderByDesc(UserPlus::getId));
        return userPage;
    }
}
