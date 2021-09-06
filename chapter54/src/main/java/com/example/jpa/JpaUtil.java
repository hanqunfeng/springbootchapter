package com.example.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <h1></h1>
 * Created by hanqf on 2021/9/2 17:16.
 */

@Component("jpaUtil")
public class JpaUtil {

    @Autowired
    ObjectMapper objectMapper;

    /**
     * 查询结果为List<Map>时，可以通过该方法转换为对象List，注意Map中key要与对象属性匹配，或者对象属性标注了@JsonProperty
     */
    public <E> List<E> mapListToObjectList(List<Map> mapList, Class clazz, boolean basic) {

        List<E> list = new ArrayList<>();
        for (Map map : mapList) {
            if (basic) {
                list.add((E) map.values().stream().findFirst().get());

            } else {
                try {
                    final String valueAsString = objectMapper.writeValueAsString(map);
                    E newInstance = (E) objectMapper.readValue(valueAsString, clazz);
                    list.add(newInstance);
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    /**
     * 查询结果为Map时，可以通过该方法转换为对象，注意Map中key要与对象属性匹配，或者对象属性标注了@JsonProperty
     */
    public <E> E mapToObject(Map map, Class clazz, boolean basic) {
        if(map == null){
            return null;
        }
        E newInstance = null;
        //基本类型，说明返回值只有一列
        if (basic) {
            newInstance = (E) map.values().stream().findFirst().get();

        } else {
            try {
                final String valueAsString = objectMapper.writeValueAsString(map);
                newInstance = (E) objectMapper.readValue(valueAsString, clazz);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        }
        return newInstance;
    }
}
