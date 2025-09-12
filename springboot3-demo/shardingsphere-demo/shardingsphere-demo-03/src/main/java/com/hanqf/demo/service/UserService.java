package com.hanqf.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanqf.demo.entity.User;
import com.hanqf.demo.mapper.UserMapper;
import org.springframework.stereotype.Service;

/**
 *
 * Created by hanqf on 2025/8/27 17:37.
 */

@Service
public class UserService extends ServiceImpl<UserMapper, User> {
}
