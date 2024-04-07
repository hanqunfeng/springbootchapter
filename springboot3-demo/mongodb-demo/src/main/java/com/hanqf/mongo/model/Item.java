package com.hanqf.mongo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "collection")
public class Item {

    @Id
    private String id;
    @Field
    private String name;
    @Field
    private int age;
    @Field
    private double salary;
    @Field
    private List<String> colors;
    @Field
    private List<String> sizes;
    @Field
    private List<Tag> tags;

    public Item(String name, int age, double salary, List<String> colors, List<String> sizes, List<Tag> tags) {
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.colors = colors;
        this.sizes = sizes;
        this.tags = tags;
    }




    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Tag {
        private String tagKey;
        private String tagValue;

    }
}

