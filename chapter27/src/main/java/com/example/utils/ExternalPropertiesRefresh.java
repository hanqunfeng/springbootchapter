package com.example.utils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * <p>扫描外部属性类</p>
 * Created by hanqf on 2020/4/17 14:50.
 */

@Slf4j
@Component
public class ExternalPropertiesRefresh {

    @Autowired
    private ConfigurableListableBeanFactory configurableListableBeanFactory;

    /**
     * <p>根据属性名获取属性值</p>
     *
     * @param fieldName bean的属性名称
     * @param object    bean对象
     * @return java.lang.Object get方法返回值
     * @author hanqf
     * 2020/4/17 23:06
     */
    private Object getFieldValueByName(String fieldName, Object object) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(object, new Object[]{});
            return value;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }


    /**
     * <p>根据属性名设置属性值</p>
     *
     * @param fieldName  bean的属性名称
     * @param object     bean对象
     * @param paramTypes set方法参数类型
     * @param params     set方法参数值
     * @author hanqf
     * 2020/4/17 23:08
     */
    private void setFieldValueByName(String fieldName, Object object, Class[] paramTypes, Object[] params) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String setter = "set" + firstLetter + fieldName.substring(1);
            Method method = object.getClass().getMethod(setter, paramTypes);
            method.invoke(object, params);

        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    /**
     * <p>获取属性名称，去除前缀</p>
     *
     * @param key   属性key
     * @param prefix 属性key前缀
     * @return java.lang.String
     * @author hanqf
     * 2020/4/17 19:54
     */
    private String fieldName(String key, String prefix) {
        if (StringUtils.hasText(prefix)) {
            return key.replace(prefix + ".", "");
        }
        return key;
    }

    /**
     * <p>将属性文件值绑定到bean对象</p>
     *
     * @param bean
     * @param properties
     * @param prefix
     * @author hanqf
     * 2020/4/17 16:21
     */
    private Object bind(Object bean, Properties[] properties, String prefix) {
        String fieldName = "";//属性名称
        String pValue = "";//属性值
        String[] sp = null; //map属性分割key和value
        for (Properties pro : properties) {
            Map<String, Map<String, String>> fidleMap = new HashMap<>();
            Map<String, Set<String>> fidleSet = new HashMap<>();
            Map<String, List<String>> fidleList = new HashMap<>();
            //遍历属性
            for (Object key : pro.keySet()) {
                pValue = (String) (pro.get(key));
                fieldName = fieldName((String) key, prefix);

                //map
                sp = fieldName.split("\\.");
                if (sp.length == 2) {
                    fieldName = sp[0];
                }

                //list&&set
                if (fieldName.indexOf("[") > 0) {
                    fieldName = fieldName.substring(0, fieldName.indexOf("["));
                }

                //属性类型
                Object object = getFieldValueByName(fieldName, bean);

                //类型匹配
                if (object instanceof Map) {
                    if (fidleMap.get(fieldName) != null) {
                        object = fidleMap.get(fieldName);
                    } else {
                        object = new HashMap<String, String>();
                    }
                    if (sp.length == 2) {
                        ((Map) object).put(sp[1], pValue);
                        fidleMap.put(fieldName, (Map<String, String>) object);
                    }
                } else if (object instanceof Set) {
                    if (fidleSet.get(fieldName) != null) {
                        object = fidleSet.get(fieldName);
                    } else {
                        object = new HashSet<String>();
                    }
                    ((Set) object).add(pValue);
                    fidleSet.put(fieldName, (Set<String>) object);
                } else if (object instanceof List) {
                    if (fidleList.get(fieldName) != null) {
                        object = fidleList.get(fieldName);
                    } else {
                        object = new ArrayList<String>();
                    }
                    ((List) object).add(pValue);
                    fidleList.put(fieldName, (List<String>) object);
                } else if (object instanceof String) {
                    setFieldValueByName(fieldName, bean, new Class[]{String.class}, new Object[]{pValue});
                } else if (object instanceof Integer) {
                    setFieldValueByName(fieldName, bean, new Class[]{Integer.class}, new Object[]{Integer.valueOf(pValue)});
                } else if (object instanceof Long) {
                    setFieldValueByName(fieldName, bean, new Class[]{Long.class}, new Object[]{Long.valueOf(pValue)});
                } else if (object instanceof Double) {
                    setFieldValueByName(fieldName, bean, new Class[]{Double.class}, new Object[]{Double.valueOf(pValue)});
                } else if (object instanceof Float) {
                    setFieldValueByName(fieldName, bean, new Class[]{Float.class}, new Object[]{Float.valueOf(pValue)});
                }
            }

            //map类型赋值
            if (fidleMap.size() > 0) {
                for (String fname : fidleMap.keySet()) {
                    setFieldValueByName(fname, bean, new Class[]{Map.class}, new Object[]{fidleMap.get(fname)});
                }
            }

            //set类型赋值
            if (fidleSet.size() > 0) {
                for (String fname : fidleSet.keySet()) {
                    setFieldValueByName(fname, bean, new Class[]{Set.class}, new Object[]{fidleSet.get(fname)});
                }
            }

            //list类型赋值
            if (fidleList.size() > 0) {
                for (String fname : fidleList.keySet()) {
                    setFieldValueByName(fname, bean, new Class[]{List.class}, new Object[]{fidleList.get(fname)});
                }
            }

        }

        return bean;
    }


    /**
     * <p>刷新指定属性类</p>
     * @author hanqf
     * 2020/4/18 12:24
     * @param beanName bean的注册名称，默认类名称首字母小写
     */
    @SneakyThrows
    public void refresh(String beanName){
        log.info("refresh " + beanName + " start");
        Class<?> cls = configurableListableBeanFactory.getType(beanName);
        Object bean = configurableListableBeanFactory.getBean(cls);
        Properties[] propertiesArray = null;
        String prefix = "";
        if (cls.getAnnotations() != null && cls.getAnnotations().length > 0) {
            for (Annotation annotation : cls.getAnnotations()) {

                if (annotation instanceof PropertySource) {
                    PropertySource propertySource = (PropertySource) annotation;
                    String[] values = propertySource.value();
                    if (values.length > 0) {
                        propertiesArray = new Properties[values.length];
                        for (int i = 0; i < values.length; i++) {
                            //如果引用的是外部文件，则重新加载
                            if (values[i].startsWith("file:")) {
                                String path = values[i].replace("file:", "");
                                Properties properties = PropertiesLoaderUtils.loadProperties(new FileSystemResource(path));
                                propertiesArray[i] = properties;
                            }
                        }
                    }
                }

                if (annotation instanceof ConfigurationProperties) {
                    ConfigurationProperties configurationProperties = (ConfigurationProperties) annotation;
                    prefix = configurationProperties.prefix();
                }

            }
        }

        if (propertiesArray != null && propertiesArray.length > 0) {
            //将属性绑定到对象
            bind(bean, propertiesArray, prefix);

        }
        log.info("bean==" + bean);
        log.info("refresh " + beanName + " end");
    }

    /**
     * <p>刷新全部属性类</p>
     *
     * @author hanqf
     * 2020/4/17 16:20
     */
    @SneakyThrows
    public void refresh() {
        String[] ary = configurableListableBeanFactory.getBeanNamesForAnnotation(PropertySource.class);
        if (ary != null && ary.length > 0) {
            for (String beanName : ary) {
                //通过Spring的beanName获取bean的类型
                refresh(beanName);
            }
        }
    }
}
