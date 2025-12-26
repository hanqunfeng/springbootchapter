package com.example.demo.geo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.domain.geo.GeoReference;
import org.springframework.data.redis.domain.geo.GeoShape;
import org.springframework.stereotype.Component;

/**
 *
 * Created by hanqf on 2025/12/19 15:04.
 */

@Component
public class GeoUtil {
    @Autowired
    protected StringRedisTemplate redisTemplate;

    public Long add(String key, double longitude, double latitude, String member) {
        Long result = 0L;
        Point point = new Point(longitude, latitude);
        result = redisTemplate.opsForGeo().add(key, point, member);

//        RedisGeoCommands.GeoLocation<String> geoLocation = new RedisGeoCommands.GeoLocation<>(member, point);
//        result = redisTemplate.opsForGeo().add(key, geoLocation);
////
//        final List<RedisGeoCommands.GeoLocation<String>> list = List.of(geoLocation);
//        result = redisTemplate.opsForGeo().add(key, list);

        return result;

    }

    public void search(String key, double longitude, double latitude) {
        Point point = new Point(longitude, latitude);
        final Distance distance = new Distance(2000);
        Circle circle = new Circle(point, distance);
        // 只会返回成员名称，不会返回坐标 和 距离
        GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().search(key, circle);
        if (results != null) {
            results.forEach(result -> {
                System.out.println(result.getContent());
                System.out.println(result.getDistance());
            });
        }

    }

    public void search2(String key, double longitude, double latitude, double radius) {
        // 中心点
        Point point = new Point(longitude, latitude);
        // 半径
        Distance distance = new Distance(radius, RedisGeoCommands.DistanceUnit.METERS);
        // 创建圆形
        Circle circle = new Circle(point, distance);
        // 创建地理参考
        GeoReference<String> objectGeoReference = GeoReference.fromCircle(circle);
        // 创建地理形状
        GeoShape geoShape = GeoShape.byRadius(distance);
        // 创建参数
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeCoordinates() // 返回坐标
                .includeDistance() // 返回距离
                .sortAscending() // 排序
                .limit(10); // 限制返回数量

        // 查询
        final GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().search(key, objectGeoReference, geoShape, args);
        if (results != null) {
            results.forEach(result -> {
                // 获取成员名称
                System.out.println(result.getContent().getName());
                // 获取坐标
                System.out.println(result.getContent().getPoint());
                // 获取距离
                System.out.println(result.getDistance());
            });
        }
    }

    public void search(String key, String member, double radius) {
        // 半径
        Distance distance = new Distance(radius, RedisGeoCommands.DistanceUnit.METERS);
        // 创建地理参考
        GeoReference<String> objectGeoReference = GeoReference.fromMember(member);
        // 创建地理形状
        GeoShape geoShape = GeoShape.byRadius(distance);
        // 创建参数
        RedisGeoCommands.GeoRadiusCommandArgs args = RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                .includeCoordinates() // 返回坐标
                .includeDistance() // 返回距离
                .sortAscending() // 排序
                .limit(10); // 限制返回数量

        // 查询
        final GeoResults<RedisGeoCommands.GeoLocation<String>> results = redisTemplate.opsForGeo().search(key, objectGeoReference, geoShape, args);
        if (results != null) {
            results.forEach(result -> {
                // 获取成员名称
                System.out.println(result.getContent().getName());
                // 获取坐标
                System.out.println(result.getContent().getPoint());
                // 获取距离
                System.out.println(result.getDistance());
            });
        }
    }

}
