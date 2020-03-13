package com.example;

import com.example.dao.AddressRepository;
import com.example.dao.BookRepository;
import com.example.dao.JpaUserRepository;
import com.example.model.Address;
import com.example.model.Book;
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

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private BookRepository bookRepository;

    @Test
    void getBookAll(){
        List<Book> bookList = bookRepository.findAll();
        bookList.stream().forEach(System.out::println);
    }

    @Test
    void getAddressAll(){
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
    void testSave(){
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
    void testSavePERSIST(){
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
    void testSaveMERGE(){
        User user = jpaUserRepository.findById(65L).get();
        user.setName("222");

        Address address = user.getUserAddress();
        address.setCity("222");

        user.setUserAddress(address);
        //保存前会再查询一次，如果没有变化，则不会发送修改sql
        jpaUserRepository.save(user);
    }

}
