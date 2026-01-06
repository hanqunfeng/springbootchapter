package com.example;

import com.example.demo.pojo.User;
import com.example.demo.pojo.User.OrdersBean;
import com.example.demo.pojo.User.ProfileBean;
import com.example.demo.pojo.User.StatsBean;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RJsonBucket;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JacksonCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 *
 * Created by hanqf on 2025/12/25 14:59.
 */

@SpringBootTest
public class RedissonJSONTests {

    @Autowired
    private RedissonClient redissonClient;

    private static ObjectMapper objectMapper = new ObjectMapper();
    private static String userStr = """
            {
              "profile": {
                "name": "Alice",
                "age": 28,
                "vip": true
              },
              "tags": ["vip", "electronics", "promotion"],
              "addresses": [
                { "city": "Beijing", "zip": "100000" },
                { "city": "Shanghai", "zip": "200000" }
              ],
              "orders": [
                {
                  "orderId": "O1001",
                  "amount": 199.99,
                  "status": "PAID",
                  "items": [
                    { "sku": "SKU-1", "price": 99.99, "qty": 1 },
                    { "sku": "SKU-2", "price": 100.00, "qty": 1 }
                  ]
                },
                {
                  "orderId": "O1002",
                  "amount": 59.90,
                  "status": "CREATED",
                  "items": [
                    { "sku": "SKU-3", "price": 59.90, "qty": 1 }
                  ]
                }
              ],
              "stats": {
                "loginCount": 10,
                "balance": 300.5
              }
            }
            """;

    User user = null;
    RJsonBucket<User> bucket = null;

    @BeforeEach
    void init() throws JsonProcessingException {
        bucket = redissonClient.getJsonBucket("user:10002", new JacksonCodec<>(User.class));
    }

    /**
     * 创建JSON对象
     * <p>
     * 创建完整 JSON 文档（JSON.SET）
     **/
    @Test
    void redisJSONCreateNew() throws JsonProcessingException {
        user = objectMapper.readValue(userStr, User.class);
        bucket.set(user);
    }

    /**
     * 1️⃣ 基础路径访问
     **/
    @Test
    void getBashPath() {
        final User user = bucket.get(new JacksonCodec<>(new TypeReference<User>() {
        }));
        System.out.println(user);

        User.ProfileBean profile = bucket.get(new JacksonCodec<>(new TypeReference<User.ProfileBean>() {
        }), "profile");
        System.out.println(profile);

        String name = bucket.get(new JacksonCodec<>(new TypeReference<String>() {
        }), "profile.name");
        System.out.println(name);

        List<String> names = bucket.get(new JacksonCodec<>(new TypeReference<List<String>>() {
        }), "$.profile.name");
        System.out.println(names.get(0));
    }

    /**
     * 2️⃣ 数组访问 & 通配符
     **/
    @Test
    void getArrayPath() {
        List<String> orderIds = bucket.get(new JacksonCodec<>(new TypeReference<List<String>>() {
        }), "$.orders[*].orderId");
        System.out.println(orderIds);

        List<String> citys = bucket.get(new JacksonCodec<>(new TypeReference<List<String>>() {
        }), "$.addresses[*].city");
        System.out.println(citys);
    }

    /**
     * 3️⃣ 数组下标与切片
     **/
    @Test
    void getArraySlice() {
        List<OrdersBean> orders = bucket.get(new JacksonCodec<>(new TypeReference<List<OrdersBean>>() {
        }), "$.orders[0]");
        System.out.println(orders);

        orders = bucket.get(new JacksonCodec<>(new TypeReference<List<OrdersBean>>() {
        }), "$.orders[0:2]");
        System.out.println(orders);

        orders = bucket.get(new JacksonCodec<>(new TypeReference<List<OrdersBean>>() {
        }), "$.orders[:]");
        System.out.println(orders);
    }

    /**
     * 4️⃣ 递归查询（..）
     **/
    @Test
    void getRecursionQuery() {
        List<Double> prices = bucket.get(new JacksonCodec<>(new TypeReference<List<Double>>() {
        }), "$..price");
        System.out.println(prices);

    }

    /**
     * 5️⃣ 条件过滤（JSONPath 核心能力）
     **/
    @Test
    void getConditionalFiltering() {
        List<OrdersBean> orders = bucket.get(new JacksonCodec<>(new TypeReference<List<OrdersBean>>() {
        }), "$.orders[?(@.amount>100)]");
        System.out.println(orders);

        orders = bucket.get(new JacksonCodec<>(new TypeReference<List<OrdersBean>>() {
        }), "$.orders[?(@.items[0].price>50)]");
        System.out.println(orders);

        orders = bucket.get(new JacksonCodec<>(new TypeReference<List<OrdersBean>>() {
        }), "$.orders[?(@.amount>100)&&(@.items[0].price>50)]");
        System.out.println(orders);
    }

    /**
     * 6️⃣ 修改数据（JSON.SET / JSON.NUMINCRBY）
     **/
    @Test
    void set() {
//        User user = bucket.get(new JacksonCodec<>(new TypeReference<User>() {
//        }));
//        user.getStats().setLoginCount(user.getStats().getLoginCount() + 1);
//        user.getProfile().setName("Alice  Zhang");
//        bucket.set(user);
        bucket.set("$.profile.name", "Alice Zhang");
        bucket.incrementAndGet("$.stats.loginCount", 1);
    }

    /**
     * 7️⃣ 数组操作（ARRAPPEND / ARRINSERT）
     **/
    @Test
    void setArray() {
        OrdersBean ordersBean = OrdersBean.builder()
                .orderId("O1003")
                .amount(399)
                .status("CREATED")
                .items(List.of(
                        OrdersBean.ItemsBean.builder()
                                .sku("SKU-9")
                                .qty(1)
                                .price(399).build()
                )).build();
//        final long num = bucket.arrayAppend("$.orders", ordersBean);

//        long num = bucket.arrayAppend("$.tags", "newTag");


        long num = bucket.arrayInsert("$.tags", 1, "newTag2");
        System.out.println(num);
    }

    /**
     * 8️⃣ 删除 & 清理操作（JSON.DEL & JSON.CLEAR）
     **/
    @Test
    void del() {
        final long num = bucket.delete("$.profile.age");
        System.out.println(num);
        final long clear = bucket.clear("$.tags1");
        System.out.println(clear);

    }

    /**
     * 9️⃣ 统计数组长度
     **/
    @Test
    void arrLength() {
//        long length = bucket.arraySize("$.orders");
//        System.out.println(length);
        final long sizeInMemory = bucket.sizeInMemory();
        System.out.println(sizeInMemory);

    }

    @Test
    void redisJSONCreate() {
        RJsonBucket<User> bucket = redissonClient.getJsonBucket("user:10001", new JacksonCodec<>(User.class));
        User user = User.builder()
                .profile(ProfileBean.builder()
                        .name("Alice")
                        .age(28)
                        .vip(true)
                        .build())
                .stats(StatsBean.builder()
                        .loginCount(10)
                        .balance(300.5)
                        .build())
                .tags(List.of("tag1", "tag2", "tag3"))
                .addresses(List.of(
                        User.AddressesBean.builder()
                                .city("Beijing")
                                .zip("100000")
                                .build(),
                        User.AddressesBean.builder()
                                .city("Shanghai")
                                .zip("200000")
                                .build()
                ))
                .orders(List.of(
                        OrdersBean.builder()
                                .orderId("O1001")
                                .amount(199.99)
                                .status("PAID")
                                .items(List.of(
                                                OrdersBean.ItemsBean.builder()
                                                        .sku("SKU-1")
                                                        .price(99.99)
                                                        .qty(1)
                                                        .build(),
                                                OrdersBean.ItemsBean.builder()
                                                        .sku("SKU-2")
                                                        .price(99.99)
                                                        .qty(1)
                                                        .build()
                                        )
                                ).build(),
                        OrdersBean.builder()
                                .orderId("O1002")
                                .amount(59.99)
                                .status("PAID")
                                .items(List.of(
                                                OrdersBean.ItemsBean.builder()
                                                        .sku("SKU-3")
                                                        .price(59.99)
                                                        .qty(1)
                                                        .build()
                                        )
                                ).build()
                ))
                .build();


        bucket.set(user);


        User obj = bucket.get();
        System.out.println(obj);
    }

    @Test
    void redisJSONGetTags() {
        RJsonBucket<User> bucket = redissonClient.getJsonBucket("userJson", new JacksonCodec<>(User.class));

        List<String> values = bucket.get(new JacksonCodec<>(new TypeReference<List<String>>() {
        }), "tags");
        System.out.println(values);
    }

    @Test
    void redisJSONAppendTags() {
        RJsonBucket<User> bucket = redissonClient.getJsonBucket("userJson", new JacksonCodec<>(User.class));

        long aa = bucket.arrayAppend("$.tags", "t3", "t4");
        System.out.println(aa);

        List<String> values = bucket.get(new JacksonCodec<>(new TypeReference<List<String>>() {
        }), "tags");
        System.out.println(values);
    }
}
