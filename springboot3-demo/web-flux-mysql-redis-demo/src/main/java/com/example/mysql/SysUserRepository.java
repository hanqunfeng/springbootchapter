package com.example.mysql;

/**
 * <h1>SysUserRepository</h1>
 * Created by hanqf on 2023/8/30 17:15.
 */


import com.example.r2dbc.BaseR2dbcRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface SysUserRepository extends BaseR2dbcRepository<SysUser, String> {
    Mono<SysUser> findByUsername(String username);


    /**
     * 实体类在进行新增时会判断主键是否填充，如果没有填充就认为是新数据，采取真正的新增操作，主键需要数据库来自动填充；
     * 如果主键存在值则认为是旧数据则调用更新操作。
     * 对于自定义主键的情况，可以使用如下方式进行新增对象
     *
     * 对象形式传参：:#{#对象名.字段名}
     * 字段传参：:字段名(@param定义)
    */
    @Modifying
    @Query("insert into sys_user (id,username,password,enable) values (:#{#sysUser.id},:#{#sysUser.username},:#{#sysUser.password},:#{#sysUser.enable})")
    Mono<Integer> addSysUser(SysUser sysUser);

    /**
     * 分页查询全部
    */
    Flux<SysUser> findAllBy(Pageable pageable);
}
