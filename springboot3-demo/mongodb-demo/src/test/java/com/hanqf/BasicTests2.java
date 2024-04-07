package com.hanqf;

import com.hanqf.mongo.model.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.List;
@SpringBootTest
class BasicTests2 {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void initializeData() {
        // Initialize data
        List<Item> items = Arrays.asList(
                new Item("item1", 20, 5000,
                        Arrays.asList("red", "blue", "green"),
                        Arrays.asList("S", "M", "L"),
                        Arrays.asList(new Item.Tag("color", "red"),
                                new Item.Tag("size", "M"),
                                new Item.Tag("style", "casual"))),
                new Item("item2", 30, 8000,
                        Arrays.asList("green", "yellow"),
                        Arrays.asList("M", "L", "XL"),
                        Arrays.asList(new Item.Tag("color", "blue"),
                                new Item.Tag("size", "XL"),
                                new Item.Tag("style", "formal"))),
                new Item("item3", 25, 10000,
                        Arrays.asList("red", "green"),
                        Arrays.asList("L", "XL"),
                        Arrays.asList(new Item.Tag("color", "green"),
                                new Item.Tag("size", "L"),
                                new Item.Tag("style", "casual")))
        );

        mongoTemplate.insertAll(items);
    }

    @Test
    void testQueries() {
        BasicTests2.QueryExecutor queryExecutor = new BasicTests2.QueryExecutor(mongoTemplate);
        // db.collection.find({ "age": { "$lt": 25 } })
        printResult("findAgeLessThan25", queryExecutor.findAgeLessThan25());
        // db.collection.find({ "age": { "$lte": 25 } })
        printResult("findAgeLessThanOrEqualTo25", queryExecutor.findAgeLessThanOrEqualTo25());
        // db.collection.find({ "age": { "$gt": 25 } })
        printResult("findAgeGreaterThan25", queryExecutor.findAgeGreaterThan25());
        // db.collection.find({ "age": { "$gte": 25 } })
        printResult("findAgeGreaterThanOrEqualTo25", queryExecutor.findAgeGreaterThanOrEqualTo25());
        // db.collection.find({ "age": { "$ne": 25 } })
        printResult("findAgeNotEqualTo25", queryExecutor.findAgeNotEqualTo25());
        // db.collection.find({ "age": { "$in": [20, 25] } })
        printResult("findAgeInArray", queryExecutor.findAgeInArray(List.of(20, 25)));
        // db.collection.find({ "age": { "$nin": [20, 25] } })
        printResult("findAgeNotInArray", queryExecutor.findAgeNotInArray(List.of(20, 25)));
        // db.collection.find({ "$or": [ { "age": 20 }, { "salary": { "$gt": 8000 } } ] })
        printResult("findAgeOrSalary", queryExecutor.findAgeOrSalary());
        // db.collection.find({ "$and": [ { "age": 20 }, { "salary": { "$gt": 8000 } } ] })
        printResult("findAgeAndSalary", queryExecutor.findAgeAndSalary());
        /*
        db.collection.find({ "tags": { "$all": [
            { "$elemMatch": { "tagKey": "color", "tagValue": "red" } },
            { "$elemMatch": { "tagKey": "size", "tagValue": "XL" } }
        ]}})
         */
        printResult("findTagsWithColorAndSize", queryExecutor.findTagsWithColorAndSize());
        // db.collection.find({ "tags": { "$elemMatch": { "tagKey": "color", "tagValue": /blue/ } } })
        printResult("findTagsWithColorContainingBlue", queryExecutor.findTagsWithColorContainingBlue());
        // db.collection.find({ "tags.tagKey": "color" })
        printResult("findTagsWithColor", queryExecutor.findTagsWithColor());
        // db.collection.find({ "tags": { "$size": 3 } })
        printResult("findDocumentWithTagsSizeThree", queryExecutor.findDocumentWithTagsSizeThree());
        // db.collection.find({ "tags": { "$elemMatch": { "tagKey": "color", "tagValue": "red" } } })
        printResult("findDocumentWithSpecificTag", queryExecutor.findDocumentWithSpecificTag());
        /*
        db.collection.find({ "tags": { "$all": [
            { "$elemMatch": { "tagKey": "color", "tagValue": /green/ } }
        ]}})
         */
        printResult("findAllElementsWithSpecificTag", queryExecutor.findAllElementsWithSpecificTag());
        printResult("findAllElementsWithSpecificTagByJson", queryExecutor.findAllElementsWithSpecificTagByJson());
    }

    private void printResult(String queryName, List<Item> result) {
        System.out.println("Query: " + queryName + " ###################################################");
        for (Item item : result) {
            System.out.println(item);
        }
        System.out.println();
    }


    public static class QueryExecutor {
        private final MongoTemplate mongoTemplate;

        public QueryExecutor(MongoTemplate mongoTemplate) {
            this.mongoTemplate = mongoTemplate;
        }

        // 查询年龄小于 25 的文档
        // db.collection.find({ "age": { "$lt": 25 } })
        public List<Item> findAgeLessThan25() {
            Query query = new Query(Criteria.where("age").lt(25));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Item.class);
        }

        // 查询年龄小于等于 25 的文档
        // db.collection.find({ "age": { "$lte": 25 } })
        public List<Item> findAgeLessThanOrEqualTo25() {
            Query query = new Query(Criteria.where("age").lte(25));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Item.class);
        }

        // 查询年龄大于 25 的文档
        // db.collection.find({ "age": { "$gt": 25 } })
        public List<Item> findAgeGreaterThan25() {
            Query query = new Query(Criteria.where("age").gt(25));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Item.class);
        }

        // 查询年龄大于等于 25 的文档
        // db.collection.find({ "age": { "$gte": 25 } })
        public List<Item> findAgeGreaterThanOrEqualTo25() {
            Query query = new Query(Criteria.where("age").gte(25));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Item.class);
        }

        // 查询年龄不等于 25 的文档
        // db.collection.find({ "age": { "$ne": 25 } })
        public List<Item> findAgeNotEqualTo25() {
            Query query = new Query(Criteria.where("age").ne(25));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Item.class);
        }

        // 查询年龄存在并且在指定数组中的文档
        // db.collection.find({ "age": { "$in": [20, 25] } })
        public List<Item> findAgeInArray(List<Integer> ages) {
            Query query = new Query(Criteria.where("age").in(ages));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Item.class);
        }

        // 查询年龄不存在或者不在指定数组中的文档
        // db.collection.find({ "age": { "$nin": [20, 25] } })
        public List<Item> findAgeNotInArray(List<Integer> ages) {
            Query query = new Query(Criteria.where("age").nin(ages));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Item.class);
        }

        // 查询年龄存在并且为 20，或者薪资大于 8000 的文档
        // db.collection.find({ "$or": [ { "age": 20 }, { "salary": { "$gt": 8000 } } ] })
        public List<Item> findAgeOrSalary() {
            Query query = new Query(new Criteria().orOperator(
                    Criteria.where("age").is(20),
                    Criteria.where("salary").gt(8000)
            ));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Item.class);
        }

        // 查询年龄为 20，并且薪资大于 8000 的文档
        // db.collection.find({ "$and": [ { "age": 20 }, { "salary": { "$gt": 8000 } } ] })
        public List<Item> findAgeAndSalary() {
            Query query = new Query(new Criteria().andOperator(
                    Criteria.where("age").is(20),
                    Criteria.where("salary").gt(8000)
            ));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Item.class);
        }

        // 查询包含 tagKey 为 "color"，tagValue 为 "red" 的标签，并且包含 tagKey 为 "size"，tagValue 为 "XL" 的标签的文档
        /*
        db.collection.find({ "tags": { "$all": [
            { "$elemMatch": { "tagKey": "color", "tagValue": "red" } },
            { "$elemMatch": { "tagKey": "size", "tagValue": "XL" } }
        ]}})
         */
        public List<Item> findTagsWithColorAndSize() {
            Criteria criteria = new Criteria().andOperator(
                    Criteria.where("tags").elemMatch(
                            Criteria.where("tagKey").is("color").and("tagValue").is("red")
                    ),
                    Criteria.where("tags").elemMatch(
                            Criteria.where("tagKey").is("size").and("tagValue").is("XL")
                    )
            );
            Query query = new Query(criteria);
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Item.class);
        }

        // 查询包含 tagKey 为 "color"，tagValue 包含 "blue" 的标签的文档
        // db.collection.find({ "tags": { "$elemMatch": { "tagKey": "color", "tagValue": /blue/ } } })
        public List<Item> findTagsWithColorContainingBlue() {
            Query query = new Query(Criteria.where("tags").elemMatch(
                    Criteria.where("tagKey").is("color").and("tagValue").regex("blue")
            ));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Item.class);
        }


        // 查询包含 tagKey 为 "color" 的标签的文档
        // db.collection.find({ "tags.tagKey": "color" })
        public List<Item> findTagsWithColor() {
            Query query = new Query(Criteria.where("tags.tagKey").is("color"));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Item.class);
        }

        // 查询 tags 数组长度为 3 的文档
        // db.collection.find({ "tags": { "$size": 3 } })
        public List<Item> findDocumentWithTagsSizeThree() {
            Query query = new Query(Criteria.where("tags").size(3));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Item.class);
        }

        // 查询包含 tagKey 为 "color"，tagValue 为 "red" 的标签的文档
        // db.collection.find({ "tags": { "$elemMatch": { "tagKey": "color", "tagValue": "red" } } })
        public List<Item> findDocumentWithSpecificTag() {
            Query query = new Query(Criteria.where("tags").elemMatch(
                    Criteria.where("tagKey").is("color").and("tagValue").is("red")
            ));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Item.class);
        }

        // 查询 tags 数组所有元素满足 tagKey 为 "color"，tagValue 包含 "green" 的标签的文档
        /*
        db.collection.find({ "tags": { "$all": [
            { "$elemMatch": { "tagKey": "color", "tagValue": /green/ } }
        ]}})
         */
        public List<Item> findAllElementsWithSpecificTag() {
            Query query = new Query(Criteria.where("tags").elemMatch(
                    Criteria.where("tagKey").is("color").and("tagValue").regex("green")
            ));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Item.class);
        }

        // 查询 tags 数组所有元素满足 tagKey 为 "color"，tagValue 包含 "green" 的标签的文档
        // 使用 BasicQuery，可以使用 json 字符串，这样就可以直接使用 mongo 的查询语法
        public List<Item> findAllElementsWithSpecificTagByJson() {
            String json = """
                    { "tags": { "$all": [
                                { "$elemMatch": { "tagKey": "color", "tagValue": /green/ } }
                            ]}}
                    """;
            Query query = new BasicQuery(json);
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Item.class);
        }
    }
}
