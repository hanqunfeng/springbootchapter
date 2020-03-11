package com.example.dao;


import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hanqf on 2020/3/8 16:04.
 */


public interface JpaUserRepository extends JpaRepository<User, Long> {
}
