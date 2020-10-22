package com.example.response;

import com.example.exception.CustomException;
import com.example.exception.CustomExceptionType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Data;

/**
 * <h1>通用Response</h1>
 * Created by hanqf on 2020/10/15 16:19.
 */

@Data
@Builder
//定义json字段的顺序
@JsonPropertyOrder(value = {"ok","code","message","data"})
public class CommonResponse {
    /**
     * 是否成功,json显示字段名称为：ok
     */
    private boolean isOk;
    /**
     * 响应状态码，如200，400，500
     */
    private Integer code;
    /**
     * 消息
     */
    private String message;
    /**
     * 数据
     */
    //如果是null就不返回了
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;




    /**
     * <h2>成功响应信息</h2>
     * Created by hanqf on 2020/10/22 15:25. <br>
     *
     * @param message
     * @param data
     * @return com.example.response.CommonResponse
     * @author hanqf
     */
    public static CommonResponse success(String message, Object data) {
        return CommonResponse.builder().isOk(true).code(200).message(message).data(data).build();
    }

    public static CommonResponse success(String message) {
        return CommonResponse.builder().isOk(true).code(200).message(message).build();
    }

    public static CommonResponse success(Object data) {
        return CommonResponse.builder().isOk(true).code(200).message("请求响应成功").data(data).build();
    }

    public static CommonResponse success() {
        return CommonResponse.builder().isOk(true).code(200).message("请求响应成功").build();
    }


    /**
     * <h2>异常响应信息</h2>
     * Created by hanqf on 2020/10/22 15:24. <br>
     *
     * @param exception
     * @return com.example.response.CommonResponse
     * @author hanqf
     */
    public static CommonResponse error(CustomException exception) {
        return CommonResponse.builder().isOk(false).code(exception.getCode()).message(exception.getMessage()).build();
    }

    public static CommonResponse error(CustomExceptionType exceptionType, String errorMessage) {
        return CommonResponse.builder().isOk(false).code(exceptionType.getCode()).message(errorMessage).build();
    }
}
