package com.example;

import cn.hutool.core.util.ObjectUtil;
import com.example.pojo.Cat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Arrays;

/**
 * <h1>克隆</h1>
 * Created by hanqf on 2021/12/11 20:42.
 */


public class CloneableTest {

    @Test
    public void Cloneable() throws JsonProcessingException {

        Cat cat = Cat.builder()
                .name("波斯猫")
                .color("白色")
                .age(10)
                .types(Arrays.asList("东北","华北"))
                .build();

        System.out.println(cat);

        //这种拷贝不能复制父类的属性
        final Cat clone = ObjectUtil.cloneByStream(cat);
        System.out.println(clone);
        System.out.println(clone.getName());

        System.out.println(ObjectUtil.clone(cat));

        //json转换可以复制父类属性
        ObjectMapper objectMapper = new ObjectMapper();
        final String asString = objectMapper.writeValueAsString(cat);
        System.out.println(asString);
        final Cat readValue = objectMapper.readValue(asString, Cat.class);
        System.out.println(readValue);


    }
}
