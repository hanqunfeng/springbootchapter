package com.example.controller;

import com.example.dao.ArticalRepository;
import com.example.model.Artical;
import com.example.views.ModelView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * <h1>视图controller</h1>
 * Created by hanqf on 2020/10/23 17:43.
 */

@Controller
@RequestMapping("/view/articals")
public class ArticalViewController {

    @Autowired
    private ArticalRepository articalRepository;

    @ModelView
    @GetMapping
    public String findArticals(Model model) {
        //异常测试
        //int i = 1 / 0;
        List<Artical> articals = articalRepository.findAll();
        model.addAttribute("articals",articals);
        return "views/atricals";
    }
}
