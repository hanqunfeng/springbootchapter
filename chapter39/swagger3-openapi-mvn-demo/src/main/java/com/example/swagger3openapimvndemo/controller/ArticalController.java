package com.example.swagger3openapimvndemo.controller;


import com.example.swagger3openapimvndemo.model.Artical;
import com.example.swagger3openapimvndemo.response.CommonResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDate;
import java.util.Enumeration;
import java.util.Random;

/**
 * <h1>文章处理器</h1>
 * Created by hanqf on 2020/10/15 16:27.
 */

@RestController
//需要安全认证，这里只是提示，并不会不让测试，需要代码里设置认证拦截，该注解也可以放在方法上，表示只对某个方法进行验证
//@SecurityRequirement(name = "apiKey")
@SecurityRequirement(name = "api")
@RequestMapping("/artical")
public class ArticalController {


    @GetMapping(value = "/{id}")
    public CommonResponse findArticalById(@PathVariable(value = "id") Long id) {
        Artical artical = Artical.builder().author("司马迁").id(id).title("资治通鉴").pageNum(20000).publishDate(LocalDate.now()).build();
        return CommonResponse.success("获取文章成功", artical);
    }

    @PostMapping
    public CommonResponse saveArtical(@RequestBody Artical artical, HttpServletRequest request) {
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            System.out.println(header + ":" + request.getHeader(header));
        }

        String authorization = request.getHeader("token");
        if ("security".equals(authorization)) {
            artical.setId(new Random().nextLong());
            return CommonResponse.success("保存文章成功", artical);
        } else {
            return CommonResponse.fail("认证失败");
        }
    }

    @PutMapping
    public CommonResponse updateArtical(@RequestBody Artical artical) {
        if (artical.getId() == null) {
            return CommonResponse.fail("文章Id不能为空");
        }
        return CommonResponse.success("保存文章成功", artical);
    }

    @DeleteMapping("/{id}")
    public CommonResponse deleteArticalById(@PathVariable(value = "id") Long id) {
        return CommonResponse.success("删除文章成功", id);
    }
}
