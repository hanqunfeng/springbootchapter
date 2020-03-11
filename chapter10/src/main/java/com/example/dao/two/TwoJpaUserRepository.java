package com.example.dao.two;


import com.example.model.two.TwoUser;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by hanqf on 2020/3/8 16:04.
 */


public interface TwoJpaUserRepository extends JpaRepository<TwoUser, Long> {
}
