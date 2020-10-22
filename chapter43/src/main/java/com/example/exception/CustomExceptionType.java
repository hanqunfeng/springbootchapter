package com.example.exception;

/**
 * <h1>异常类型</h1>
 * Created by hanqf on 2020/10/22 15:03.
 *
 * 代码中如果try-catch，全部在catch中向外抛出CustomException
 */
public enum CustomExceptionType {

    /**
     * 400，用户请求的相关异常，比如输入内容不符合要求，等等
     */
    USER_INPUT_ERROR(400, "您输入的数据错误或者您没有权限访问资源！"),
    /**
     * 500，系统异常，比如数据库连接异常，等等
     */
    SYSTEM_ERROR(500, "系统出现异常，请您稍后再试或者联系管理员！"),
    /**
     * 503，只要是没有通过代码捕获的异常全部定义为503，具体问题看日志吧
     */
    OTHER_ERROR(503, "系统出现未知异常，请您联系管理员！");


    /**
     * 异常类型中文描述
     */
    private String desc;

    /**
     * 异常类型状态码，400，500，503
     */
    private int code;

    CustomExceptionType(int code, String desc) {
        this.desc = desc;
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }


    public int getCode() {
        return code;
    }

}
