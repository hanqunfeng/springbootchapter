package com.example.common;

import com.alibaba.fastjson.JSON;
import com.example.model.ResponseResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;

/**
 * <p>解密处理器</p>
 * Created by hanqf on 2020/9/2 10:23.
 */


@Component
@ControllerAdvice(annotations = {RestController.class})
public class EncryResponseBodyAdvice implements ResponseBodyAdvice<Object> {
    private static final Logger log = LoggerFactory.getLogger(EncryResponseBodyAdvice.class);
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object obj, MethodParameter returnType, MediaType selectedContentType, Class<? extends HttpMessageConverter<?>> selectedConverterType, ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        ResponseEncrypt responseEncrypt = null;
        //方法注解优先
        if (returnType.getMethod().isAnnotationPresent(ResponseEncrypt.class)) {
            responseEncrypt = returnType.getMethod().getAnnotation(ResponseEncrypt.class);
        }else if (returnType.getContainingClass().isAnnotationPresent(ResponseEncrypt.class)) {
            responseEncrypt = returnType.getContainingClass().getAnnotation(ResponseEncrypt.class);
        }

        if(responseEncrypt != null){
            boolean needEncrypt = responseEncrypt.value();
            if(needEncrypt) {
                if(obj instanceof ResponseResult) { //只对返回特定类型的情况进行处理
                    ResponseResult responseResult = (ResponseResult) obj;
                    try {
                        if(responseResult.getData().size()>0) { //只有数据不为空时才加密
                            String srcData = JSON.toJSONString(responseResult.getData());
                            //加密
                            Class encryptClass = responseEncrypt.encryptClass();
                            Method method = encryptClass.getMethod(responseEncrypt.encryptMethod(), String.class);
                            String returnStr = (String) method.invoke(null, srcData);
                            log.info("原始数据={},加密后数据={}", srcData, returnStr);
                            //清空map
                            responseResult.getData().clear();
                            responseResult.setDataValue("encryptData", returnStr);
                            //添加header，告诉前端数据已加密
                            serverHttpResponse.getHeaders().add("encrypt", "true");
                        }
                    } catch (Exception e) {
                        log.error("异常！", e);
                        e.printStackTrace();
                    }
                    return responseResult;
                }
            }
        }


        return obj;
    }

}
