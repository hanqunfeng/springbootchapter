package com.geccocrawler.gecco.spider.render;

import com.geccocrawler.gecco.annotation.*;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.response.HttpResponse;
import com.geccocrawler.gecco.scheduler.DeriveSchedulerContext;
import com.geccocrawler.gecco.spider.SpiderBean;
import com.geccocrawler.gecco.utils.ReflectUtils;
import net.sf.cglib.beans.BeanMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.reflections.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * render抽象方法，主要包括注入基本的属性和自定义属性注入。将特定的html、json、xml注入放入实现类
 *
 * @author huchengyi
 */
public abstract class AbstractRender implements Render {

    private static Log log = LogFactory.getLog(AbstractRender.class);

    private static final Class[] hrefAnnotationClass = {
            Href.class, ParentHref.class,
            ParentPreviousHref.class, ParentNextHref.class,
            BrotherNextHref.class, BrotherPreviousHref.class
    };

    /**
     * request请求的注入
     */
    private RequestFieldRender requestFieldRender;

    /**
     * request参数的注入
     */
    private RequestParameterFieldRender requestParameterFieldRender;

    /**
     * 自定义注入
     */
    private CustomFieldRenderFactory customFieldRenderFactory;

    public AbstractRender() {
        this.requestFieldRender = new RequestFieldRender();
        this.requestParameterFieldRender = new RequestParameterFieldRender();
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public SpiderBean inject(Class<? extends SpiderBean> clazz, HttpRequest request, HttpResponse response) {
        try {
            SpiderBean bean = clazz.newInstance();
            BeanMap beanMap = BeanMap.create(bean);
            requestFieldRender.render(request, response, beanMap, bean);
            requestParameterFieldRender.render(request, response, beanMap, bean);
            fieldRender(request, response, beanMap, bean);
            Set<Field> customFields = ReflectionUtils.getAllFields(bean.getClass(), ReflectionUtils.withAnnotation(FieldRenderName.class));
            for (Field customField : customFields) {
                FieldRenderName fieldRender = customField.getAnnotation(FieldRenderName.class);
                String name = fieldRender.value();
                CustomFieldRender customFieldRender = customFieldRenderFactory.getCustomFieldRender(name);
                if (customFieldRender != null) {
                    customFieldRender.render(request, response, beanMap, bean, customField);
                }
            }
            requests(request, bean);
            return bean;
        } catch (Exception ex) {
            //throw new RenderException(ex.getMessage(), clazz);
            log.error("instance SpiderBean error", ex);
            return null;
        }
    }

    public abstract void fieldRender(HttpRequest request, HttpResponse response, BeanMap beanMap, SpiderBean bean);


    private void doSomething(String discardPattern, BeanMap beanMap, Field hrefField, HttpRequest request) {
        Object object = beanMap.get(hrefField.getName());
        if (discardPattern != null && !"".equals(discardPattern)) {
            Pattern r = Pattern.compile(discardPattern);
            Matcher m = r.matcher((String) object);
            if (m.matches()) {
                return;
            }
        }

        if (object != null) {
            boolean isList = ReflectUtils.haveSuperType(object.getClass(), List.class);// 是List类型
            if (isList) {
                List<String> list = (List<String>) object;
                for (String url : list) {
                    if (StringUtils.isNotEmpty(url)) {
                        DeriveSchedulerContext.into(request.subRequest(url));
                    }
                }
            } else {
                String url = (String) object;
                if (StringUtils.isNotEmpty(url)) {
                    DeriveSchedulerContext.into(request.subRequest(url));
                }
            }
        }
    }

    private <T extends Annotation> void requestsHref(HttpRequest request, SpiderBean bean, Class<T> tClass) {
        BeanMap beanMap = BeanMap.create(bean);
        Set<Field> hrefFields = ReflectionUtils.getAllFields(bean.getClass(),
                ReflectionUtils.withAnnotation(tClass));
        for (Field hrefField : hrefFields) {
            Annotation href = hrefField.getAnnotation(tClass);
            boolean click;
            String discardPattern;

            try {
                Class<? extends Annotation> aClass = href.getClass();
                click = (Boolean) aClass.getMethod("click").invoke(href);
                discardPattern = (String) aClass.getMethod("discardPattern").invoke(href);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            if (click) {
                doSomething(discardPattern, beanMap, hrefField, request);
            }
        }
    }


    /**
     * 需要继续抓取的请求
     */
    @Override
    @SuppressWarnings({"unchecked"})
    public void requests(HttpRequest request, SpiderBean bean) {
        for (Class clazz : hrefAnnotationClass) {
            requestsHref(request, bean, clazz);
        }
    }

    public void setCustomFieldRenderFactory(CustomFieldRenderFactory customFieldRenderFactory) {
        this.customFieldRenderFactory = customFieldRenderFactory;
    }

}
