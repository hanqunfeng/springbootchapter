package com.example.dataType;

import com.example.model.BookInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * <h1></h1>
 * Created by hanqf on 2022/6/30 15:25.
 */


public class ObjectMapperUtil {

    private static class MyList extends ArrayList<BookInfo> {

    }

    public static void main(String[] args) throws JsonProcessingException {

        ObjectMapper objectMapper = new ObjectMapper();
        String valueAsString = "";
        //支持，自定义子类类型，显示声明泛型类型
        final List<BookInfo> list1 = objectMapper.readValue(valueAsString, MyList.class);

        //支持，调用objectMapper的转list方法
        final ObjectReader objectReader = objectMapper.readerForListOf(BookInfo.class);
        final List<BookInfo> list2 = objectReader.readValue(valueAsString);


        //不行的
        Class<List<BookInfo>> bookInfoListClass = new CP_GenericsType<List<BookInfo>>() {}.getClassType();
        final List<BookInfo> list3 = objectMapper.readValue(valueAsString, bookInfoListClass);


        //支持，使用匿名内部类的方式获取Type，这里使用的是jackson的TypeReference
        final List<BookInfo> list4 = objectMapper.readerFor(new TypeReference<List<BookInfo>>() {}).readValue(valueAsString);

        final List<BookInfo> list5 = objectMapper
                .readerFor(objectMapper.getTypeFactory()
                        .constructType(new TypeReference<List<BookInfo>>() {}.getType()))
                .readValue(valueAsString);

        //支持，同样是匿名内部类的方法，这里使用的是自定义的CP_GenericsType
        final Type type = new CP_GenericsType<List<BookInfo>>() {}.getType();
        JavaType javaType = objectMapper.getTypeFactory().constructType(type);
        final List<BookInfo> list6 = objectMapper.readerFor(javaType).readValue(valueAsString);

        System.out.println();



    }
}
