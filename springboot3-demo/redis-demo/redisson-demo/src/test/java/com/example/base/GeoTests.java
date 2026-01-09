package com.example.base;

import org.junit.jupiter.api.Test;
import org.redisson.api.*;
import org.redisson.api.geo.GeoSearchArgs;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

/**
 * Geo — Redisson 对象与实现解析
 * Created by hanqf on 2026/1/9 18:06.
 */

@SpringBootTest
public class GeoTests {

    @Autowired
    private RedissonClient redisson;

    /**
     * RGeo —— 地理空间索引
     */
    @Test
    void testRGeo() {
        RGeo<String> geo = redisson.getGeo("geo:shop", StringCodec.INSTANCE);
        geo.clear();
        // 添加经纬度 (longitude, latitude):
        // GEOADD geo:shop 116.397128 39.916527 Beijing
        geo.add(116.397128, 39.916527, "Beijing");
        geo.add(121.473701, 31.230416, "Shanghai");
        geo.add(113.264385, 23.129112, "Guangzhou");

        // 查询北京附近 1500km 内的城市
        // GEOSEARCH geo:shop FROMLONLAT 116.4 39.9 BYRADIUS 1500 km ASC COUNT 5
        List<String> results = geo.search(GeoSearchArgs.from(116.4, 39.9)
                .radius(1500, GeoUnit.KILOMETERS)
                .order(GeoOrder.ASC)
                .count(5));
        results.forEach(System.out::println);

        // 获取北京与上海之间的距离
        // GEODIST geo:shop Beijing Shanghai KM
        Double distance =
                geo.dist("Beijing", "Shanghai", GeoUnit.KILOMETERS);
        System.out.println("distance = " + distance + " km");

        // 获取北京与上海之间的经纬度
        // GEOPOS geo:shop Beijing Shanghai
        Map<String, GeoPosition> pos =
                geo.pos("Beijing", "Shanghai");
        pos.forEach((k, v) ->
                System.out.println(k + " -> " + v));
    }


}
