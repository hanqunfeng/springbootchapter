package org.example.model;/**
 * Created by hanqf on 2020/3/2 16:15.
 */


/**
 * @author hanqf
 * @date 2020/3/2 16:15
 */
public class ConditionProperties {
    private String first;
    private String second;

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    @Override
    public String toString() {
        return "ConditionProperties{" +
                "first='" + first + '\'' +
                ", second='" + second + '\'' +
                '}';
    }
}
