package com.hanqf.mongo.model;

/**
 * <h1></h1>
 * Created by hanqf on 2024/3/4 18:28.
 */


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "goods")
public class Goods {

    @Id
    private String id;
    private String name;
    private List<Tag> tags;

    public Goods(String name, List<Tag> tags) {
        this.name = name;
        this.tags = tags;
    }
}

