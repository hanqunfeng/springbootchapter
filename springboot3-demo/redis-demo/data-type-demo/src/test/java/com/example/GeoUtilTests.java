package com.example;

import com.example.demo.geo.GeoUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 *
 * Created by hanqf on 2025/12/19 15:27.
 */

@SpringBootTest
public class GeoUtilTests {
    @Autowired
    private GeoUtil geoUtil;

    @Test
    void add(){
        String key = "testgeo";
        double longitude = 116.404;
        double latitude = 39.915;
        String member = "tiananmen";
        System.out.println(geoUtil.add(key, longitude, latitude, member));
    }

    @Test
    void add2(){
        String key = "testgeo";
        double longitude = 116.391248;
        double latitude = 39.906217;
        String member = "xidan";
        System.out.println(geoUtil.add(key, longitude, latitude, member));
    }

    @Test
    void search(){
        String key = "testgeo";
        double longitude = 116.404;
        double latitude = 39.915;
        String member = "天安门1";
        geoUtil.search(key, longitude, latitude);
    }

    @Test
    void search2(){
        String key = "testgeo";
        double longitude = 116.404;
        double latitude = 39.915;
        String member = "天安门1";
        geoUtil.search2(key, longitude, latitude,2000);
    }
}
