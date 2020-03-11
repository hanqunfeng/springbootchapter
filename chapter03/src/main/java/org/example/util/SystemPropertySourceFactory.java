package org.example.util;/**
 * Created by hanqf on 2020/3/2 21:41.
 */


import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SystemEnvironmentPropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;
import java.util.HashMap;

/**
 * 针对自定义配置文件的一个多环境配置逻辑
 * 在@PropertySource中增加factory属性 factory = SystemPropertySourceFactory.class
 * 这里没有用上，只是给出一种解决方案
 * @author hanqf
 * @date 2020/3/2 21:41
 */
public class SystemPropertySourceFactory implements PropertySourceFactory {

    @Override
    public PropertySource<?> createPropertySource(String name, EncodedResource resource)
            throws IOException {
        String fileSuffix = "";
        try {
            String filename = resource.getResource().getFilename();
            fileSuffix = filename.substring(filename.lastIndexOf("."));
            resource.getResource().getFile();
            return new ResourcePropertySource(name, resource);
        } catch (Exception e) {
            //InputStream inputStream = SystemPropertySourceFactory.class.getClassLoader()
            //        .getResourceAsStream(name + fileSuffix);
            ////转成resource
            //InputStreamResource inResource = new InputStreamResource(inputStream);
            //return new ResourcePropertySource(new EncodedResource(inResource));

            //这里其实没啥用，就是为了不重复加载文件，因为重复加载会报错， 不同文件名称加载的相同属性名称，后加载的会覆盖先加载的
            return new SystemEnvironmentPropertySource(name,new HashMap<>());

        }
    }

}
