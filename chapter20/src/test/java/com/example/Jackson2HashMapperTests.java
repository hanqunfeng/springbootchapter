package com.example;

import com.example.model.Address;
import com.example.model.Person;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.hash.HashMapper;
import org.springframework.data.redis.hash.Jackson2HashMapper;

import javax.annotation.Resource;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * <h1>Jackson2HashMapper测试用例</h1>
 * Created by hanqf on 2020/10/18 21:28.
 */

@SpringBootTest
public class Jackson2HashMapperTests {

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, Object> jacksonHashOperations;

    /**
     * 参数为true,则对象内部的类属性里面的属性都会被展开作为独立属性存储到redis,否则类会作为json存储
    */
    private HashMapper<Object, String, Object> jackson2HashMapper = new Jackson2HashMapper(true);


    @Test
    public void testHashPutAll() {
        Person person = new Person("kobe", "bryant");
        person.setId("kobe");
        person.setAddress(new Address("洛杉矶", "美国"));

        Map<String, Object> mapperHash = jackson2HashMapper.toHash(person);

        jacksonHashOperations.putAll("player:" + person.getId(), mapperHash);

        Map<String,Object> loaderHash = jacksonHashOperations.entries("player:" + person.getId());
        Object map = jackson2HashMapper.fromHash(loaderHash);
        Person convertValue = new ObjectMapper().convertValue(map, Person.class);

        System.out.println(convertValue);
        assertEquals(convertValue.getFirstName(), person.getFirstName());
    }
}
