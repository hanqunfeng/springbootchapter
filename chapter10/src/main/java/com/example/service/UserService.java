package com.example.service;

import com.example.model.one.OneUser;
import com.example.model.two.TwoUser;

/**
 * Created by hanqf on 2020/3/5 16:47.
 */


public interface UserService {

    public void save1And2(OneUser oneUser,TwoUser twoUser);
    public void saveOne(OneUser oneUser);
    public void saveTwo(TwoUser twoUser);
}
