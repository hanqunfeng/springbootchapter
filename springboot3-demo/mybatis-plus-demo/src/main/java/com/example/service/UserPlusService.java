package com.example.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.mapper.UserPlusMapper;
import com.example.model.UserPlus;
import org.springframework.stereotype.Service;

/**
 * <h1>继承mybatisplus的service类，已经封装好了很多方法</h1>
 * Created by hanqf on 2023/8/15 15:44.
 */

@Service
public class UserPlusService extends ServiceImpl<UserPlusMapper, UserPlus> {
}
