package com.example.response;

import org.springframework.core.MethodParameter;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * <h1>全局响应处理器</h1>
 * Created by hanqf on 2020/10/22 15:40.
 */

@ControllerAdvice(basePackages = {"com.example"})
public class GlobalResponseAdvice implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {

        //关于HAL_JSON可以参考:https://www.jianshu.com/p/faba6b57f915 application/hal+json
        if (selectedContentType.equalsTypeAndSubtype(MediaType.APPLICATION_JSON)
                || selectedContentType.equalsTypeAndSubtype(MediaTypes.HAL_JSON)) {

            if (body instanceof CommonResponse) {
                response.setStatusCode(HttpStatus.valueOf(((CommonResponse) body).getCode()));
                return body;
            } else {
                //返回值不是CommonResponse对象，但是MediaType是json类型，则将返回值封装为CommonResponse
                return CommonResponse.success(body);
            }
        }

        return body;
    }
}
