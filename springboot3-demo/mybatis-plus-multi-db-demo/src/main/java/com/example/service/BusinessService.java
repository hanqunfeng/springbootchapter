package com.example.service;

import com.example.service.one.UserService;
import com.example.service.two.SysUserService;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * <h1>测试seata分布式事务</h1>
 * Created by hanqf on 2023/8/18 15:46.
 */

@Slf4j
@Service
public class BusinessService {
    @Autowired
    private UserService userService;

    @Autowired
    private SysUserService sysUserService;

    @GlobalTransactional(timeoutMills = 30000,name = "business-tx")
    public void business() throws InterruptedException {
        userService.removeById(18);
        sysUserService.removeById("5c74a4e4-c4f2-4570-8735-761d7a570d36");
        TimeUnit.SECONDS.sleep(10);
//        int a = 1/0;
    }

}
