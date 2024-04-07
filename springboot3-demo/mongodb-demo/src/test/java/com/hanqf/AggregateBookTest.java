package com.hanqf;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * <h1></h1>
 * Created by hanqf on 2024/3/7 17:34.
 */

@SpringBootTest
public class AggregateBookTest {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Test
    void initData() {
        String[] tags = {"nosql", "mongodb", "document", "developer", "popular"};
        String[] types = {"technology", "sociality", "travel", "novel", "literature"};
        List<Document> books = new ArrayList<>();

        Random random = new Random();
        for (int i = 0; i < 50; i++) {
            int typeIdx = random.nextInt(types.length);
            int tagIdx = random.nextInt(tags.length);
            int tagIdx2 = random.nextInt(tags.length);
            int favCount = random.nextInt(100);
            String username = "xx00" + random.nextInt(10);
            int age = 20 + random.nextInt(15);

            Document book = new Document("title", "book-" + i)
                    .append("type", types[typeIdx])
                    .append("tag", List.of(tags[tagIdx], tags[tagIdx2]))
                    .append("favCount", favCount)
                    .append("author", new Document("name", username).append("age", age));
            books.add(book);
        }

        mongoTemplate.insert(books, "books");
    }

    // db.books.aggregate([{$project:{name:"$title"}}])
    @Test
    void project1() {
//        AggregateIterable<Document> result = mongoTemplate.getCollection("books").aggregate(Arrays.asList(
//                new Document("$project", new Document("name", "$title"))
//        ));

        ProjectionOperation projectOperation = Aggregation.project()
                .andExpression("title").as("name");
        Aggregation aggregation = Aggregation.newAggregation(projectOperation);
        System.out.println(aggregation);
        List<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class).getMappedResults();

        for (Document document : results) {
            System.out.println(document.toJson());
        }
    }

    // db.books.aggregate([{$project:{name:"$title",_id:0,type:1,"author.name":1}}])
    @Test
    void project2() {
//        AggregateIterable<Document> result = mongoTemplate.getCollection("books").aggregate(Arrays.asList(
//                new Document("$project", new Document("name", "$title")
//                        .append("type", 1)
//                        .append("author.name", 1)
//                        .append("_id", 0))
//        ));

        ProjectionOperation projectOperation = Aggregation.project()
                .andExclude("_id")
                .andInclude("type")
                .andExpression("author.name").as("author.name")
                .andExpression("title").as("name");
        Aggregation aggregation = Aggregation.newAggregation(projectOperation);
        System.out.println(aggregation);
        List<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class).getMappedResults();

        for (Document document : results) {
            System.out.println(document.toJson());
        }
    }

    // db.books.aggregate([{$match:{type:"technology"}}])
    @Test
    void match1() {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("type").is("technology"));
        Aggregation aggregation = Aggregation.newAggregation(matchOperation);
        System.out.println(aggregation);
        List<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class).getMappedResults();
        for (Document document : results) {
            System.out.println(document.toJson());
        }
    }

    /*
    db.books.aggregate([
        {$match:{type:"technology"}},
        {$project:{name:"$title",_id:0,type:1,author:{name:1}}}
    ])
    */
    @Test
    void match2() {
        MatchOperation matchOperation = Aggregation.match(Criteria.where("type").is("technology"));
        ProjectionOperation projectOperation = Aggregation.project("title").andExclude("_id").andInclude("type").and("author.name").as("author.name");
        Aggregation aggregation = Aggregation.newAggregation(matchOperation, projectOperation);
        System.out.println(aggregation);
        List<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class).getMappedResults();
        for (Document document : results) {
            System.out.println(document.toJson());
        }
    }

    /*
    db.books.aggregate([
        {$match:{type:"technology"}},
        {$count: "type_count"}
    ])
     */
    @Test
    void count() {
        // MatchOperation to filter documents by type: "technology"
        MatchOperation matchAggregation = Aggregation.match(Criteria.where("type").is("technology"));
        // CountOperation to count the matched documents
        CountOperation countAggregation = Aggregation.count().as("type_count");
        // Combine the match and count operations
        Aggregation aggregation = Aggregation.newAggregation(matchAggregation, countAggregation);
        System.out.println(aggregation);
        List<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class).getMappedResults();
        for (Document document : results) {
            System.out.println(document.toJson());
        }
    }

    /*
    db.books.aggregate([
        {$group:{_id:null,count:{$sum:1},pop:{$sum:"$favCount"},avg:{$avg:"$favCount"}}}
    ])
     */
    @Test
    void aggregateBooksData() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group().count().as("count").sum("favCount").as("pop").avg("favCount").as("avg")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class);
        List<Document> mappedResults = results.getMappedResults();

        // Assuming that there will be only one result due to grouping by null (_id: null)
        if (!mappedResults.isEmpty()) {
            System.out.println(mappedResults.get(0).toJson());
        } else {
            System.out.println("{}");
        }
    }

    /*
    db.books.aggregate([
        {$group:{_id:"$author.name",pop:{$sum:"$favCount"}}}
    ])
     */
    @Test
    void aggregateAuthorsData() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("author.name").sum("favCount").as("pop")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class);
        List<Document> mappedResults = results.getMappedResults();

        System.out.println(mappedResults);
    }

    /*
    db.books.aggregate([
        {$group:{_id:{name:"$author.name",title:"$title"},pop:{$sum:"$favCount"}}}
    ])
    */
    @Test
    void aggregateAuthorsAndTitlesData() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("author.name", "title").sum("favCount").as("pop")
        );

        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class);
        List<Document> mappedResults = results.getMappedResults();

        System.out.println(mappedResults);
    }

    /*
    db.books.aggregate([
        {$group:{_id:"$author.name",types:{$addToSet:"$type"}}}
    ])
    */
    @Test
    void aggregateAuthorsAndTypesData() {
        GroupOperation groupOperation = Aggregation.group("author.name").addToSet("type").as("types");
        Aggregation aggregation = Aggregation.newAggregation(groupOperation);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class);
        List<Document> mappedResults = results.getMappedResults();
        System.out.println(mappedResults);
    }

    /*
    db.books.aggregate([
        {$unwind:"$tag"},
        {$group:{_id:"$author.name",types:{$addToSet:"$tag"}}}
    ])
    */
    @Test
    void aggregateAuthorsAndTagsData() {
        UnwindOperation unwindOperation = Aggregation.unwind("tag");
        GroupOperation groupOperation = Aggregation.group("author.name").addToSet("tag").as("types");
        Aggregation aggregation = Aggregation.newAggregation(unwindOperation, groupOperation);
        AggregationResults<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class);
        List<Document> mappedResults = results.getMappedResults();
        System.out.println(mappedResults);
    }

    /*
    db.books.aggregate([
        {$match:{"author.name":"xx006"}},
        {$unwind:"$tag"}
    ])
     */

    @Test
    void aggregateAuthorAndTagsData() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("author.name").is("xx006")),
                Aggregation.unwind("tag")
        );
        List<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class).getMappedResults();
        System.out.println(results);
    }

    /*
    db.books.aggregate([
        {$match:{"author.name":"xx006"}},
        {$unwind:{path:"$tag", includeArrayIndex: "arrayIndex"}}
    ])
    */
    @Test
    void aggregateAuthorAndTagsDataWithArrayIndex() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("author.name").is("xx006")),
                Aggregation.unwind("tag", "arrayIndex")
        );
        List<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class).getMappedResults();
        System.out.println(results);
    }

    /*
db.books.aggregate([
    {$match:{"author.name":"xx006"}},
    {$unwind:{path:"$tag", preserveNullAndEmptyArrays: true}}
])
*/
    @Test
    void aggregateAuthorAndTagsDataWithPreserveNullAndEmptyArrays() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("author.name").is("xx006")),
                Aggregation.unwind("tag", true)
        );
        List<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class).getMappedResults();
    }

    /*
    db.books.aggregate([
        {$match:{"author.name":"xx006"}},
        {$unwind:{path:"$tag", preserveNullAndEmptyArrays: true}},
        {$sort:{"favCount":-1}},
        {$skip : 2},
        {$limit : 5 }

    ])
    */
    @Test
    void aggregateAuthorAndTagsDataWithSortAndLimitAndSkip() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("author.name").is("xx006")),
                Aggregation.unwind("tag", true),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "favCount")),
                Aggregation.skip(2),
                Aggregation.limit(5)
        );
        List<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class).getMappedResults();
        for (Document document : results){
            System.out.println(document.toJson());
        }
    }

    /*
db.books.aggregate([
    {$group:{_id:"$type",total:{$sum:1}}},
    {$sort:{total:-1}}
])
*/
    @Test
    void aggregateTypeAndTotal() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.group("type").count().as("total"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "total"))
        );
        List<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class).getMappedResults();
        for (Document document : results){
            System.out.println(document.toJson());
        }
    }

    /*
db.books.aggregate([
    {$match:{favCount:{$gt:0}}},
    {$unwind:"$tag"},
    {$group:{_id:"$tag",total:{$sum:"$favCount"}}},
    {$sort:{total:-1}}
])
*/
    @Test
    void aggregateTagsAndTotal() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("favCount").gt(0)),
                Aggregation.unwind("tag"),
                Aggregation.group("tag").sum("favCount").as("total"),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "total"))
        );
        List<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class).getMappedResults();
        for (Document document : results){
            System.out.println(document.toJson());
        }
    }

    /*
db.books.aggregate([{
    $bucket:{
        groupBy:"$favCount",
        boundaries:[0,10,60,80,100],
        default:"other",
        output:{"count":{$sum:1}}
    }
}])
*/
    @Test
    void aggregateFavCountAndCount() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.bucket("$favCount")
                        .withBoundaries(0,10,60,80,100)
                        .withDefaultBucket("other")
                        .andOutput(context -> new Document("$sum", 1))
                        .as("count")
        );
        List<Document> results = mongoTemplate.aggregate(aggregation, "books", Document.class).getMappedResults();
        for (Document document : results){
            System.out.println(document.toJson());
        }
    }
/*
db.customer.aggregate([
    {$lookup: {
       from: "order",
       localField: "customerCode",
       foreignField: "customerCode",
       as: "customerOrder"
     }
    }
])
*/
    @Test
    void aggregateCustomerAndOrder() {
        /**
         * 执行MongoDB的聚合操作，并打印结果。
         *
         * 该方法首先创建一个聚合操作，使用lookup函数来实现从"order"集合到"customer"集合的连接，
         * 通过"customerCode"字段进行匹配，将结果映射到"customerOrder"字段。
         * 然后，该方法执行这个聚合操作，对"customer"集合进行聚合，并将结果转换为Document对象的列表。
         * 最后，遍历这个结果列表，将每个Document对象转换为JSON字符串并打印出来。
         *
         * @param mongoTemplate MongoDB模板，用于执行聚合操作。
         */
            // 创建聚合操作，使用lookup进行集合连接
            Aggregation aggregation = Aggregation.newAggregation(
                    Aggregation.lookup("order", "customerCode", "customerCode", "customerOrder")
            );

            // 执行聚合操作并获取结果
            List<Document> results = mongoTemplate.aggregate(aggregation, "customer", Document.class).getMappedResults();

            // 遍历并打印结果
            for (Document document : results){
                System.out.println(document.toJson());
            }
    }

/*
db.order.aggregate([
    {$lookup: {
               from: "customer",
               localField: "customerCode",
               foreignField: "customerCode",
               as: "curstomer"
             }

    },
    {$lookup: {
               from: "orderItem",
               localField: "orderId",
               foreignField: "orderId",
               as: "orderItem"
             }
    }
])
*/
    @Test
    void aggregateOrderAndCustomerAndOrderItem() {
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("customer", "customerCode", "customerCode", "curstomer"),
                Aggregation.lookup("orderItem", "orderId", "orderId", "orderItem")
        );
        List<Document> results = mongoTemplate.aggregate(aggregation, "order", Document.class).getMappedResults();
        for (Document document : results){
            System.out.println(document.toJson());
        }
    }



















}
