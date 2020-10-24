package com.example.controller;


import com.example.dao.ArticalRepository;
import com.example.exception.CustomException;
import com.example.exception.CustomExceptionType;
import com.example.model.Artical;
import com.example.response.CommonResponse;
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
    private ArticalRepository articalRepository;


    @GetMapping(value = "/{id}")
    public CommonResponse findArticalById(@PathVariable(value = "id") Long id) {
        Artical artical = articalRepository.findById(id).orElse(null);
        if (artical == null) {
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR,"给定id文章不存在!");
        }
        return CommonResponse.success("获取文章成功", artical);
    }

    @GetMapping
    public CommonResponse findArticals() {
        List<Artical> articals = articalRepository.findAll();
        return CommonResponse.success("获取文章列表成功", articals);
    }

    @PostMapping
    //开启数据校验
    public CommonResponse saveArtical(@Valid @RequestBody Artical artical) {
        if (artical.getId() != null) {
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR,"新增操作，文章Id不能赋值，修改操作请使用put method!");
        }
        articalRepository.save(artical);
        return CommonResponse.success("保存文章成功", artical);

    }

    @PutMapping
    public CommonResponse updateArtical(@RequestBody Artical artical) {
        if (artical.getId() == null) {
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR,"修改操作，文章Id不能为空!");
        }
        articalRepository.save(artical);
        return CommonResponse.success("保存文章成功", artical);
    }

    @DeleteMapping("/{id}")
    public CommonResponse deleteArticalById(@PathVariable(value = "id") Long id) {
        articalRepository.deleteById(id);
        return CommonResponse.success("删除文章成功", id);
    }
}
