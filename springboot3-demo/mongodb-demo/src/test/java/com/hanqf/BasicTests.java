package com.hanqf;

import com.mongodb.client.DistinctIterable;
import org.bson.BsonDocument;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <h1>基本操作测试类</h1>
 * Created by hanqf on 2024/3/5 12:02.
 */

@SpringBootTest
class BasicTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    /*
    // 初始化数据
    db.collection.insertMany([
        {
            "name": "item1",
            "age": 20,
            "salary": 5000,
            "colors": ["red", "blue", "green"],
            "sizes": ["S", "M", "L"],
            "tags": [
                { "tagKey": "color", "tagValue": "red" },
                { "tagKey": "size", "tagValue": "M" },
                { "tagKey": "style", "tagValue": "casual" }
            ]
        },
        {
            "name": "item2",
            "age": 30,
            "salary": 8000,
            "colors": ["green", "yellow"],
            "sizes": ["M", "L", "XL"],
            "tags": [
                { "tagKey": "color", "tagValue": "blue" },
                { "tagKey": "size", "tagValue": "XL" },
                { "tagKey": "style", "tagValue": "formal" }
            ]
        },
        {
            "name": "item3",
            "age": 25,
            "salary": 10000,
            "colors": ["red", "green"],
            "sizes": ["L", "XL"],
            "tags": [
                { "tagKey": "color", "tagValue": "green" },
                { "tagKey": "size", "tagValue": "L" },
                { "tagKey": "style", "tagValue": "casual" }
            ]
        }
    ]);
     */
    @Test
    void initializeData() {
        List<Document> documents = Arrays.asList(
                // 文档1
                new Document().append("name", "item1")
                        .append("age", 20)
                        .append("salary", 5000)
                        .append("colors", Arrays.asList("red", "blue", "green"))
                        .append("sizes", Arrays.asList("S", "M", "L"))
                        .append("tags", Arrays.asList(
                                new Document().append("tagKey", "color")
                                        .append("tagValue", "red"),
                                new Document().append("tagKey", "size")
                                        .append("tagValue", "M"),
                                new Document().append("tagKey", "style")
                                        .append("tagValue", "casual")
                        )),
                // 文档2
                new Document().append("name", "item2")
                        .append("age", 30)
                        .append("salary", 8000)
                        .append("colors", Arrays.asList("green", "yellow"))
                        .append("sizes", Arrays.asList("M", "L", "XL"))
                        .append("tags", Arrays.asList(
                                new Document().append("tagKey", "color")
                                        .append("tagValue", "blue"),
                                new Document().append("tagKey", "size")
                                        .append("tagValue", "XL"),
                                new Document().append("tagKey", "style")
                                        .append("tagValue", "formal")
                        )),
                // 文档3
                new Document().append("name", "item3")
                        .append("age", 25)
                        .append("salary", 10000)
                        .append("colors", Arrays.asList("red", "green"))
                        .append("sizes", Arrays.asList("L", "XL"))
                        .append("tags", Arrays.asList(
                                new Document().append("tagKey", "color")
                                        .append("tagValue", "green"),
                                new Document().append("tagKey", "size")
                                        .append("tagValue", "L"),
                                new Document().append("tagKey", "style")
                                        .append("tagValue", "casual")
                        ))
        );

        mongoTemplate.insert(documents, "collection");
    }


    /**
     * 也可以通过json来构建Document对象
     */
    @Test
    void initializeData2() {
        List<String> jsonList = new ArrayList<>();
        jsonList.add("""
                {
                    "name": "item1",
                    "age": 20,
                    "salary": 5000,
                    "colors": ["red", "blue", "green"],
                    "sizes": ["S", "M", "L"],
                    "tags": [
                        { "tagKey": "color", "tagValue": "red" },
                        { "tagKey": "size", "tagValue": "M" },
                        { "tagKey": "style", "tagValue": "casual" }
                    ]
                }
                """);
        jsonList.add("""
                {
                    "name": "item2",
                    "age": 30,
                    "salary": 8000,
                    "colors": ["green", "yellow"],
                    "sizes": ["M", "L", "XL"],
                    "tags": [
                        { "tagKey": "color", "tagValue": "blue" },
                        { "tagKey": "size", "tagValue": "XL" },
                        { "tagKey": "style", "tagValue": "formal" }
                    ]
                }
                """);
        jsonList.add("""
                {
                    "name": "item3",
                    "age": 25,
                    "salary": 10000,
                    "colors": ["red", "green"],
                    "sizes": ["L", "XL"],
                    "tags": [
                        { "tagKey": "color", "tagValue": "green" },
                        { "tagKey": "size", "tagValue": "L" },
                        { "tagKey": "style", "tagValue": "casual" }
                    ]
                }
                """);

        List<Document> documents = new ArrayList<>();
        for (String json : jsonList) {
            // 通过Document.parse()方法将json字符串转换为Document对象
            documents.add(Document.parse(json));
        }
        mongoTemplate.insert(documents, "collection");
    }


    // db.collection.distinct("name",{age:{$gte:25}})
    @Test
    void findDistinctNamesWithAgeGreaterThanEqualTest() {
        // 方式1
            DistinctIterable<String> distinctIterable = mongoTemplate.getCollection("collection")
                    .distinct("name", BsonDocument.parse("{age:{$gte:25}}"), String.class);

        // 方式2
//        DistinctIterable<String> distinctIterable = mongoTemplate.getCollection("collection")
//                .distinct("name", new Document("age", new Document("$gte", 25)), String.class);


        // 将DistinctIterable转换为List
        List<String> distinctNames = new ArrayList<>();
        distinctIterable.iterator().forEachRemaining(distinctNames::add);

        distinctNames.forEach(System.out::println);

    }

    /*
    db.collection.aggregate([
        {
            $match: { age: { $gte: 25 } }
        },
        {
            $group: {
                _id: "$name"
            }
        }
    ])
     */
    @Test
    void findDistinctNamesWithAgeGreaterThanEqualTest2() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("age").gte(25)),
                Aggregation.group("name")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "collection", Document.class);
        List<String> distinctNames = results.getMappedResults().stream()
                .map(document -> document.getString("_id"))
                .toList();

        distinctNames.forEach(System.out::println);

    }
    @Test
    void testQueries() {
        QueryExecutor queryExecutor = new QueryExecutor(mongoTemplate);
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

    private void printResult(String queryName, List<Document> result) {
        System.out.println("Query: " + queryName + " ###################################################");
        for (Document document : result) {
            System.out.println(document);
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
        public List<Document> findAgeLessThan25() {
            Query query = new Query(Criteria.where("age").lt(25));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Document.class, "collection");
        }

        // 查询年龄小于等于 25 的文档
        // db.collection.find({ "age": { "$lte": 25 } })
        public List<Document> findAgeLessThanOrEqualTo25() {
            Query query = new Query(Criteria.where("age").lte(25));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Document.class, "collection");
        }

        // 查询年龄大于 25 的文档
        // db.collection.find({ "age": { "$gt": 25 } })
        public List<Document> findAgeGreaterThan25() {
            Query query = new Query(Criteria.where("age").gt(25));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Document.class, "collection");
        }

        // 查询年龄大于等于 25 的文档
        // db.collection.find({ "age": { "$gte": 25 } })
        public List<Document> findAgeGreaterThanOrEqualTo25() {
            Query query = new Query(Criteria.where("age").gte(25));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Document.class, "collection");
        }

        // 查询年龄不等于 25 的文档
        // db.collection.find({ "age": { "$ne": 25 } })
        public List<Document> findAgeNotEqualTo25() {
            Query query = new Query(Criteria.where("age").ne(25));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Document.class, "collection");
        }

        // 查询年龄存在并且在指定数组中的文档
        // db.collection.find({ "age": { "$in": [20, 25] } })
        public List<Document> findAgeInArray(List<Integer> ages) {
            Query query = new Query(Criteria.where("age").in(ages));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Document.class, "collection");
        }

        // 查询年龄不存在或者不在指定数组中的文档
        // db.collection.find({ "age": { "$nin": [20, 25] } })
        public List<Document> findAgeNotInArray(List<Integer> ages) {
            Query query = new Query(Criteria.where("age").nin(ages));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Document.class, "collection");
        }

        // 查询年龄存在并且为 20，或者薪资大于 8000 的文档
        // db.collection.find({ "$or": [ { "age": 20 }, { "salary": { "$gt": 8000 } } ] })
        public List<Document> findAgeOrSalary() {
            Query query = new Query(new Criteria().orOperator(Criteria.where("age").is(20), Criteria.where("salary").gt(8000)));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Document.class, "collection");
        }

        // 查询年龄为 20，并且薪资大于 8000 的文档
        // db.collection.find({ "$and": [ { "age": 20 }, { "salary": { "$gt": 8000 } } ] })
        public List<Document> findAgeAndSalary() {
            Query query = new Query(new Criteria().andOperator(Criteria.where("age").is(20), Criteria.where("salary").gt(8000)));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Document.class, "collection");
        }

        // 查询包含 tagKey 为 "color"，tagValue 为 "red" 的标签，并且包含 tagKey 为 "size"，tagValue 为 "XL" 的标签的文档
        /*
        db.collection.find({ "tags": { "$all": [
            { "$elemMatch": { "tagKey": "color", "tagValue": "red" } },
            { "$elemMatch": { "tagKey": "size", "tagValue": "XL" } }
        ]}})
         */
        public List<Document> findTagsWithColorAndSize() {
            Criteria criteria = new Criteria().andOperator(Criteria.where("tags").elemMatch(Criteria.where("tagKey").is("color").and("tagValue").is("red")), Criteria.where("tags").elemMatch(Criteria.where("tagKey").is("size").and("tagValue").is("XL")));
            Query query = new Query(criteria);
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Document.class, "collection");
        }

        // 查询包含 tagKey 为 "color"，tagValue 包含 "blue" 的标签的文档
        // db.collection.find({ "tags": { "$elemMatch": { "tagKey": "color", "tagValue": /blue/ } } })
        public List<Document> findTagsWithColorContainingBlue() {
            Query query = new Query(Criteria.where("tags").elemMatch(Criteria.where("tagKey").is("color").and("tagValue").regex("blue")));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Document.class, "collection");
        }


        // 查询包含 tagKey 为 "color" 的标签的文档
        // db.collection.find({ "tags.tagKey": "color" })
        public List<Document> findTagsWithColor() {
            Query query = new Query(Criteria.where("tags.tagKey").is("color"));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Document.class, "collection");
        }

        // 查询 tags 数组长度为 3 的文档
        // db.collection.find({ "tags": { "$size": 3 } })
        public List<Document> findDocumentWithTagsSizeThree() {
            Query query = new Query(Criteria.where("tags").size(3));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Document.class, "collection");
        }

        // 查询包含 tagKey 为 "color"，tagValue 为 "red" 的标签的文档
        // db.collection.find({ "tags": { "$elemMatch": { "tagKey": "color", "tagValue": "red" } } })
        public List<Document> findDocumentWithSpecificTag() {
            Query query = new Query(Criteria.where("tags").elemMatch(Criteria.where("tagKey").is("color").and("tagValue").is("red")));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Document.class, "collection");
        }

        // 查询 tags 数组所有元素满足 tagKey 为 "color"，tagValue 包含 "green" 的标签的文档
        /*
        db.collection.find({ "tags": { "$all": [
            { "$elemMatch": { "tagKey": "color", "tagValue": /green/ } }
        ]}})
         */
        public List<Document> findAllElementsWithSpecificTag() {
            Query query = new Query(Criteria.where("tags").elemMatch(Criteria.where("tagKey").is("color").and("tagValue").regex("green")));
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Document.class, "collection");
        }

        // 查询 tags 数组所有元素满足 tagKey 为 "color"，tagValue 包含 "green" 的标签的文档
        // 使用 BasicQuery，可以使用 json 字符串，这样就可以直接使用 mongo 的查询语法
        public List<Document> findAllElementsWithSpecificTagByJson() {
            String json = """
                    { "tags": { "$all": [
                                { "$elemMatch": { "tagKey": "color", "tagValue": /green/ } }
                            ]}}
                    """;
            Query query = new BasicQuery(json);
            System.out.println(query); // 打印出生成的查询字符串
            return mongoTemplate.find(query, Document.class, "collection");
        }
    }
}
