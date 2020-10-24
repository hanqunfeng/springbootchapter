package com.example.controller;

import com.example.dao.ArticalRepository;
import com.example.model.Artical;
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

    @GetMapping
    public String findArticals(Model model) {
        List<Artical> articals = articalRepository.findAll();
        model.addAttribute("articals",articals);
        return "views/atricals";
    }
}
