package com.example.response;

import com.example.exception.CustomException;
import com.example.exception.CustomExceptionType;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/**
 * <h1>通用Response</h1>
 * Created by hanqf on 2020/10/15 16:19.
 */

//定义json字段的顺序
@JsonPropertyOrder(value = {"ok","code", "message", "data"})
public class CommonResponse {
    /**
     * 是否成功,true成功，false失败
     */
    private boolean ok;
    /*
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
    //如果是null就不在json中显示了，现在使用全局配置
    //@JsonInclude(JsonInclude.Include.NON_NULL)
    private Object data;

    /**
     * 进制自定义响应
    */
    private CommonResponse() {
    }

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
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setOk(true);
        commonResponse.setCode(200);
        commonResponse.setMessage(message);
        commonResponse.setData(data);
        return commonResponse;
    }

    public static CommonResponse success(String message) {
        return success(message,null);
    }

    public static CommonResponse success(Object data) {
        return success("请求响应成功",data);
    }

    public static CommonResponse success() {
        return success("请求响应成功",null);
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
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setOk(false);
        commonResponse.setCode(exception.getCode());
        commonResponse.setMessage(exception.getMessage());
        return commonResponse;
    }

    public static CommonResponse error(CustomExceptionType exceptionType, String errorMessage) {
        CommonResponse commonResponse = new CommonResponse();
        commonResponse.setOk(false);
        commonResponse.setCode(exceptionType.getCode());
        commonResponse.setMessage(errorMessage);
        return commonResponse;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public Integer getCode() {
        return code;
    }

    private void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    private void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    private void setData(Object data) {
        this.data = data;
    }
}
