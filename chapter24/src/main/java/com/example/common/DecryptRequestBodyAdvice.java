package com.example.common;

/**
 * <p>RequestBody处理器--解密</p>
 * Created by hanqf on 2020/9/2 09:55.
 */


import com.example.model.RequestDataBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.RequestBodyAdvice;

import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

@Component
@ControllerAdvice(annotations = {RestController.class})
public class DecryptRequestBodyAdvice implements RequestBodyAdvice {
    private static final Logger log = LoggerFactory.getLogger(DecryptRequestBodyAdvice.class);

    @Override
    public boolean supports(MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter methodParameter, Type targetType, Class<? extends HttpMessageConverter<?>> selectedConverterType) throws IOException {
        return inputMessage;
    }

    @Override
    public Object afterBodyRead(Object body, HttpInputMessage inputMessage, MethodParameter parameter, Type targetType, Class<? extends HttpMessageConverter<?>> converterType) {

        //请求头中包含加密参数encrypt，并且其值为true时才进行解密
        String encryptHeader = inputMessage.getHeaders().getFirst("encrypt");
        if(StringUtils.hasText(encryptHeader) && "true".equals(encryptHeader)){
            RequestDecrypt requestDecrypt = null;
            //方法注解优先
            if (parameter.getMethod().isAnnotationPresent(RequestDecrypt.class)) {
                requestDecrypt = parameter.getMethod().getAnnotation(RequestDecrypt.class);
            } else if (parameter.getContainingClass().isAnnotationPresent(RequestDecrypt.class)) {
                requestDecrypt = parameter.getContainingClass().getAnnotation(RequestDecrypt.class);
            }

            if (requestDecrypt != null) {
                boolean needDecrypt = requestDecrypt.value();
                if (needDecrypt) {
                    try {
                        //解密操作
                        //只对接收类型为特定类型的情况进行处理
                        if (body instanceof RequestDataBody) {
                            RequestDataBody requestDataBody = (RequestDataBody) body;

                            String srcData = requestDataBody.getData();
                            if (StringUtils.hasText(srcData)) { //有数据才进行解密
                                //解密数据
                                Class decryptClass = requestDecrypt.decryptClass();
                                Method method = decryptClass.getMethod(requestDecrypt.decryptMethod(), String.class);
                                String dealData = (String) method.invoke(null, srcData);
                                log.info("原始数据={},解密后数据={}", srcData, dealData);
                                requestDataBody.setData(dealData);
                                return requestDataBody;
                            }
                        }
                    } catch (Exception e) {
                        log.error("异常！", e);
                        e.printStackTrace();
                    }

                }
            }
        }

        return body;


    }

    @Override
    public Object handleEmptyBody(@Nullable Object var1, HttpInputMessage var2, MethodParameter var3, Type var4, Class<? extends HttpMessageConverter<?>> var5) {
        log.info("Request Body is Empty");
        return var1;
    }
}
