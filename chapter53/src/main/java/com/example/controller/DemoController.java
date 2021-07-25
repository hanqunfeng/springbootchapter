package com.example.controller;

import com.example.support.OssResult;
import com.example.support.OssTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1></h1>
 * Created by hanqf on 2021/7/25 23:48.
 */

@RestController
public class DemoController {

    @Autowired
    OssTemplate ossTemplate;

    @PostMapping("/file")
    public List<OssResult> file(MultipartFile[] multipartFiles){
        List<OssResult> list = new ArrayList<>();
        if(multipartFiles!=null&&multipartFiles.length>0){
            for(MultipartFile multipartFile:multipartFiles){
                final OssResult ossResult = ossTemplate.uploadFile(multipartFile, multipartFile.getOriginalFilename());
                list.add(ossResult);
            }
        }
        return list;
    }
}
