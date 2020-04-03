package com.example.model;/**
 * Created by hanqf on 2020/3/8 18:06.
 */


import javax.persistence.AttributeConverter;

/**
 * @author hanqf
 * @date 2020/3/8 18:06
 */
public class DelConverter implements AttributeConverter<DelEnum, String> {

    //将枚举转换为数据库列
    @Override
    public String convertToDatabaseColumn(DelEnum delEnum) {
        return delEnum.getId();
    }
    //将数据库列转换为枚举
    @Override
    public DelEnum convertToEntityAttribute(String s) {
        return DelEnum.getDelEnum(s);
    }
}
