package com.example.swagger3mvndemo.controller;


import com.example.swagger3mvndemo.model.Artical;
import com.example.swagger3mvndemo.response.CommonResponse;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * <h1>文章处理器</h1>
 * Created by hanqf on 2020/10/15 16:27.
 */

@RestController
@RequestMapping("/actical")
public class ArticalController {


    @GetMapping(value = "/{id}")
    public CommonResponse findArticalById(@PathVariable(value = "id") Long id){
        Artical artical = Artical.builder().author("司马迁").id(id).title("资治通鉴").pageNum(20000).publishDate(LocalDate.now()).build();
        return CommonResponse.success("获取文章成功",artical);
    }

    @PostMapping
    public CommonResponse saveArtical(@RequestBody Artical artical){
        return CommonResponse.success("保存文章成功",artical);
    }

    @PutMapping
    public CommonResponse updateArtical(@RequestBody Artical artical){
        if(artical.getId()==null){
            return CommonResponse.fail("文章Id不能为空");
        }
        return CommonResponse.success("保存文章成功",artical);
    }

    @DeleteMapping("/{id}")
    public CommonResponse deleteArticalById(@PathVariable(value = "id") Long id){
        return CommonResponse.success("删除文章成功",id);
    }
}
