package org.example.model;/**
 * Created by hanqf on 2020/3/2 15:13.
 */


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author hanqf
 * @date 2020/3/2 15:13
 */
@Component
public class ReadProperties {
    @Value("${first.value}")
    private String first;

    @Value("${second.value}")
    private String second;

    public String getFirst() {
        return first;
    }


    public String getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "ReadProperties{" +
                "first='" + first + '\'' +
                ", second='" + second + '\'' +
                '}';
    }
}
