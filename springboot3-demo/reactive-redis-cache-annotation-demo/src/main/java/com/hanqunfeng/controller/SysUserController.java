package com.hanqunfeng.controller;

import com.hanqunfeng.model.SysUser;
import com.hanqunfeng.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <h1></h1>
 * Created by hanqf on 2024/7/18 10:45.
 */

@RestController
public class SysUserController {
    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/findUserByUsername")
    public Mono<SysUser> findUserByUsername(String username) {
        return sysUserService.findUserByUsername(username);
    }

    @GetMapping("/findAll")
    public Flux<SysUser> findAll() {
        return sysUserService.findAll();
    }


    @PostMapping("/add")
    public Mono<SysUser> add(@RequestBody SysUser sysUser) {
        return sysUserService.add(sysUser);
    }


    @PutMapping("/update")
    public Mono<SysUser> update(@RequestBody SysUser sysUser) {
       return sysUserService.update(sysUser);
    }


    @DeleteMapping("/deleteByUserName")
    public Mono<Boolean> deleteByUserName(String username) {
        return sysUserService.deleteByUserName(username);
    }

    @DeleteMapping("/deleteAll")
    public Mono<Boolean> deleteAll() {
        return sysUserService.deleteAll();
    }

    @DeleteMapping("/deleteAll2")
    public Mono<Boolean> deleteAll2() {
        return sysUserService.deleteAll2();
    }
}
