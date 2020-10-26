package com.example.controller;

import com.example.model.Artical;
import com.example.service.ArticalServcie;
import com.example.views.CustomPage;
import com.example.views.CustomSort;
import com.example.views.ModelView;
import com.example.views.PageList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
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
    private ArticalServcie articalServcie;

    @ModelView
    @GetMapping
    public String findArticals(Model model) {
        //异常测试
        //int i = 1 / 0;
        List<Artical> articals = articalServcie.findAll();
        model.addAttribute("articals",articals);
        return "views/atricals";
    }

    @ModelView
    @RequestMapping("/pages")
    public String findArticalsByPages(Model model, @ModelAttribute("_queryBean")Artical artical,@ModelAttribute("_pageBean") CustomPage page, @ModelAttribute("_sorter") CustomSort sort){
        PageList<Artical> pageList = articalServcie.findAll(artical, page, sort);
        page.setTotal(pageList.getTotal());
        model.addAttribute("articals",pageList.getData());
        return "views/atricals";
    }
}
