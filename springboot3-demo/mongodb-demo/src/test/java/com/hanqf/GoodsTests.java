package com.hanqf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.hanqf.mongo.model.Goods;
import com.hanqf.mongo.model.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <h1></h1>
 * Created by hanqf on 2024/3/4 18:30.
 */

@SpringBootTest
class GoodsTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    private final JsonMapper jsonMapper = new JsonMapper();

    /*
    db.goods.insertMany([{
        name:"羽绒服",
        tags:[
            {tagKey:"size",tagValue:["M","L","XL","XXL","XXXL"]},
            {tagKey:"color",tagValue:["黑色","宝蓝"]},
            {tagKey:"style",tagValue:"韩风"}
        ]
    },{
        name:"羊毛衫",
        tags:[
            {tagKey:"size",tagValue:["L","XL","XXL"]},
            {tagKey:"color",tagValue:["蓝色","杏色"]},
            {tagKey:"style",tagValue:"韩风"}
        ]
    }])
     */
    @Test
    void initGoodsTest() {
        List<Goods> goods = Arrays.asList(
                new Goods("羽绒服", Arrays.asList(
                        Tag.builder().tagKey("size").tagValue(Arrays.asList("M", "L", "XL", "XXL", "XXXL")).build(),
                        Tag.builder().tagKey("color").tagValue(Arrays.asList("黑色", "宝蓝")).build(),
                        Tag.builder().tagKey("style").tagValue("韩风").build()
                )),
                new Goods("羊毛衫", Arrays.asList(
                        Tag.builder().tagKey("size").tagValue(Arrays.asList("M", "L", "XXL")).build(),
                        Tag.builder().tagKey("color").tagValue(Arrays.asList("蓝色", "杏色")).build(),
                        Tag.builder().tagKey("style").tagValue("韩风").build()
                ))
        );

        mongoTemplate.insert(goods, "goods");
    }


    /*
    db.goods.insertMany([{
        name:"羽绒服",
        tags:[
            {tagKey:"size",tagValue:["M","L","XL","XXL","XXXL"]},
            {tagKey:"color",tagValue:["黑色","宝蓝"]},
            {tagKey:"style",tagValue:"韩风"}
        ]
    },{
        name:"羊毛衫",
        tags:[
            {tagKey:"size",tagValue:["L","XL","XXL"]},
            {tagKey:"color",tagValue:["蓝色","杏色"]},
            {tagKey:"style",tagValue:"韩风"}
        ]
    }])
     */
    @Test
    void initGoodsTest2() {
        List<Map<String, Object>> goods = Arrays.asList(
                createGood("羽绒服", Arrays.asList(
                        createTag("size", Arrays.asList("M", "L", "XL", "XXL", "XXXL")),
                        createTag("color", Arrays.asList("黑色", "宝蓝")),
                        createTag("style", "韩风")
                )),
                createGood("羊毛衫", Arrays.asList(
                        createTag("size", Arrays.asList("L", "XL", "XXL")),
                        createTag("color", Arrays.asList("蓝色", "杏色")),
                        createTag("style", "韩风")
                ))
        );

        mongoTemplate.insert(goods, "goods");
    }




    private Map<String, Object> createGood(String name, List<Map<String, Object>> tags) {
        Map<String, Object> good = new HashMap<>();
        good.put("name", name);
        good.put("tags", tags);
        return good;
    }

    private Map<String, Object> createTag(String tagKey, Object tagValue) {
        Map<String, Object> tag = new HashMap<>();
        tag.put("tagKey", tagKey);
        tag.put("tagValue", tagValue);
        return tag;
    }

    @Test
    void findGoodsTest() {
        /*
        #筛选出color=黑色的商品信息
        db.goods.find({
            tags:{
                $elemMatch:{tagKey:"color",tagValue:"黑色"}
            }
        })
         */
        Query query = Query.query(Criteria.where("tags").elemMatch(
                Criteria.where("tagKey").is("color").and("tagValue").is("黑色")
        ));

        mongoTemplate.find(query, Goods.class, "goods").forEach(System.out::println);

        System.out.println("#############################################");

        /*
        # 筛选出color=蓝色，并且size=XL的商品信息
        db.goods.find({
            tags:{
                $all:[
                    {$elemMatch:{tagKey:"color",tagValue:"黑色"}},
                    {$elemMatch:{tagKey:"size",tagValue:"XL"}}
                ]
            }
        })
         */

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("tags").elemMatch(
                        Criteria.where("tagKey").is("color").and("tagValue").is("黑色")
                ),
                Criteria.where("tags").elemMatch(
                        Criteria.where("tagKey").is("size").and("tagValue").is("XL")
                )
        );

        query = Query.query(criteria);
        mongoTemplate.find(query, Goods.class, "goods").forEach(System.out::println);

        System.out.println("#############################################");

        String json = """
        {
            tags:{
                $all:[
                    {$elemMatch:{tagKey:"color",tagValue:"黑色"}},
                    {$elemMatch:{tagKey:"size",tagValue:"XL"}}
                ]
            }
        }
        """;
        query = new BasicQuery(json);
        mongoTemplate.find(query, Goods.class, "goods").forEach(System.out::println);

    }

    @Test
    void findGoodsTest2() {
        /*
        #筛选出color=黑色的商品信息
        db.goods.find({
            tags:{
                $elemMatch:{tagKey:"color",tagValue:"黑色"}
            }
        })
         */
        Query query = Query.query(Criteria.where("tags").elemMatch(
                Criteria.where("tagKey").is("color").and("tagValue").is("黑色")
        ));

        mongoTemplate.find(query, Map.class, "goods").forEach(map -> {
            try {
                System.out.println(jsonMapper.writeValueAsString(map));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("#############################################");

        /*
        # 筛选出color=蓝色，并且size=XL的商品信息
        db.goods.find({
            tags:{
                $all:[
                    {$elemMatch:{tagKey:"color",tagValue:"黑色"}},
                    {$elemMatch:{tagKey:"size",tagValue:"XL"}}
                ]
            }
        })
         */

        Criteria criteria = new Criteria().andOperator(
                Criteria.where("tags").elemMatch(
                        Criteria.where("tagKey").is("color").and("tagValue").is("黑色")
                ),
                Criteria.where("tags").elemMatch(
                        Criteria.where("tagKey").is("size").and("tagValue").is("XL")
                )
        );

        query = Query.query(criteria);
        mongoTemplate.find(query, Map.class, "goods").forEach(map -> {
            try {
                System.out.println(jsonMapper.writeValueAsString(map));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

        System.out.println("#############################################");

        String json = """
        {
            tags:{
                $all:[
                    {$elemMatch:{tagKey:"color",tagValue:"黑色"}},
                    {$elemMatch:{tagKey:"size",tagValue:"XL"}}
                ]
            }
        }
        """;
        query = new BasicQuery(json);
        mongoTemplate.find(query, Map.class, "goods").forEach(map -> {
            try {
                System.out.println(jsonMapper.writeValueAsString(map));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        });

    }
}
