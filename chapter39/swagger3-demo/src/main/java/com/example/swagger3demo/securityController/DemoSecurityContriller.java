package com.example.swagger3demo.securityController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 * Created by hanqf on 2020/10/9 15:43.
 */
@Api(tags = "安全认证接口-DemoSecurityContriller")
@RestController
@RequestMapping("/security")
public class DemoSecurityContriller {


    @Operation(summary = "demoSecurity", description = "swagger api demo security")
    @PostMapping("/demoMap")
    public Map<String, String> demoMap(HttpServletRequest request, @Parameter(description = "姓名", example = "张三") @RequestParam(required = true) String name) {

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

    @ApiImplicitParams({
            @ApiImplicitParam(name="userid",value="用户id",dataTypeClass = Long.class, paramType = "query",example="12345"),
            @ApiImplicitParam(name="goodsid",value="商品id",dataTypeClass = Integer.class, paramType = "query",example="12345"),
            @ApiImplicitParam(name="mobile",value="手机号",dataTypeClass = String.class, paramType = "query",example="13866668888"),
            @ApiImplicitParam(name="comment",value="发货备注",dataTypeClass = String.class, paramType = "query",example="请在情人节当天送到")
    })
    @Operation(summary = "提交订单")
    @PostMapping("/order")
    public Map<String, String> demoMap(HttpServletRequest request,@ApiIgnore @RequestParam Map<String,String> params) {

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
}
