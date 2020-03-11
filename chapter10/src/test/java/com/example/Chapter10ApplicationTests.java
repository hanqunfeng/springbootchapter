package com.example;

import com.example.dao.one.OneJpaUserRepository;
import com.example.dao.two.TwoJpaUserRepository;
import com.example.model.one.OneUser;
import com.example.model.two.TwoUser;
import com.example.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@SpringBootTest
class Chapter10ApplicationTests {

    @Autowired
    private OneJpaUserRepository oneJpaUserRepository;
    @Autowired
    private TwoJpaUserRepository twoJpaUserRepository;

    @Autowired
    private UserService userService;

    @Test
    void testFindAll() {
        List<OneUser> oneUsers = oneJpaUserRepository.findAll();
        oneUsers.stream().forEach(System.out::println);

        List<TwoUser> twoUsers = twoJpaUserRepository.findAll();
        twoUsers.stream().forEach(System.out::println);
    }

    @Test
    void testSave1and2(){
        OneUser oneUser = new OneUser();
        oneUser.setAge(300);
        oneUser.setName("one");
        TwoUser twoUser = new TwoUser();
        twoUser.setAge(400);
        twoUser.setName("two");

        userService.save1And2(oneUser,twoUser);
    }

    @Test
    void testSave1(){
        OneUser oneUser = new OneUser();
        oneUser.setAge(200);
        oneUser.setName("one");


        userService.saveOne(oneUser);
    }


    @Test
    void testPage(){
        //排序
        //查询第一页，按一页三行分页
        Pageable pageable = PageRequest.of(2,10, Sort.by(Sort.Direction.DESC, "id"));
        Page<OneUser> allByPage = oneJpaUserRepository.findAllByPage(pageable);
        System.out.println(allByPage);
        allByPage.get().forEach(System.out::println);


        //内置的查询方法也支持分页的
        Page<OneUser> oneUsers = oneJpaUserRepository.findAll(pageable);

    }


}
