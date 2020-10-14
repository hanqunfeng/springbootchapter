package com.example.swagger3mvndemo.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * <h1>DemoController</h1>
 * Created by hanqf on 2020/10/9 12:47.
 */
@Api(tags = "Api接口-DemoController")
@RestController
@RequestMapping("/demo")
public class DemoController {

    @ApiOperation(value = "demo",notes = "swagger api demo")
    @GetMapping("/demoMap")
    public Map<String,String> demoMap(){
        Map<String, String> map = new HashMap<>();
        map.put("name","张三");
        map.put("age","33");
        return map;
    }


    //附件不好使，不会显示file上传按钮，导入到yapi后也不会被识别，需要自己定义file参数
    //curl -X POST "http://localhost:8080/demo/files" -H "accept: */*" -H "Content-Type: multipart/form-data" -F "multipartFile=@/Users/hanqf/Desktop/me.png"
    @ApiOperation(value = "上传附件测试")
    @PostMapping(value = "/files",consumes = "multipart/*" ,headers = "Content-Type=multipart/form-data")
    public Map<String, String> fileUpload(@ApiParam(required = true) MultipartFile multipartFile){
        Map<String, String> map = new HashMap<>();
        System.out.println(multipartFile.getOriginalFilename());

        return map;


    }
}
