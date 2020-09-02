package com.example.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>结果模型</p>
 * Created by hanqf on 2020/9/2 10:11.
 */


public class ResponseResult implements Serializable {

    private static final long serialVersionUID = -8530387260540806792L;
    /**
     * 成功 true，失败 false
    */
    private boolean success;

    /**
     * 结果数据
    */
    private Map<String,Object> data = new HashMap<>();

    public boolean isSuccess() {
        return success;
    }

    public ResponseResult setSuccess(boolean success) {
        this.success = success;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public static ResponseResult createResult(){
        return new ResponseResult();
    }

    public void setDataValue(String name, Object object) {
        data.put(name,object);
    }
}
