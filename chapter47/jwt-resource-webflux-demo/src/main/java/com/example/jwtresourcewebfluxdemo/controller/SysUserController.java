package com.example.jwtresourcewebfluxdemo.controller;


import com.example.jwtresourcewebfluxdemo.exception.AjaxResponse;
import com.example.jwtresourcewebfluxdemo.exception.CustomException;
import com.example.jwtresourcewebfluxdemo.exception.CustomExceptionType;
import com.example.jwtresourcewebfluxdemo.model.SysUser;
import com.example.jwtresourcewebfluxdemo.service.CustomReactiveUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;
import java.util.UUID;

/**
 * <h1>demo</h1>
 * Created by hanqf on 2020/11/3 16:42.
 */

@RestController
@RequestMapping("/sysuser")
public class SysUserController {

    @Autowired
    private CustomReactiveUserDetailsService userDetailsService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/hello")
    public Mono<AjaxResponse> hello(){
        return Mono.just(AjaxResponse.success("hello"));
    }

    @GetMapping("/me")
    public Mono<AjaxResponse> user(Principal principal){
        return Mono.just(AjaxResponse.success(principal));
    }


    @GetMapping("/{userName}")
    public Mono<AjaxResponse> getSysUser(@PathVariable String userName){
        Mono<SysUser> sysUserMono = userDetailsService.findUserByUsername(userName);
        return sysUserMono.map(AjaxResponse::success);
    }

    /**
     * {
     *     "username":"001",
     *     "password":"123456"
     * }
    */
    @PostMapping
    public Mono<AjaxResponse> addSysUser(@RequestBody SysUser sysUser){
        sysUser.setId(UUID.randomUUID().toString());
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        sysUser.setEnable(true);
        Mono<SysUser> sysUserMono = userDetailsService.add(sysUser);
        return sysUserMono.map(AjaxResponse::success);
    }

    /**
     * {
     *     "id": "d9ed56a6-b36b-40ee-842e-019e3113e47e",
     *     "username":"001",
     *     "password":"123456"
     * }
     */
    @PutMapping
    public Mono<AjaxResponse> updateSysUser(@RequestBody SysUser sysUser){
        if(StringUtils.isEmpty(sysUser.getId())){
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR,"更新操作主键不能为空");
        }
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        Mono<SysUser> sysUserMono = userDetailsService.update(sysUser);
        return sysUserMono.map(AjaxResponse::success);
    }


    @DeleteMapping("/{userName}")
    public Mono<AjaxResponse> deleteSysUserByUserName(@PathVariable String userName){
        Mono<Boolean> sysUserMono = userDetailsService.deleteByUserName(userName);
        return sysUserMono.map(AjaxResponse::success);
    }

    @GetMapping
    public Mono<AjaxResponse> findAll(){
        Mono<List<SysUser>> listMono = userDetailsService.findAll().collectList();
        return listMono.map(AjaxResponse::success);
    }
}
