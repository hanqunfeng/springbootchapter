package com.example.mongo.model;/**
 * Created by hanqf on 2020/3/16 13:58.
 */


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * @author hanqf
 * @date 2020/3/16 13:58
 */
@Document(collection = "mongouser")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MongoUser implements Serializable {

    private static final long serialVersionUID = -7229906944062898852L;

    /** ID */
    @Id
    private String id;

    /** 用户名 */
    @Field(name = "name")
    @Indexed(unique = true)
    private String userName;

    /** 年龄 */
    @Field(name = "age")
    private Integer age;

    /** 邮箱 */
    @Field(name = "email")
    private String email;

    /** 博客地址 */
    @Field(name = "blog")
    private String blog;

    /** 标签 */
    @Field(name = "tags")
    private String[] tags;

}
