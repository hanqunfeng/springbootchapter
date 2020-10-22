package com.example.swagger3openapigradledemo.controller;


import com.example.swagger3openapigradledemo.dao.ArticalRepository;
import com.example.swagger3openapigradledemo.model.Artical;
import com.example.swagger3openapigradledemo.response.CommonResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <h1>文章处理器</h1>
 * Created by hanqf on 2020/10/15 16:27.
 */

@RestController
@RequestMapping("/demo/articals")
public class ArticalController {

    @Autowired
    private ArticalRepository articalRepository;


    @GetMapping(value = "/{id}")
    public CommonResponse findArticalById(@PathVariable(value = "id") Long id) {
        Artical artical = articalRepository.findById(id).orElse(null);
        if (artical == null) {
            return CommonResponse.err("文章不存在");
        }
        return CommonResponse.success("获取文章成功", artical);
    }

    @PostMapping
    public CommonResponse saveArtical(@RequestBody Artical artical) {
        articalRepository.save(artical);
        return CommonResponse.success("保存文章成功", artical);

    }

    @PutMapping
    public CommonResponse updateArtical(@RequestBody Artical artical) {
        if (artical.getId() == null) {
            return CommonResponse.err("文章Id不能为空");
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
