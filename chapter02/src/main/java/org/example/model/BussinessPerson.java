package org.example.model;/**
 * Created by hanqf on 2020/3/2 11:52.
 */


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author hanqf
 * @date 2020/3/2 11:52
 */
@Component
public class BussinessPerson implements Person {

    //@Autowired
    //@Qualifier("dog")  1
    private Animal animal = null;

    //也可以通过构造方法指定注入的对象 2
    public BussinessPerson(@Autowired @Qualifier("dog") Animal animal) {
        this.animal = animal;
    }

    @Override
    public void service() {
        this.animal.use();
    }

    @Override
    //@Autowired @Qualifier("dog") 声明在set方法上也可以 3
    public void setAnimal(Animal animal) {
        System.out.println("Animal:我被注入了，用于测试延迟依赖注入");
        this.animal = animal;
    }
}
