package com.hanqf;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Arrays;
@SpringBootTest
class BulkWriteTests {
    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void insertManyPizzas() {
        mongoTemplate.insertAll(Arrays.asList(
                new Pizza(0, "pepperoni", "small", 4),
                new Pizza(1, "cheese", "medium", 7),
                new Pizza(2, "vegan", "large", 8)
        ));
    }

    @Test
    void performBulkWrite() {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.ORDERED, Pizza.class);

        bulkOperations.insert(Arrays.asList(
                new Pizza(3, "beef", "medium", 6),
                new Pizza(4, "sausage", "large", 10)
        ));

        bulkOperations.updateOne(
                Query.query(Criteria.where("type").is("cheese")),
                new Update().set("price", 8)
        );

        bulkOperations.remove(
                Query.query(Criteria.where("type").is("pepperoni")).limit(1)
        );

        bulkOperations.replaceOne(
                Query.query(Criteria.where("type").is("vegan")),
                new Pizza(null, "tofu", "small", 4)
        );

        bulkOperations.execute();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Document("pizza")
    private static class Pizza {
        @Id
        private Integer _id;
        private String type;
        private String size;
        private Integer price;

    }
}
