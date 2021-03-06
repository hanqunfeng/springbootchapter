package com.example.swagger3mvndemo.securityController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * <h1>一个需要认证的demo</h1>
 * Create on  2020/10/9 15:43.
 *
 * @author hanqf
 * @version 1.0
 */
@Api(tags = "安全认证接口-DemoSecurityContriller")
@RestController
@RequestMapping("/security")
public class DemoSecurityContriller {


    /**
     * <h2>demoMap 一个例子</h2>
     * Create on 2020/10/10 15:52
     *
     * @param request HttpServletRequest
     * @param name    姓名
     * @return java.util.Map&lt;java.lang.String, java.lang.String&gt; 封装对象map
     * @author hanqf
     */
    @Operation(summary = "demoSecurity", description = "swagger api demo security")
    @PostMapping("/demoMap")
    public Map<String, String> demoMap(HttpServletRequest request, @Parameter(description = "姓名", example = "张三") @RequestParam(required = false) String name){

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            System.out.println(header + ":" + request.getHeader(header));
        }

        Map<String, String> map = new HashMap<>();
        //String authorization = request.getHeader("Authorization");
        String authorization = request.getHeader("token");
        if ("security".equals(authorization)) {
            map.put("name", name);
            map.put("age", "33");
            return map;
        } else {
            map.put("Authorization", "fail");
            return map;
        }


    }

    /**
     * <h2>提交订单</h2>
     * Create on  2020/10/10 15:54
     *
     * @param request HttpServletRequest
     * @param params  订单map
     * @return java.util.Map&lt;java.lang.String, java.lang.String&gt;
     * @author hanqf
     */
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userid", value = "用户id", dataTypeClass = Long.class, paramType = "query", example = "12345"),
            @ApiImplicitParam(name = "goodsid", value = "商品id", dataTypeClass = Integer.class, paramType = "query", example = "12345"),
            @ApiImplicitParam(name = "mobile", value = "手机号", dataTypeClass = String.class, paramType = "query", example = "13866668888"),
            @ApiImplicitParam(name = "comment", value = "发货备注", dataTypeClass = String.class, paramType = "query", example = "请在情人节当天送到")
    })
    @Operation(summary = "提交订单")
    @PostMapping("/order")
    public Map<String, String> demoMap(HttpServletRequest request, @ApiIgnore @RequestParam Map<String, String> params) {

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            System.out.println(header + ":" + request.getHeader(header));
        }

        Map<String, String> map = new HashMap<>();
        //String authorization = request.getHeader("Authorization");
        String authorization = request.getHeader("token");
        if ("security".equals(authorization)) {
            return params;
        } else {
            map.put("Authorization", "fail");
            return map;
        }


    }

    /**
     * @param request
     * @param params
     * @return
     */
    @Operation(summary = "json测试")
    @PostMapping(value = "/json", headers = "Content-Type=application/json;charset=utf8")
    public Map<String, String> demoJson(HttpServletRequest request, @RequestBody Map<String, String> params){

        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String header = headerNames.nextElement();
            System.out.println(header + ":" + request.getHeader(header));
        }

        Map<String, String> map = new HashMap<>();
        //String authorization = request.getHeader("Authorization");
        String authorization = request.getHeader("token");
        if ("security".equals(authorization)) {
            return params;
        } else {
            map.put("Authorization", "fail");
            return map;
        }


    }

    
    @Operation(summary = "上传附件测试")
    @PostMapping(value = "/files", consumes = "multipart/*", headers = "Content-Type=multipart/form-data")
    public Map<String, String> fileUpload(@Parameter(description = "上传图片", required = true, in = ParameterIn.QUERY) @RequestParam(required = true) MultipartFile multipartFile) {
        Map<String, String> map = new HashMap<>();

        System.out.println(multipartFile.getOriginalFilename());
        return map;


    }

}
