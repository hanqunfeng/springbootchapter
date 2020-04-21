package com.example.externalproperties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * <p></p>
 * Created by hanqf on 2020/4/17 22:33.
 */

@Component
@PropertySource(value = "file:demo.properties",encoding = "utf-8")
@ConfigurationProperties(prefix = "demo.data")
@Data
public class PropertiesDemo {
    private Map<String, String> map = new HashMap<>();
    private Map<String, String> map2 = new HashMap<>();
    private Set<String> set = new HashSet<>();
    private Set<String> set2 = new HashSet<>();
    private List<String> list = new ArrayList<>();
    private List<String> list2 = new ArrayList<>();

    private String name;
    private Integer age;
    private Double salary;
}
