package com.example.demo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * <h1></h1>
 * Created by hanqf on 2024/9/23 14:34.
 */


public interface UserRepository extends JpaRepository<User, Long> {
    @Query(value = "alter table user_info delete where id=:id", nativeQuery = true)
    void delUser(@Param("id") Long id);

//    @Modifying //这里不能使用该注解，因为其要求必须在事务中运行
    @Query(value = "alter table user_info update username=:#{#user.username},addr=:#{#user.addr},create_time=:#{#user.createTime} where id=:#{#user.id}", nativeQuery = true)
    void updateUser(@Param("user") User user);
}
