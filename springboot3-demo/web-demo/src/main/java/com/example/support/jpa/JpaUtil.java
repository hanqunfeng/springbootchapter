package com.example.support.jpa;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * <h1></h1>
 * Created by hanqf on 2021/9/2 17:16.
 */

@Component("jpaUtil")
@Slf4j
public class JpaUtil {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 判断className是否为基本类型
     *
     * @param className
     * @return
     */
    private boolean isBaseType(Class className) {
        if (className.equals(Integer.class) ||
                className.equals(BigInteger.class) ||
                className.equals(Byte.class) ||
                className.equals(Long.class) ||
                className.equals(Double.class) ||
                className.equals(Float.class) ||
                className.equals(Character.class) ||
                className.equals(Short.class) ||
                className.equals(Boolean.class)) {
            return true;
        }
        return false;
    }


    /**
     * 查询结果为List<Map>时，可以通过该方法转换为对象List，注意Map中key要与对象属性匹配，或者对象属性标注了@JsonProperty
     */
    public <E> List<E> mapListToObjectList(List<Map> mapList, Class<E> clazz) {
        return mapListToObjectList(mapList, clazz, isBaseType(clazz));
    }

    private <E> List<E> mapListToObjectList(List<Map> mapList, Class<E> clazz, boolean basic) {

        List<E> list = new ArrayList<>();
        E newInstance = null;
        String valueAsString = "";

        for (Map map : mapList) {
            try {
                if (basic) {
                    valueAsString = objectMapper.writeValueAsString(map.values().stream().findFirst().get());
                } else {
                    valueAsString = objectMapper.writeValueAsString(map);
                }
                newInstance = objectMapper.readValue(valueAsString, clazz);
                list.add(newInstance);
            } catch (JsonProcessingException e) {
                log.error("", e);
            }
        }
        return list;
    }

    /**
     * 查询结果为Map时，可以通过该方法转换为对象，注意Map中key要与对象属性匹配，或者对象属性标注了@JsonProperty
     */
    public <E> E mapToObject(Map map, Class<E> clazz) {
        return mapToObject(map, clazz, isBaseType(clazz));
    }

    private <E> E mapToObject(Map map, Class<E> clazz, boolean basic) {
        if (map == null) {
            return null;
        }
        E newInstance = null;
        String valueAsString = "";
        //基本类型，说明返回值只有一列
        try {
            if (basic) {
                valueAsString = objectMapper.writeValueAsString(map.values().stream().findFirst().get());
            } else {
                valueAsString = objectMapper.writeValueAsString(map);
            }
            newInstance = objectMapper.readValue(valueAsString, clazz);
        } catch (JsonProcessingException e) {
            log.error("", e);
        }

        return newInstance;
    }
}
