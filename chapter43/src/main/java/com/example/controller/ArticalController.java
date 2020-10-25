package com.example.controller;


import com.example.model.Artical;
import com.example.response.CommonResponse;
import com.example.service.ArticalServcie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * <h1>文章处理器</h1>
 * Created by hanqf on 2020/10/15 16:27.
 */

@RestController
@RequestMapping("/json/articals")
public class ArticalController {

    @Autowired
    private ArticalServcie articalServcie;


    @GetMapping(value = "/{id}")
    public CommonResponse findArticalById(@PathVariable(value = "id") Long id) {
        Artical artical = articalServcie.findById(id);
        return CommonResponse.success("获取文章成功", artical);
    }

    @GetMapping
    public CommonResponse findArticals() {
        List<Artical> articals = articalServcie.findAll();
        return CommonResponse.success("获取文章列表成功", articals);
    }

    @PostMapping
    //开启数据校验
    public CommonResponse saveArtical(@Valid @RequestBody Artical artical) {
        artical = articalServcie.save(artical);
        return CommonResponse.success("保存文章成功", artical);

    }

    @PutMapping
    public CommonResponse updateArtical(@RequestBody Artical artical) {
        artical = articalServcie.update(artical);
        return CommonResponse.success("保存文章成功", artical);
    }

    @DeleteMapping("/{id}")
    public CommonResponse deleteArticalById(@PathVariable(value = "id") Long id) {
        articalServcie.deleteById(id);
        return CommonResponse.success("删除文章成功", id);
    }
}
