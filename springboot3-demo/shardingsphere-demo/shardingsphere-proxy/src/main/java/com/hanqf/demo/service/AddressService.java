package com.hanqf.demo.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hanqf.demo.entity.Address;
import com.hanqf.demo.entity.User;
import com.hanqf.demo.mapper.AddressMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * Created by hanqf on 2025/8/27 17:37.
 */

@Service
public class AddressService extends ServiceImpl<AddressMapper, Address> {

    @Autowired
    private  UserService userService;
    @Transactional
    public void testTransaction() {
        int i = 1000;
        Address address = new Address();
        address.setAddress("address" + i);
        address.setUserId(1001L + i);
        this.save(address);

        User user = new User();
        user.setName("user" + i);
        user.setPassword("password" + i);
        user.setEmail("email" + i + "@163.com");
        user.setTelephone("180701" + i + "8082");
        userService.save(user);
        throw new RuntimeException("测试事务");
    }
}
