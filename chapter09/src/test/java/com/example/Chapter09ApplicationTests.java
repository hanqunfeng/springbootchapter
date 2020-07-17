package com.example;

import com.example.dao.AddressRepository;
import com.example.dao.BookRepository;
import com.example.dao.JpaUserRepository;
import com.example.dao.RoleRepository;
import com.example.model.*;
import com.example.service.LockService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.function.Consumer;

@SpringBootTest
//@Transactional
class Chapter09ApplicationTests {

    @Autowired
    private JpaUserRepository jpaUserRepository;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private LockService lockService;

    @Test
    void getRoleAll() {
        List<Role> roleList = roleRepository.findAll();
        roleList.stream().forEach(new Consumer<Role>() {
            @Override
            public void accept(Role role) {
                System.out.println(role);
                System.out.println(role.getUsers());
            }
        });

    }

    @Test
    void getBookAll() {
        List<Book> bookList = bookRepository.findAll();
        bookList.stream().forEach(System.out::println);
    }

    @Test
    void getAddressAll() {
        List<Address> addressList = addressRepository.findAll();
        addressList.stream().forEach(System.out::println);
        Address address = addressRepository.findById(1L).get();
        System.out.println(address.getUser().getUserAddress());
    }

    @Test
    void testFindAll() {
        List<User> list = jpaUserRepository.findAll();
        list.stream().forEach(System.out::println);
    }

    @Test
    void testFindbyId() {
        User user = jpaUserRepository.findById(65L).get();
        System.out.println(user);
        user.getRoles().stream().forEach(System.out::println);
    }

    @Test
    void testSave() {
        User user = new User();
        user.setName("jpa");
        user.setAge(10);
        user.setEmail("jpa@email.com");
        user.setDel(DelEnum.one);
        jpaUserRepository.save(user);
    }

    //级联
    //ALL PERSIST
    @Test
    void testSavePERSIST() {
        User user = new User();
        user.setName("PERSIST");
        user.setAge(10);
        user.setEmail("jpa@email.com");
        user.setDel(DelEnum.one);

        Address address = new Address();
        address.setCity("PERSIST");
        address.setUser(user);

        user.setUserAddress(address);

        //保存前会再查询一次，如果没有变化，则不会发送修改sql
        jpaUserRepository.save(user);
    }

    //ALL MERGE
    @Test
    void testSaveMERGE() {
        User user = jpaUserRepository.findById(65L).get();
        user.setName("222");

        Address address = user.getUserAddress();
        address.setCity("222");

        user.setUserAddress(address);
        //保存前会再查询一次，如果没有变化，则不会发送修改sql
        jpaUserRepository.save(user);
    }

    @Test
    void testLock() {

        for (int i1 = 0; i1 < 4; i1++) {
            boolean lock = lockService.tryLock("myLock", 30);
            if (lock) {
                try {
                    System.out.println("get lock ==" + i1);
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    //lockService.unlock("myLock");
                }
            } else {
                try {
                    System.out.println("can't get lock ==" + i1);
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
