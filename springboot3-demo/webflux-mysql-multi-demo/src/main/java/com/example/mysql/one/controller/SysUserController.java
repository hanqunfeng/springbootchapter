package com.example.mysql.one.controller;

import com.example.mysql.one.model.SysUser;
import com.example.mysql.one.repository.SysUserRepository;
import com.example.r2dbc.DefaultR2dbcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

/**
 * <h1></h1>
 * Created by hanqf on 2023/8/31 11:40.
 */

@RestController
@RequestMapping("/user")
public class SysUserController {

    @Autowired
    private SysUserRepository sysUserRepository;

    @Autowired
    @Qualifier("oneR2dbcEntityTemplate")
    private R2dbcEntityTemplate oneR2dbcEntityTemplate;

    @RequestMapping("/all")
    public Flux<SysUser> findAll() {
        return sysUserRepository.findAll();
    }

    @RequestMapping("/save")
    public Mono<SysUser> save(@RequestBody SysUser sysUser) {
        return sysUserRepository.addSysUser(sysUser)
                .flatMap(e -> sysUserRepository.findById(sysUser.getId()));
    }


    @RequestMapping("/update")
    public Mono<SysUser> update(@RequestBody SysUser sysUser) {
        return sysUserRepository.save(sysUser);
    }

    @RequestMapping("/page")
    public Flux<SysUser> page() {
        Pageable page = PageRequest.of(1, 2, Sort.by(Sort.Order.desc("id")));
        return sysUserRepository.findAllBy(page);
    }

    @RequestMapping("/page2")
    public Mono<Page<SysUser>> page2() {
        Pageable page = PageRequest.of(1, 2, Sort.by(Sort.Order.desc("id")));
        return sysUserRepository.count().flatMap(total -> sysUserRepository.findAllBy(page).collectList().map(list -> new PageImpl<>(list, page, total)));
    }

    @RequestMapping("/page3")
    public Mono<Page<SysUser>> page3() {
        Pageable page = PageRequest.of(1, 2, Sort.by(Sort.Order.desc("id")));
        return sysUserRepository.pageByQuery(Criteria.empty(), page);
    }

    @RequestMapping("/page4")
    public Mono<Page<SysUser>> page4() {
        Pageable page = PageRequest.of(0, 2, Sort.by(Sort.Order.desc("id")));

        Criteria criteria = Criteria.empty();
        criteria = criteria.and("username").like("%admin%");

        return sysUserRepository.pageByQuery(criteria, page);
    }

    @RequestMapping("/count")
    public Mono<Long> count() {
        return sysUserRepository.countByQuery(Criteria.empty());
    }

    @RequestMapping("/list")
    public Flux<SysUser> findList() {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and("username").like("%admin%");
        return sysUserRepository.findByQuery(criteria);
    }

    @RequestMapping("/many")
    public Flux<SysUser> getMany(String username, Integer enable) {
        String sql = "select id, username from sys_user where username like CONCAT('%',:username,'%') and enable = :enable";

        return DefaultR2dbcService.builder()
                .r2dbcEntityTemplate(oneR2dbcEntityTemplate).build()
                .execSqlToFlux(sql, Map.of("username", username, "enable", enable), (row, rowMetadata) -> {
                    final SysUser sysUser = new SysUser();
                    sysUser.setId(row.get("id", String.class));
                    sysUser.setUsername(row.get("username", String.class));
                    return sysUser;
                });
    }
}

