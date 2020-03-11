package com.example;

import com.example.dao.JpaUserRepository;
import com.example.model.DelEnum;
import com.example.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class Chapter09ApplicationTests {

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Test
    void testFindAll() {
        List<User> list = jpaUserRepository.findAll();
        list.stream().forEach(System.out::println);
    }

    @Test
    void testSave(){
        User user = new User();
        user.setName("jpa");
        user.setAge(10);
        user.setEmail("jpa@email.com");
        user.setDel(DelEnum.one);
        jpaUserRepository.save(user);
    }

}
