package org.example.model;/**
 * Created by hanqf on 2020/3/3 15:30.
 */


/**
 * @author hanqf
 * @date 2020/3/3 15:30
 */
public class OtherBean {
    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "OtherBean{" +
                "name='" + name + '\'' +
                '}';
    }
}
