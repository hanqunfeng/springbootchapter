package com.geccocrawler.gecco.spider.render.html;

import com.geccocrawler.gecco.annotation.*;
import com.geccocrawler.gecco.downloader.DownloaderContext;
import com.geccocrawler.gecco.request.HttpRequest;
import com.geccocrawler.gecco.response.HttpResponse;
import com.geccocrawler.gecco.spider.SpiderBean;
import com.geccocrawler.gecco.spider.render.FieldRender;
import com.geccocrawler.gecco.spider.render.FieldRenderException;
import com.geccocrawler.gecco.utils.DownloadImage;
import net.sf.cglib.beans.BeanMap;
import org.apache.commons.lang3.StringUtils;
import org.reflections.ReflectionUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 渲染@Image属性
 *
 * @author huchengyi
 */
public class ImageFieldRender implements FieldRender {

    private static final Class[] imageAnnotationClass = {
            Image.class, ParentImage.class,
            ParentPreviousImage.class, ParentNextImage.class,
            BrotherNextImage.class, BrotherPreviousImage.class
    };

    private Object returnValue(String parentPath, String imgUrl, HttpRequest request, Field field) {
        if (StringUtils.isEmpty(parentPath)) {
            return imgUrl;
        }
        HttpResponse subReponse = null;
        try {
            String before = StringUtils.substringBefore(imgUrl, "?");
            String last = StringUtils.substringAfter(imgUrl, "?");
            String fileName = StringUtils.substringAfterLast(before, "/");
            if (StringUtils.isNotEmpty(last)) {
                last = URLEncoder.encode(last, "UTF-8");
                imgUrl = before + "?" + last;
            }
            HttpRequest subRequest = request.subRequest(imgUrl);
            subReponse = DownloaderContext.defaultDownload(subRequest);
            return DownloadImage.download(parentPath, fileName, subReponse.getRaw());
        } catch (Exception ex) {
            //throw new FieldRenderException(field, ex.getMessage(), ex);
            FieldRenderException.log(field, "download image error : " + imgUrl, ex);
            return imgUrl;
        } finally {
            if (subReponse != null) {
                subReponse.close();
            }
        }
    }


    @Override
    @SuppressWarnings("unchecked")
    public void render(HttpRequest request, HttpResponse response, BeanMap beanMap, SpiderBean bean) {
        for (Class clazz : imageAnnotationClass) {
            renderImage(request, response, beanMap, bean, clazz);
        }
    }


    private <T extends Annotation> void renderImage(HttpRequest request, HttpResponse response, BeanMap beanMap, SpiderBean bean, Class<T> tClass) {
        Map<String, Object> fieldMap = new HashMap<String, Object>();
        Set<Field> imageFields = ReflectionUtils.getAllFields(bean.getClass(), ReflectionUtils.withAnnotation(tClass));
        for (Field imageField : imageFields) {
            Object value = beanMap.get(imageField.getName());
            if (value != null) {
                String imgUrl = value.toString();
                if (StringUtils.isNotEmpty(imgUrl)) {
                    String parentPath = null;
                    Annotation image = imageField.getAnnotation(tClass);
                    Class<? extends Annotation> aClass = image.getClass();
                    try {
                        parentPath = (String) aClass.getMethod("download").invoke(image);
                    } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                    value = returnValue(parentPath, imgUrl, request, imageField);
                }
            }
            if (value != null) {
                fieldMap.put(imageField.getName(), value);
            }
        }
        beanMap.putAll(fieldMap);
    }

}
