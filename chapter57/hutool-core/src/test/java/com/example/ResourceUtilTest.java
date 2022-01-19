package com.example;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.Console;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

/**
 * <h1></h1>
 * Created by hanqf on 2021/12/13 19:26.
 */


public class ResourceUtilTest {

    @Test
    /**
     * <h2>读取类路径下任意文件</h2>
     * Created by hanqf on 2021/12/13 19:40.
     * @author hanqf
     */
    public void ResourceUtil(){
        try(BufferedReader reader = ResourceUtil.getReader("test.xml", StandardCharsets.UTF_8)){
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        final String readStr = ResourceUtil.readStr("test.xml", StandardCharsets.UTF_8);
        //System.out.println(readStr);
    }

    @Test
    /**
     * <h2>读取properties文件</h2>
     * Created by hanqf on 2021/12/13 19:40.
     * @author hanqf
     */
    public void demo() throws IOException {
        ClassPathResource resource = new ClassPathResource("test.properties");
        Properties properties = new Properties();
        properties.load(resource.getStream());

        Console.log("Properties: {}", properties);
    }

}
