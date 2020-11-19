package com.example.mysql.dao;

import com.example.mysql.model.SysUser;
import org.springframework.data.repository.reactive.ReactiveSortingRepository;
import reactor.core.publisher.Mono;

/**
 * <h1>系统登录用户Repository</h1>
 * Created by hanqf on 2020/11/19 10:04.
 */

/**
 * 当我们的操作接口继承的是ReactiveCrudRepository<T, ID> 或者ReactiveSortingRepository<T, ID>时，需要在实体类上使用@Table注解，这也是推荐的用法
 * 当然实体类不使用@Table注解标记时，我们还可以继承R2dbcRepository<T, ID>接口
 *
 * 注意，截至springboot2.4.0版本，分页功能不被支持
*/
public interface SysUserRepository extends ReactiveSortingRepository<SysUser, String> {
    Mono<SysUser> findByUsername(String username);
}


