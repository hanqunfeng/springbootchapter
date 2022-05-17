package com.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.StringJoiner;

@SpringBootTest
class Chapter59ApplicationTests {

    public static final String URL_STR = "http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/";
    public static final String CHARSET = "utf-8";

    private Document getDocument(String url_str,String charset){
        try {
            String userAgent = "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.119 Safari/537.36";
            URL url = new URL(url_str);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            //是否允许缓存，默认true。
            connection.setUseCaches(Boolean.FALSE);
            //设置请求头信息
            connection.addRequestProperty("Connection", "close");
            connection.addRequestProperty("user-agent", userAgent);
            //设置连接主机超时（单位：毫秒）
            connection.setConnectTimeout(80000);
            //设置从主机读取数据超时（单位：毫秒）
            connection.setReadTimeout(80000);

            //开始请求
            Document document = Jsoup.parse(connection.getInputStream(), charset, url_str);
            if(document == null) {
                throw new RuntimeException("document == null");
            }
            return document;

        } catch (Exception e) {
            System.out.println("parse error: " + url_str);
            e.printStackTrace();
        }
        return null;
    }

    @Test
    void getData()  {
        Document rootDoc = getDocument(URL_STR, CHARSET);

        Element firstElement = rootDoc.getElementsByClass("center_list_contlist").get(0);
        String yearHref = firstElement.select("a").get(0).attr("href"); // 最近一个年份的省份链接
        System.out.println(yearHref);

        Document province_doc = getDocument(yearHref, CHARSET);
        // 遍历所有的省
        Elements provinceElements = province_doc.getElementsByClass("provincetr");
        for (Element element : provinceElements) {
            Elements aEles = element.select("a");
            for (Element aEle : aEles) {
                String name = aEle.text();
                String provincesHref = aEle.attr("href");
                String code = provincesHref.substring(0, provincesHref.indexOf("."));
                int index = yearHref.lastIndexOf("/") + 1;
                provincesHref = yearHref.substring(0, index) + provincesHref;
                System.out.println(new StringJoiner(",").add(name).add(code).add(provincesHref));

                Document city_doc = getDocument(provincesHref, CHARSET);

                //遍历城市
                Elements cityElements = rootDoc.getElementsByClass("citytr");
                for (Element cityElement : cityElements) {
                    Element city_aEle = cityElement.select("a").get(1); // 第二个是市的名字
                    String city_name = city_aEle.text();
                    String cityHref = city_aEle.attr("href");

                    int start = cityHref.lastIndexOf("/") + 1;
                    String city_code = cityHref.substring(start, cityHref.indexOf("."));
                    System.out.println(new StringJoiner(",").add(city_name).add(city_code).add(cityHref));

                }


            }
        }





    }

}
