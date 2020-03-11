package org.example.model;/**
 * Created by hanqf on 2020/3/2 16:43.
 */


import org.springframework.beans.factory.annotation.Value;

/**
 * @author hanqf
 * @date 2020/3/2 16:43
 */

public class ProfilePreperties {

    @Value("${first.value}")
    private String first;
    @Value("${second.value}")
    private String second;



    @Override
    public String toString() {
        return "ReadProperties{" +
                "first='" + first + '\'' +
                ", second='" + second + '\'' +
                '}';
    }

}
