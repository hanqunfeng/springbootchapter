package org.example.aspect.enhance;/**
 * Created by hanqf on 2020/3/5 11:42.
 */


import org.example.model.User;

/**
 * @author hanqf
 * @date 2020/3/5 11:42
 */
public class UserValidatorImpl implements UserValidator {
    @Override
    public boolean validate(User user) {
        System.out.println("引入增强接口:" + UserValidator.class.getSimpleName());
        return user != null;
    }
}
