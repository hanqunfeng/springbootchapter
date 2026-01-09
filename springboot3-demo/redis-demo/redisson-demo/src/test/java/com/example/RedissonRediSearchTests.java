package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RSearch;
import org.redisson.api.RedissonClient;
import org.redisson.api.SortOrder;
import org.redisson.api.search.SpellcheckOptions;
import org.redisson.api.search.aggregate.*;
import org.redisson.api.search.index.FieldIndex;
import org.redisson.api.search.index.IndexInfo;
import org.redisson.api.search.index.IndexOptions;
import org.redisson.api.search.index.IndexType;
import org.redisson.api.search.query.Document;
import org.redisson.api.search.query.QueryOptions;
import org.redisson.api.search.query.ReturnAttribute;
import org.redisson.api.search.query.SearchResult;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.*;

/**
 *
 * Created by hanqf on 2025/12/25 14:59.
 */

@SpringBootTest
public class RedissonRediSearchTests {

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    RSearch rSearch = null;

    @BeforeEach
    void init() {
        rSearch = redissonClient.getSearch(StringCodec.INSTANCE);
    }

    /**
     * FT._LIST
     */
    @Test
    void getIndex() {
        final List<String> indexes = rSearch.getIndexes();
        indexes.forEach(System.out::println);
    }

    @Test
    void dropIndex() {
        rSearch.dropIndex("idx:user");
//        rSearch.dropIndexAndDocuments("idx:user");
    }

    /**
     * FT.CREATE idx:user
     * ON JSON
     * PREFIX 1 user:
     * LANGUAGE chinese
     * SCHEMA
     * $.name AS name TEXT
     * $.age AS age NUMERIC SORTABLE
     * $.orders[*].amount AS amount NUMERIC
     * $.orders[*].status AS status TAG
     * $.comment AS comment TEXT
     * $.items[*] AS items TEXT
     */
    @Test
    void createIndex() {
        rSearch.createIndex(
                "idx:user",
                IndexOptions.defaults()
                        .on(IndexType.JSON)
                        .prefix(List.of("user:"))
                        .language("chinese"),
                FieldIndex.text("$.name").as("name"),
                FieldIndex.numeric("$.age").as("age"),
                FieldIndex.numeric("$.orders[*].amount").as("amount"),
                FieldIndex.tag("$.orders[*].status").as("status"),
                FieldIndex.text("$.comment").as("comment"),
                FieldIndex.text("$.items[*]").as("items"));
    }

    /**
     * FT.ALTER idx:user
     * SCHEMA
     * ADD
     * $.vip AS vip TAG
     */
    @Test
    void alterIndex() {
        rSearch.alter(
                "idx:user",
                false,
                FieldIndex.tag("$.vip").as("vip")
        );
    }

    @Test
    void info() {
        IndexInfo info = rSearch.info("idx:user");
        System.out.println(info);
    }

    /**
     * FT.SEARCH idx:user
     * '@status:{PAID} @amount:[50 +inf]'
     * RETURN 3 name status amount
     * SORTBY amount DESC
     * LIMIT 0 10
     */
    @Test
    void search1() {
        SearchResult result = rSearch.search(
                "idx:user",
                "@status:{PAID} @amount:[50 +inf]",
                QueryOptions.defaults()
                        .returnAttributes(new ReturnAttribute("name"), new ReturnAttribute("status"), new ReturnAttribute("amount"))
                        .sortBy("amount")
                        .sortOrder(SortOrder.DESC)
                        .limit(0, 10));

        long total = result.getTotal();
        System.out.println("total = " + total);
        List<Document> docs = result.getDocuments();

        for (Document doc : docs) {
            String id = doc.getId();
            Map<String, Object> attrs = doc.getAttributes();
            System.out.println("id = " + id + " attrs = " + attrs);
        }
    }

    /**
     * FT.SEARCH idx:user 'phone'
     */
    @Test
    void search2() {
        SearchResult result = rSearch.search(
                "idx:user",
                "phone",
                QueryOptions.defaults()
        );

        long total = result.getTotal();
        System.out.println("total = " + total);
        List<Document> docs = result.getDocuments();

        for (Document doc : docs) {
            String id = doc.getId();
            Map<String, Object> attrs = doc.getAttributes();
            System.out.println("id = " + id + " attrs = " + attrs);
        }
    }


    /**
     * FT.AGGREGATE idx:user
     * '*'
     * GROUPBY 1 @status
     * REDUCE COUNT 0 AS cnt
     */
    @Test
    void aggregate1() {
        AggregationResult result = rSearch.aggregate(
                "idx:user",
                "*",
                AggregationOptions.defaults()
                        .groupBy(GroupBy.fieldNames("@status").reducers(Reducer.count().as("cnt")))
        );
        final long total = result.getTotal();
        System.out.println("total = " + total);
        final List<Map<String, Object>> attributes = result.getAttributes();
        for (Map<String, Object> attribute : attributes) {
            System.out.println("attribute = " + attribute);
        }
    }


    /**
     * FT.AGGREGATE idx:user '*'
     * GROUPBY 1 @status
     * REDUCE SUM 1 @amount AS total_amount
     * SORTBY 2 @total_amount DESC
     * LIMIT 0 3
     */
    @Test
    void aggregate2() {
        AggregationResult result = rSearch.aggregate(
                "idx:user",
                "*",
                AggregationOptions.defaults()
                        .groupBy(GroupBy.fieldNames("@status")
                                .reducers(
                                        Reducer.sum("@amount").as("total_amount")
                                )
                        )
                        .sortBy(new SortedField("@total_amount", SortOrder.DESC))
                        .limit(0, 3)
        );
        final long total = result.getTotal();
        System.out.println("total = " + total);
        final List<Map<String, Object>> attributes = result.getAttributes();
        for (Map<String, Object> attribute : attributes) {
            System.out.println("attribute = " + attribute);
        }
    }


    /**
     * FT.AGGREGATE idx:user '*'
     * GROUPBY 1 @status
     * REDUCE COUNT 0 AS cnt
     * REDUCE SUM 1 @amount AS total_amount
     * SORTBY 4 @total_amount DESC @cnt ASC
     */
    @Test
    void aggregate3() {
        AggregationResult result = rSearch.aggregate(
                "idx:user",
                "*",
                AggregationOptions.defaults()
                        .groupBy(GroupBy.fieldNames("@status")
                                .reducers(
                                        Reducer.count().as("cnt"),
                                        Reducer.sum("@amount").as("total_amount")
                                )
                        )
                        .sortBy(new SortedField("@total_amount", SortOrder.DESC))
                        .sortBy(new SortedField("@cnt", SortOrder.ASC))
        );
        final long total = result.getTotal();
        System.out.println("total = " + total);
        final List<Map<String, Object>> attributes = result.getAttributes();
        for (Map<String, Object> attribute : attributes) {
            System.out.println("attribute = " + attribute);
        }
    }

    /**
     * FT.AGGREGATE idx:user '*'
     * GROUPBY 1 @status
     * REDUCE SUM 1 @amount AS total_amount
     * FILTER @total_amount > 1000
     * SORTBY 2 @total_amount DESC
     */
    @Test
    void aggregate4() {
        AggregationResult result = rSearch.aggregate(
                "idx:user",
                "*",
                AggregationOptions.defaults()
                        .groupBy(GroupBy.fieldNames("@status")
                                .reducers(
                                        Reducer.sum("@amount").as("total_amount")
                                )
                        )
                        .filter("@total_amount > 200")
                        .sortBy(new SortedField("@total_amount", SortOrder.DESC))
        );
        final long total = result.getTotal();
        System.out.println("total = " + total);
        final List<Map<String, Object>> attributes = result.getAttributes();
        for (Map<String, Object> attribute : attributes) {
            System.out.println("attribute = " + attribute);
        }

    }


    /**
     * FT.ALIASADD alias:user idx:user
     */
    @Test
    void addAlias() {
        rSearch.addAlias("alias:user", "idx:user");
    }


    /**
     * FT.ALIASDEL alias:user
     */
    @Test
    void delAlias() {
        rSearch.delAlias("alias:user");
    }

    /**
     * FT.ALIASUPDATE alias:user idx:user:v2
     */
    @Test
    void updateAlias() {
        rSearch.updateAlias("alias:user", "idx:user:v2");
    }

    @Test
    void cursor() {
        AggregationResult result = rSearch.aggregate(
                "idx:user",
                "*",
                AggregationOptions.defaults()
                        .groupBy(GroupBy.fieldNames("@status")
                                .reducers(
                                        Reducer.sum("@amount").as("total_amount")
                                )
                        )
                        .withCursor(2, 60000)
        );
        long total;
        long cursorId;
        List<Map<String, Object>> attributes;

        total = result.getTotal();
        cursorId = result.getCursorId();
        System.out.println("total = " + total);
        System.out.println("cursorId = " + cursorId);
        attributes = result.getAttributes();
        for (Map<String, Object> attribute : attributes) {
            System.out.println("attribute = " + attribute);
        }

        while (cursorId != 0) {
            System.out.println("===============================================");
            // 游标读取，Redisson的readCursor方法有bug，无法继续读取游标数据
            // result = rSearch.readCursor("idx:user", cursorId, 2);

            // 这里游标读取可以自己编写一个基于Lua 的实现
            result = readCursor("idx:user", cursorId, 2);

            total = result.getTotal();
            cursorId = result.getCursorId();
            System.out.println("total = " + total);
            System.out.println("cursorId = " + cursorId);
            attributes = result.getAttributes();
            for (Map<String, Object> attribute : attributes) {
                System.out.println("attribute = " + attribute);
            }
        }

        // cursorId=0 或 到期 时会自动删除游标，此处无需删除
        // rSearch.delCursor("idx:user", cursorId);
    }

    public AggregationResult readCursor(String indexName, long cursorId, int count) {
        String script = "return redis.call('FT.CURSOR', 'READ', KEYS[1], ARGV[1], 'COUNT', ARGV[2])";

        List<Object> results = stringRedisTemplate.execute(
                new DefaultRedisScript<>(script, List.class),
                Collections.singletonList(indexName),
                String.valueOf(cursorId),
                String.valueOf(count)
        );

        cursorId = (long) results.get(1);
        List<Object> lo = (List) results.get(0);
        return new AggregationResult(
                (long) lo.get(0),
                toListOfMap(lo),
                cursorId
        );
    }


    public static List<Map<String, Object>> toListOfMap(List<Object> lo) {
        List<Map<String, Object>> result = new ArrayList<>();

        if (lo == null || lo.size() <= 1) {
            return result;
        }

        // 从索引 1 开始，跳过聚合总数
        for (int i = 1; i < lo.size(); i++) {
            Object rowObj = lo.get(i);

            if (!(rowObj instanceof List<?> row)) {
                continue;
            }

            Map<String, Object> map = new LinkedHashMap<>();

            for (int j = 0; j < row.size() - 1; j += 2) {
                String key = String.valueOf(row.get(j));
                Object value = row.get(j + 1);
                map.put(key, value);
            }

            result.add(map);
        }

        return result;
    }

    /**
     * # 添加商品相关词
     * FT.DICTADD product_dict iphone mobile phone android huawei
     * # 查看词典内容（FT.DICTDUMP）
     * FT.DICTDUMP product_dict
     * # 删除词典中的词（FT.DICTDEL）
     * FT.DICTDEL product_dict iphone
     */
    @Test
    void dict() {
        rSearch.addDict("product_dict", "iphone", "mobile", "phone", "android", "huawei");
        List<String> productDict = rSearch.dumpDict("product_dict");
        rSearch.delDict("product_dict", "iphone");
    }

    /**
     * FT.SYNUPDATE idx:user
     *   phone_group
     *   iphone mobile phone
     */
    @Test
    void synonyms() {
        rSearch.updateSynonyms("idx:user", "phone_group", "iphone", "mobile", "phone");
        Map<String, List<String>> synonyms = rSearch.dumpSynonyms("idx:user");
        System.out.println("synonyms = " + synonyms);
    }



    @Test
    void spellcheck(){
        Map<String, Map<String, Double>> spellcheck = rSearch.spellcheck(
                "idx:user",
                "iphnoe linu",
                SpellcheckOptions.defaults()
                        .distance(2)
        );
        System.out.println("spellcheck = " + spellcheck);
    }

    @Test
    void spellcheck2(){
        Map<String, Map<String, Double>> spellcheck = rSearch.spellcheck(
                "idx:user",
                "macboo",
                SpellcheckOptions.defaults()
                        .distance(2)
                        .includedTerms("dict:tech")
        );
        System.out.println("spellcheck = " + spellcheck);
    }

}
