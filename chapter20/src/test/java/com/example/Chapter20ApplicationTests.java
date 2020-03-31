package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class Chapter20ApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    //Lua脚本
    @Test
    void testLua1(){
        DefaultRedisScript<String> defaultRedisScript = new DefaultRedisScript<>();
        //设置脚本
        defaultRedisScript.setScriptText("return 'hello redis'");
        //设置返回类型
        defaultRedisScript.setResultType(String.class);
        //执行脚本
        String result = (String) redisTemplate.execute(defaultRedisScript, redisTemplate.getStringSerializer(), redisTemplate.getStringSerializer(), null);

        System.out.println("result=="+result);
    }

    //Lua是原子操作，这里比较两个key的value，如果相等则返回1，不相等返回0
    @Test
    void testLua2(){
        String script = "redis.call('set', KEYS[1] , ARGV[1]) \n" +
                "redis.call('set', KEYS[2], ARGV[2] ) \n" +
                "local str1 = redis.call('get' , KEYS[1]) \n" +
                "local str2 = redis.call('get', KEYS[2]) \n" +
                "if str1 == str2 then \n" +
                "return 1 \n" +
                "end \n" +
                "return 0";

        System.out.println(script);

        DefaultRedisScript<Long> defaultRedisScript = new DefaultRedisScript<>();
        //设置脚本
        defaultRedisScript.setScriptText(script);
        //设置返回类型
        defaultRedisScript.setResultType(Long.class);

        List<String> keyList = new ArrayList<>();
        keyList.add("keylua1");
        keyList.add("keylua2");
        String value1 = "lua";
        String value2 = "lua";
        //第一个序列化器是key的，第二个是参数的即value
        Long result = (Long) redisTemplate.execute(defaultRedisScript, redisTemplate.getStringSerializer(), redisTemplate.getStringSerializer(), keyList, value1, value2);
        System.out.println("result=="+result);
    }


    //流水线执行
    @Test
    void testPipeLine(){

        for(int i=0;i<100000;i++) {
            //此时命令只是进入队列，不会真正执行，待到最后一次性一起执行，这里要注意数据量，一次执行量过大会导致内存益处
            redisTemplate.delete("pipeline_" + i);
        }

        //十万次写入大约400~500ms
        Long start = System.currentTimeMillis();
        List list = (List)redisTemplate.executePipelined(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                for(int i=0;i<100000;i++) {
                    //此时命令只是进入队列，不会真正执行，待到最后一次性一起执行，这里要注意数据量，一次执行量过大会导致内存益处
                    redisOperations.opsForValue().set("pipeline_" + i, "value_" + i);
                }
                return null;

            }
        });
        Long end = System.currentTimeMillis();
        System.out.println("耗时：" + (end - start) + "ms");
        //list.stream().forEach(x -> System.out.println(x));
    }

    //事务测试
    @Test
    void testTransactional() {
        redisTemplate.delete("key");
        redisTemplate.delete("t1");
        redisTemplate.delete("t2");
        redisTemplate.delete("t3");
        redisTemplate.delete("key2");

        //这里设置一个监控key
        redisTemplate.opsForValue().set("key", "yes");
        List list = (List)redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                //1.监控key是否发生变化
                redisOperations.watch("key");
                //2.开启事务，在exec命令执行前，全部都只是进入队列
                redisOperations.multi();
                redisOperations.opsForValue().set("t1", "value01");
                //对字符串做自增会报错，但是不会影响事务执行
                redisOperations.opsForValue().increment("key2",1);
                redisOperations.opsForValue().set("t2", "value02");
                redisOperations.opsForValue().set("t3", "value03");
                //3.执行exec命令，先判断key是否在监控后发生变化，如果是则不执行事务操作，否则就执行
                //每成功执行一条命令就会返回true(void方法)或者实际方法返回值，如increment返回Long
                return redisOperations.exec();
            }
        });
        //true
        //1
        //true
        //true
        list.stream().forEach(x -> System.out.println(x));


    }

    @Test
    void testString() throws InterruptedException {
        redisTemplate.delete("name");
        redisTemplate.delete("age");
        redisTemplate.delete("email");

        redisTemplate.opsForValue().set("name", "lisi");
        System.out.println(redisTemplate.opsForValue().get("name"));

        stringRedisTemplate.opsForValue().set("age", "10");
        System.out.println(stringRedisTemplate.opsForValue().get("age"));


        stringRedisTemplate.opsForValue().set("email", "aaa@123.com", 10, TimeUnit.SECONDS);
        System.out.println("email:" + stringRedisTemplate.opsForValue().get("email"));
        Thread.sleep(10001);
        System.out.println("email:" + stringRedisTemplate.opsForValue().get("email"));
    }


    @Test
    void testHash() {
        redisTemplate.delete("hashname");
        redisTemplate.delete("hashname2");
        redisTemplate.delete("hash");

        redisTemplate.opsForHash().put("hashname", "key01", "value01");
        redisTemplate.opsForHash().put("hashname", "key02", "value02");
        redisTemplate.opsForHash().put("hashname", "key03", "value03");
        Map<String, String> map = redisTemplate.opsForHash().entries("hashname");
        for (String key : map.keySet()) {
            System.out.println(key + ":" + map.get(key));
        }


        //上面每一次操作都会获取连接再关闭连接，很浪费资源，可以用下面的两种方法一次批量操作
        Map<String, String> hash = new HashMap();
        hash.put("field1", "value1");
        hash.put("field2", "value2");

        redisTemplate.opsForHash().putAll("hash", hash);
        System.out.println(redisTemplate.opsForHash().get("hash", "field2"));


        redisTemplate.execute(new SessionCallback() {
            @Override
            public Object execute(RedisOperations redisOperations) throws DataAccessException {
                redisOperations.opsForHash().put("hashname2", "key01", "value01");
                redisOperations.opsForHash().put("hashname2", "key02", "value02");
                redisOperations.opsForHash().put("hashname2", "key03", "value03");
                return null;
            }
        });


    }


    @Test
    void testList() {
        //先删除
        stringRedisTemplate.delete("listkey");
        //从左侧插入列表
        stringRedisTemplate.opsForList().leftPushAll("listkey", "value1", "value2", "value3");
        //从右侧插入列表
        stringRedisTemplate.opsForList().rightPushAll("listkey", "value4", "value5", "value6");
        List<String> stringList = stringRedisTemplate.opsForList().range("listkey", 0, stringRedisTemplate.opsForList().size("listkey") - 1);
        stringList.stream().forEach(System.out::println);

        System.out.println("======================================");

        //绑定key
        BoundListOperations<String, String> listOperations = stringRedisTemplate.boundListOps("listkey");
        //左侧弹出一个成员
        listOperations.leftPop();
        //右侧弹出一个成员
        listOperations.rightPop();

        stringList = listOperations.range(0, listOperations.size() - 1);
        stringList.stream().forEach(System.out::println);

        //左侧插入
        listOperations.leftPush("value7");
        //右侧插入
        listOperations.rightPush("value8");

        stringList = listOperations.range(0, listOperations.size() - 1);
        stringList.stream().forEach(System.out::println);


    }

    @Test
        //无序集合
    void testSet() {
        stringRedisTemplate.delete("setkey1");
        stringRedisTemplate.delete("setkey2");
        //集合成员不允许重复，所以这里value1只会插入一个
        stringRedisTemplate.opsForSet().add("setkey1", "value1", "value2", "value3", "value1", "value4", "value5");
        Set<String> members = stringRedisTemplate.opsForSet().members("setkey1");
        members.stream().forEach(System.out::println);

        System.out.println("===========================");

        //绑定key
        BoundSetOperations<String, String> setOperations = stringRedisTemplate.boundSetOps("setkey2");
        //增加多个元素
        setOperations.add("value1", "value2", "value3", "value4", "value5", "value6", "value7");
        //删除两个元素
        setOperations.remove("value2", "value3");
        //成员数量
        Long size = setOperations.size();
        //求交集
        Set<String> intersect = setOperations.intersect("setkey1");
        intersect.stream().forEach(System.out::println);

        System.out.println("============求交集===============");

        //求差集
        Set<String> diff = setOperations.diff("setkey1");
        diff.stream().forEach(System.out::println);

        System.out.println("============求差集===============");

        //求并集
        Set<String> union = setOperations.union("setkey1");
        union.stream().forEach(System.out::println);

        System.out.println("============求并集===============");

    }

    @Test
        //有序集合
    void testZSet() {
        redisTemplate.delete("zset1");

        Set<ZSetOperations.TypedTuple<String>> typedTupleSet = new HashSet<>();
        for (int i = 1; i <= 9; i++) {
            double score = i * 0.1; //排序分值，默认从小到大排序
            ZSetOperations.TypedTuple<String> typedTuple = new DefaultTypedTuple<>("value" + i, score);
            typedTupleSet.add(typedTuple);
        }
        redisTemplate.opsForZSet().add("zset1", typedTupleSet);

        BoundZSetOperations zSetOperations = redisTemplate.boundZSetOps("zset1");
        Long size = zSetOperations.size();
        System.out.println("size==" + size);
        Set range = zSetOperations.range(0, size - 1);
        range.stream().forEach(System.out::println);
        System.out.println("======================");

        zSetOperations.add("value10", 0.26);
        //取出分数范围内的数据，0.2<= range <= 0.6
        Set rangeByScore = zSetOperations.rangeByScore(0.2, 0.6);
        rangeByScore.stream().forEach(System.out::println);
        System.out.println("======================");

        //求分数
        Double value3Score = zSetOperations.score("value3");
        System.out.println(value3Score);

        System.out.println("======================");
        //删除数据
        zSetOperations.remove("value4", "value5");
        System.out.println("======================");

        //从大到小排序
        Set range1 = zSetOperations.reverseRange(0, zSetOperations.size() - 1);
        range1.stream().forEach(System.out::println);
        System.out.println("======================");

        //返回有序集合，这里在下标区间按分数排序
        Set<ZSetOperations.TypedTuple<String>> rangeWithScores = zSetOperations.rangeWithScores(0, zSetOperations.size() - 1);
        for (ZSetOperations.TypedTuple<String> typedTuple : rangeWithScores) {
            System.out.println(typedTuple.getValue() + "||" + typedTuple.getScore());
        }
        System.out.println("======================");
        //返回有序集合，这里在分数区间按分数排序
        Set<ZSetOperations.TypedTuple<String>> rangeByScoreWithScores = zSetOperations.rangeByScoreWithScores(0.1, 0.9);
        for (ZSetOperations.TypedTuple<String> typedTuple : rangeByScoreWithScores) {
            System.out.println(typedTuple.getValue() + "||" + typedTuple.getScore());
        }
        System.out.println("======================");
    }

}
