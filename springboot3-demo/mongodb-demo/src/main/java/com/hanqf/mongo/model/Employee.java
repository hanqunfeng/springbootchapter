package com.hanqf.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;

/**
 * <h1></h1>
 * Created by hanqf on 2024/3/4 17:29.
 */

/**
 * @Description: 相关注解
 * - @Document
 * - 修饰范围: 用在类上
 * - 作用: 用来映射这个类的一个对象为mongo中一条文档数据。
 * - 属性:( value 、collection )用来指定操作的集合名称
 * <p>
 * - @Id
 * - 修饰范围: 用在成员变量、方法上
 * - 作用: 用来将成员变量的值映射为文档的_id的值
 * <p>
 * - @Field
 * - 修饰范围: 用在成员变量、方法上
 * - 作用: 用来将成员变量及其值映射为文档中一个key:value对。
 * - 属性:( name , value )用来指定在文档中 key的名称,默认为成员变量名
 * <p>
 * - @Transient
 * - 修饰范围:用在成员变量、方法上
 * - 作用:用来指定此成员变量不参与文档的序列化
 */

@Document("employee")  //对应employee集合中的一个文档
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {

    @Id   //映射文档中的_id
    private Integer id;
    @Field("username")
    private String name;
    @Field
    private int age;
    @Field
    private Double salary;
    @Field
    private Date entryDay;
}
