package com.example.jwtresourcewebfluxdemo.controller;


import com.example.jwtresourcewebfluxdemo.exception.AjaxResponse;
import com.example.jwtresourcewebfluxdemo.exception.CustomException;
import com.example.jwtresourcewebfluxdemo.exception.CustomExceptionType;
import com.example.jwtresourcewebfluxdemo.model.SysUser;
import com.example.jwtresourcewebfluxdemo.service.SysUserServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebSession;
import reactor.core.publisher.Mono;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * <h1>demo</h1>
 * Created by hanqf on 2020/11/3 16:42.
 */

@RestController
@RequestMapping("/sysuser")
public class SysUserController {

    @Autowired
    private SysUserServcie sysUserServcie;

    @Autowired
    private PasswordEncoder passwordEncoder;

    ///request?a=1&c=1&c=2
    //    "a": [
    //        "1"
    //        ],
    //    "c": [
    //        "1",
    //        "2"
    //        ]
    @GetMapping("/request")
    public Mono<AjaxResponse> request(ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        return Mono.just(AjaxResponse.success(request.getQueryParams()));
    }

    @GetMapping("/session")
    public Mono<AjaxResponse> session(ServerWebExchange exchange) {
        Mono<WebSession> session = exchange.getSession();
        Mono<Map<String, Object>> map = session.map(ses -> ses.getAttributes());
        return Mono.just(AjaxResponse.success(map));
    }

    @GetMapping("/me")
    public Mono<AjaxResponse> user(Principal principal) {
        return Mono.just(AjaxResponse.success(principal));
    }


    @GetMapping("/{userName}")
    public Mono<AjaxResponse> getSysUser(@PathVariable String userName) {
        Mono<SysUser> sysUserMono = sysUserServcie.findUserByUsername(userName);
        return sysUserMono
                .map(AjaxResponse::success);
                //.switchIfEmpty(Mono.just(AjaxResponse.success(null)));
    }

    /**
     * {
     * "username":"001",
     * "password":"123456"
     * }
     */
    @PostMapping
    public Mono<AjaxResponse> addSysUser(@RequestBody SysUser sysUser) {
        sysUser.setId(UUID.randomUUID().toString());
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        sysUser.setEnable(true);
        Mono<SysUser> sysUserMono = sysUserServcie.add(sysUser);
        return sysUserMono.map(AjaxResponse::success);
    }

    /**
     * {
     * "id": "d9ed56a6-b36b-40ee-842e-019e3113e47e",
     * "username":"001",
     * "password":"123456"
     * }
     */
    @PutMapping
    public Mono<AjaxResponse> updateSysUser(@RequestBody SysUser sysUser) {
        if (StringUtils.isEmpty(sysUser.getId())) {
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR, "更新操作主键不能为空");
        }
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        Mono<SysUser> sysUserMono = sysUserServcie.update(sysUser);
        return sysUserMono.map(AjaxResponse::success);
                //统一使用异常拦截器WebExceptionHandler处理
                //.doOnError(e -> e.printStackTrace()) //打印异常信息
                ////异常时将异常信息封装到返回值中
                //.onErrorResume(throwable -> Mono.just(AjaxResponse.error(new CustomException(CustomExceptionType.USER_INPUT_ERROR,throwable.getMessage(),throwable.getClass().getName()))));
    }


    @DeleteMapping("/{userName}")
    public Mono<AjaxResponse> deleteSysUserByUserName(@PathVariable String userName) {
        Mono<Boolean> sysUserMono = sysUserServcie.deleteByUserName(userName);
        return sysUserMono.map(AjaxResponse::success);
    }

    @GetMapping
    public Mono<AjaxResponse> findAll() {
        Mono<List<SysUser>> listMono = sysUserServcie.findAll().collectList();
        return listMono.map(AjaxResponse::success);
                //.switchIfEmpty(Mono.just(AjaxResponse.success(null)));
    }

    @GetMapping("/sort")
    public Mono<AjaxResponse> findAllBySort() {
        Mono<List<SysUser>> listMono = sysUserServcie.findAllBySort().collectList();
        return listMono.map(AjaxResponse::success);
                //.switchIfEmpty(Mono.just(AjaxResponse.success(null)));
    }

    @GetMapping("/page/{page}/{size}")
    public Mono<AjaxResponse> findAllByPage(@PathVariable Integer page,@PathVariable Integer size) {
        Mono<List<SysUser>> listMono = sysUserServcie.findAllByPage(page,size).collectList();
        return listMono.map(AjaxResponse::success);
                //.switchIfEmpty(Mono.just(AjaxResponse.success(null)));
    }
}
