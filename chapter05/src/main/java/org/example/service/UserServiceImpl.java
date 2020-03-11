package org.example.service;/**
 * Created by hanqf on 2020/3/5 10:45.
 */


import org.example.model.User;
import org.springframework.stereotype.Service;

/**
 * @author hanqf
 * @date 2020/3/5 10:45
 */
@Service
public class UserServiceImpl implements UserService {
    @Override
    public void printUser(User user) {
        if(user == null){
            throw new RuntimeException("参数不能为空");
        }
        System.out.println(user);
    }
}
