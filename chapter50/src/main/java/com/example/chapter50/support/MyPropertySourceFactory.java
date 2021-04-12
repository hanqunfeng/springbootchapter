package com.example.chapter50.support;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * <h1>自定义配置文件加载工厂</h1>
 * Created by hanqf on 2021/4/12 15:47.
 *
 * 使用方式：
 * @Configuration 注解的类上使用 @PropertySource，并指定factory参数为本类
 * 例如:
 * @Configuration
 * @PropertySource(name = "xxxName", value = "classpath*:application-xxx-${spring.profiles.active}.properties", factory = MyPropertySourceFactory.class)
 * public class MyConfig(){
 *
 * }
 *
 * 说明:
 * 1. classpath* : 表示从所有类路径下查找，包含引入的jar。如果只从当前项目中查找，则去掉 * 即可。
 * 2. 一定要设置name属性的值，以区分不同的配置文件
 * 3. ${spring.profiles.active}表示支持不同的环境，这里需要指定环境，要求自定义配置文件名称格式为application-xxx-${spring.profiles.active}.properties
 * 4. 会自动加载不含环境参数的配置文件，即支持application-xxx.properties
 * 5. 不影响主配置文件及其环境文件的加载
 *
 */

@Slf4j
public class MyPropertySourceFactory implements PropertySourceFactory {
    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource) throws IOException {

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(
                new DefaultResourceLoader());
        Resource[] res = null;
        Resource[] noEvnRes = null;
        String resourcePattern = resource.getResource().getFilename();

        int lastIndexOf = resourcePattern.lastIndexOf("-");

        String suffix = resourcePattern.split("\\.")[1];

        //去掉环境的文件名称
        String noEvnResourcePattern = resourcePattern.substring(0,lastIndexOf) + "." + suffix;

        try {
            res = resolver.getResources(resourcePattern);
            noEvnRes = resolver.getResources(noEvnResourcePattern);
            //先加载没有环境的，后加载有环境的，这样相同配置有环境的会覆盖没有环境的配置
            res = ArrayUtils.addAll(noEvnRes,res);

        } catch (Exception e) {
            log.error("Find resource by pattern [" + resourcePattern
                    + "] failed!!", e);
        }

        //如果是多个
        List<InputStream> inputStreamList = new ArrayList<>();

        if (res != null) {
            for (Resource item : res) {
                InputStream in = item.getInputStream();
                if (in != null) {
                    inputStreamList.add(in);
                }
            }

            //串行流，将多个文件流合并成一个流
            SequenceInputStream inputStream = new SequenceInputStream(Collections.enumeration(inputStreamList));
            //转成resource
            InputStreamResource inputStreamResource = new InputStreamResource(inputStream);

            return (name != null ? new ResourcePropertySource(name, new EncodedResource(inputStreamResource)) : new ResourcePropertySource(new EncodedResource(inputStreamResource)));
        }

        return null;
    }
}
