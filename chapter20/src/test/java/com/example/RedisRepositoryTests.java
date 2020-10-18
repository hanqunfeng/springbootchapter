package com.example;

import com.example.model.Address;
import com.example.model.Person;
import com.example.repository.PersonRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <h1>redisRepository 测试用例</h1>
 * Created by hanqf on 2020/10/18 22:01.
 */

@SpringBootTest
public class RedisRepositoryTests {

    @Autowired
    private PersonRepository personRepository;

    @Test
    public void testHashPutAll(){
        Person person = new Person("Jadon","Macer");
        person.setAddress(new Address("芝加哥","美国"));
        //如果不设置id值，则会默认生成一个，格式为：2e3e7660-cd1e-49a9-ba0f-952ede6d3e24
        person.setId("123");

        personRepository.save(person);

        Optional<Person> optionalPerson = personRepository.findById(person.getId());
        optionalPerson.ifPresent(person1 -> {
            System.out.println(person1);
            assertEquals(person.getFirstName(),person1.getFirstName());
        });

        long count = personRepository.count();
        System.out.println(count);

    }
}
