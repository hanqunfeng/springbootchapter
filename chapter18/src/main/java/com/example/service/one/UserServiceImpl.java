package com.example.service.one;/**
 * Created by hanqf on 2020/3/17 16:35.
 */


import com.example.dao.one.UserRepository;
import com.example.model.one.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author hanqf
 * @date 2020/3/17 16:35
 */
@Service

//1：MongoDB的版本必须是4.0
//2.MongoDB事务功能必须是在多副本集的情况下才能使用，否则报错"Sessions are not supported by the MongoDB cluster to which this client is connected"，4.2版本会支持分片事务。
//3.事务控制只能用在已存在的集合中，也就是集合需要手工添加不会由jpa创建会报错"Cannot create namespace glcloud.test_user in multi-document transaction."
//@Transactional(transactionManager = "oneMongoTransactionManager",propagation = Propagation.REQUIRED, readOnly = false, rollbackFor = { Exception.class })
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    //@Transactional(readOnly = true)
    @Override
    public List<User> getAllUser() {
        return userRepository.findAll();
    }

    @Override
    public User Save(User user) {
        return userRepository.save(user);
    }
}
