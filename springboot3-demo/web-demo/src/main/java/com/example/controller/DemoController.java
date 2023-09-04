package com.example.controller;

import com.example.function.dao.UserInfo;
import com.example.function.dao.UserInfoJpaRepository;
import com.example.support.ApplicationContextProvider;
import com.example.support.jpa.DefaultJpaService;
import jakarta.persistence.EntityManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;

/**
 * <h1>demo</h1>
 * Created by hanqf on 2023/8/11 10:08.
 */

@Slf4j
@Controller
public class DemoController {

    @Autowired
    private UserInfoJpaRepository userInfoJpaRepository;

    @Autowired
    @Qualifier("entityManager")
    private EntityManager entityManager;

    @RequestMapping(value = {"/", "/index.do"})
    public String handleIndex(Model model, HttpServletRequest request) {
        log.info("handleIndex");
//        int a = 5/0;
        final String message = ApplicationContextProvider.getMessage("common.menu");
        model.addAttribute("message", message);
        return "index";
    }

    @RequestMapping(value = {"/auth.do"})
    public String handleAuth(Model model, HttpServletRequest request) {
        log.info("handleAuth");
//        int a = 5/0;
        final String message = ApplicationContextProvider.getMessage("common.menu");
        model.addAttribute("message", message);

        final List<UserInfo> userInfos = userInfoJpaRepository.findAll();
        model.addAttribute("userInfos",userInfos);

        final Map bySqlFirst = DefaultJpaService.builder().entityManager(entityManager).build()
                .findBySqlFirst("select * from tbl_dream_userinfo where id = 1");

        //这里要注意查询字段与model对象的匹配，可以使用as，也可以在model对象的属性加上 @JsonProperty("device_id")进行绑定
        final UserInfo userInfo = DefaultJpaService.builder().build() // 默认主数据源
                .findBySqlFirst("select * from tbl_dream_userinfo where id = 1", UserInfo.class);

        return "index";
    }
}

