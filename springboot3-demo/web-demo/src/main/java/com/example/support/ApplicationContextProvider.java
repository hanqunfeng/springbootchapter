package com.example.support;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

/**
 * ${DESCRIPTION}
 */


@Component
public class ApplicationContextProvider
        implements ApplicationContextAware {
    /**
     * 上下文对象实例
     */
    private static ApplicationContext applicationContext;

    /**
     * 获取applicationContext
     *
     * @return
     */
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextProvider.applicationContext = applicationContext;
    }

    /**
     * 通过name获取 Bean.
     *
     * @param name
     * @return
     */
    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }

    /**
     * 通过class获取Bean.
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(Class<T> clazz) {
        return getApplicationContext().getBean(clazz);
    }

    /**
     * 通过name,以及Clazz返回指定的Bean
     *
     * @param name
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }


    public static String getMessage(String code) {
        return getApplicationContext().getMessage(code, new Object[]{}, LocaleContextHolder.getLocale());
    }
    public static String getMessage(String code,
                             String defaultMessage) {
        return getApplicationContext().getMessage(code, new Object[]{}, defaultMessage,
                LocaleContextHolder.getLocale());
    }

    /**
     * 描述 : <获得多语言的资源内容>. <br>
     * <p>
     * <使用方法说明>
     * </p>
     *
     * @param code
     * @param args
     * @return
     */
    public static String getMessage(String code, Object[] args) {
        return getApplicationContext().getMessage(code, args, LocaleContextHolder.getLocale());
    }

    /**
     * 描述 : <获得多语言的资源内容>. <br>
     * <p>
     * <使用方法说明>
     * </p>
     *
     * @param code
     * @param args
     * @param defaultMessage
     * @return
     */
    public static String getMessage(String code, Object[] args,
                                    String defaultMessage) {
        return getApplicationContext().getMessage(code, args, defaultMessage,
                LocaleContextHolder.getLocale());
    }

    public static <T> T getProperty(String key, Class<T> targetType){
        return getApplicationContext().getEnvironment().getProperty(key,targetType);
    }
    public static <T> T getProperty(String key, Class<T> targetType, T defaultValue){
        return getApplicationContext().getEnvironment().getProperty(key,targetType,defaultValue);
    }
}
