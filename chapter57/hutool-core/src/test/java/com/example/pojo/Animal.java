package com.example.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

/**
 * <h1></h1>
 * Created by hanqf on 2021/12/11 20:44.
 */

//子类继承父类时，可以让子类的Builder拥有父类的属性，父类和子类要同时声明该注解
@SuperBuilder
@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Animal {

    protected String name;
    protected String color;
}
