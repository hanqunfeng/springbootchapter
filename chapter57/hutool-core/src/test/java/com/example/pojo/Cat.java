package com.example.pojo;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * <h1></h1>
 * Created by hanqf on 2021/12/11 20:43.
 */

//子类继承父类时，可以让子类的Builder拥有父类的属性，父类和子类要同时声明该注解
@SuperBuilder
//toString要展示父类的属性
@Data
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
//json拷贝需要无参构造方法
@NoArgsConstructor
@AllArgsConstructor
public class Cat extends Animal implements Serializable {

    private static final long serialVersionUID = 7184465324004518125L;
    private int age;

    private List<String> types;

}
