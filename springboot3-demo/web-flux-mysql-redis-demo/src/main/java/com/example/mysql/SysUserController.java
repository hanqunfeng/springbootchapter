package com.example.mysql;

import com.example.r2dbc.CustomCriteria;
import com.example.r2dbc.DefaultR2dbcService;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheEvict;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCachePut;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCacheable;
import com.hanqunfeng.reactive.redis.cache.aop.ReactiveRedisCaching;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

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
    private TransactionalOperator transactionalOperator;

    @Autowired
    private R2dbcEntityTemplate r2dbcEntityTemplate;


    @ReactiveRedisCacheable(cacheName = "sys-user", key = "all")
    @RequestMapping("/all")
    public Flux<SysUser> findAll() {
        return sysUserRepository.findAll();
    }

    @ReactiveRedisCacheable(cacheName = "sys-user", key = "'find_' + #username")
    @RequestMapping("/find/{username}")
    public Mono<SysUser> findUserByUsername(@PathVariable String username) {
        return sysUserRepository.findByUsername(username);
    }

    /**
     * 删除指定的"cacheName_key"
     */
    @ReactiveRedisCacheEvict(cacheName = "sys-user", key = "'find_' + #username")
    @RequestMapping("/delete/{username}")
    public Mono<Boolean> deleteByUserName(@PathVariable String username) {
        return sysUserRepository.deleteByUsername(username);
    }


    @ReactiveRedisCacheEvict(cacheName = "sys-user", allEntries = true)
    @RequestMapping("/save")
    public Mono<SysUser> save(@RequestBody SysUser sysUser) {
        return sysUserRepository.addSysUser(sysUser)
                .flatMap(e -> sysUserRepository.findById(sysUser.getId()));
    }


    @ReactiveRedisCaching(
            evict = {@ReactiveRedisCacheEvict(cacheName = "sys-user", key = "all")},
            put = {@ReactiveRedisCachePut(cacheName = "sys-user", key = "'find_' + #sysUser.username")}
    )
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

    @RequestMapping("/list2")
    public Flux<SysUser> findList2() {
        final Criteria criteria = CustomCriteria.and()
                .like(true, "username", "%admin%", "lisi%")
                .eq(true, "enable", 1)
                .build();

        return sysUserRepository.findByQuery(criteria);
    }

    /**
     * 注解式事务
     */
    @Transactional
    @RequestMapping("/tx")
    public Mono<Integer> tx() {
        final SysUser sysUser = new SysUser();
        sysUser.setId(UUID.randomUUID().toString());
        sysUser.setUsername("test");
        sysUser.setPassword("123456");
        sysUser.setEnable(true);
        // 主键重复
        return sysUserRepository.addSysUser(sysUser).then(sysUserRepository.addSysUser(sysUser));
    }

    /**
     * 编程式事务
     */
    @RequestMapping("/tx2")
    public Mono<Integer> tx2() {
        final SysUser sysUser = new SysUser();
        sysUser.setId(UUID.randomUUID().toString());
        sysUser.setUsername("test");
        sysUser.setPassword("123456");
        sysUser.setEnable(true);
        // 主键重复
        return sysUserRepository.addSysUser(sysUser).then(sysUserRepository.addSysUser(sysUser))
                .as(transactionalOperator::transactional);
    }

    @RequestMapping("/many")
    public Flux<SysUser> getMany(String username) {
        String sql = "select id, username from sys_user where username like CONCAT('%',:username,'%')";

        return DefaultR2dbcService.builder()
                .r2dbcEntityTemplate(r2dbcEntityTemplate).build()
                .execSqlToFlux(sql, Map.of("username", username), (row, rowMetadata) -> {
                    final SysUser sysUser = new SysUser();
                    sysUser.setId(row.get("id", String.class));
                    sysUser.setUsername(row.get("username", String.class));
                    return sysUser;
                });
    }


}

