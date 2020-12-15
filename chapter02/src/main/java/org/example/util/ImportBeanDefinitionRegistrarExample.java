package org.example.util;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <h1>ImportBeanDefinitionRegistrar</h1>
 * Created by hanqf on 2020/12/15 11:07.
 *
 * 将指定包路径下的class加载到spring上下文
 */


public class ImportBeanDefinitionRegistrarExample implements ImportBeanDefinitionRegistrar {
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //获取引入ImportBeanDefinitionRegistrarExample本对象的类名称，这里是AppConfig，其是通过@MyScan注解被关联上的
        System.out.println(importingClassMetadata.getClassName());
        Set<String> annotationTypes = importingClassMetadata.getAnnotationTypes();
        annotationTypes.stream().forEach(System.out::println);

        Map<String, Object> annotationAttributes = importingClassMetadata.getAnnotationAttributes("org.example.util.MyScan");
        String packageDirName = (String)annotationAttributes.get("value");
        System.out.println("packageDirName=="+packageDirName);


        List<String> list = ClazzUtils.getClazzName(packageDirName, false);

        for (String string : list) {

            System.out.println(string);
            try {
                Class clazz = Class.forName(string);
                Annotation[] annotations = clazz.getAnnotations();
                for(Annotation annotation:annotations){

                }
                boolean anyMatch = Arrays.stream(annotations).anyMatch(annotation -> {
                    if (annotation instanceof NoRegister) {
                        return true;
                    } else {
                        return false;
                    }
                });

                if(!anyMatch){

                    //BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(clazz);
                    //AbstractBeanDefinition beanDefinition = beanDefinitionBuilder.getRawBeanDefinition();
                    //beanDefinition.getPropertyValues().addPropertyValue("interfaceClass", clazz);
                    ////通过FactoryBean构建对象
                    //beanDefinition.setBeanClass(InterfaceFactoryBean.class);
                    //beanDefinition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
                    //registry.registerBeanDefinition(clazz.getSimpleName(), beanDefinition);


                    BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.genericBeanDefinition(InterfaceFactoryBean.class);
                    beanDefinitionBuilder.addPropertyValue("interfaceClass", clazz);
                    beanDefinitionBuilder.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
                    registry.registerBeanDefinition(clazz.getSimpleName(), beanDefinitionBuilder.getBeanDefinition());

                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }




        }

    }

}
