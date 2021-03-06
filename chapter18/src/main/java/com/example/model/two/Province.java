package com.example.model.two;/**
 * Created by hanqf on 2020/3/16 13:58.
 */


import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;

/**
 * @author hanqf
 * @date 2020/3/16 13:58
 */
@Document(collection = "province")
public class Province implements Serializable {

    private static final long serialVersionUID = -7229906944062898852L;

    /** ID */
    @Id
    private String id;

    /** 省份名称 */
    @Field(name = "name")
    private String name;


    @Override
    public String toString() {
        return "City{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
