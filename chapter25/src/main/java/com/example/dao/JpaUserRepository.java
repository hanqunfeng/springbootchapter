package com.example.dao;


import com.example.dao.base.BaseJpaRepository;
import com.example.model.User;

/**
 * Created by hanqf on 2020/3/8 16:04.
 */


public interface JpaUserRepository extends BaseJpaRepository<User, Long> {
}
