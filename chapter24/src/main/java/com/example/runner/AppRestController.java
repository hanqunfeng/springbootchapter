package com.example.runner;/**
 * Created by hanqf on 2020/4/2 16:19.
 */


import com.example.common.RequestDecrypt;
import com.example.common.ResponseEncrypt;
import com.example.model.RequestDataBody;
import com.example.model.ResponseResult;
import com.example.utils.DesUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 请求及响应数据加解密
 * @author hanqf
 * @date 2020/4/2 16:19
 */
@RestController
public class AppRestController {
    Logger log = LoggerFactory.getLogger(getClass());

    /**
     * 响应数据 加密
     */
    @GetMapping(value = "/sendResponseEncryData")
    @ResponseEncrypt(encryptClass = DesUtil.class)
    public ResponseResult sendResponseEncryData() {
        ResponseResult responseResult = ResponseResult.createResult().setSuccess(true);
        responseResult.setDataValue("name", "spring好好玩");
        responseResult.setDataValue("encry", true);
        return responseResult;
    }

    /**
     * 获取 解密后的 数据，并在响应时进行加密
     *
     * 接收的数据格式：
     * {"data":"1kDc9KiEuI7j4N+EzycxgJpzxS+PxeWoFK9Zf3EznaNXuz7Q8j2XJly554SA5ASGt/zWSBk3/2kM\nBo60dRnUOw=="}
     */
    @PostMapping(value = "/getRequestData")
    @RequestDecrypt(decryptClass = DesUtil.class)
    @ResponseEncrypt
    public ResponseResult getRequestData(@Nullable @RequestBody RequestDataBody requestDataBody) {
        ResponseResult responseResult = ResponseResult.createResult().setSuccess(true);
        if(requestDataBody != null) {
            log.info("controller接收的参数object={}", requestDataBody.toString());
            //Map map = JSONObject.parseObject(requestDataBody.getData(), Map.class);
            responseResult.setDataValue("bodyData",requestDataBody.getData());

        }else{
            log.info("controller接收的参数为null");
        }

        return responseResult;
    }
}
