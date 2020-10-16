package com.example.swagger3demo.response;

import lombok.Builder;
import lombok.Data;

/**
 * <h1>通用Response</h1>
 * Created by hanqf on 2020/10/15 16:19.
 */

@Data
@Builder
public class CommonResponse {
    /**
     * 是否成功
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
    private Object data;


    /**
     * <h2>成功响应信息</h2>
     * Created by hanqf on 2020/10/15 16:33. <br>
     *
     * @param message 消息
     * @param data
     * @return com.example.swagger3demo.response.CommonResponse
     * @author hanqf
     */
    public static CommonResponse success(String message, Object data) {
        return CommonResponse.builder().isOk(true).code(200).message(message).data(data).build();
    }

    public static CommonResponse success(String message) {
        return CommonResponse.builder().isOk(true).code(200).message(message).build();
    }
    public static CommonResponse success() {
        return CommonResponse.builder().isOk(true).code(200).message("请求响应成功").build();
    }


    public static CommonResponse fail(String message) {
        return CommonResponse.builder().isOk(false).code(500).message(message).build();
    }
}
