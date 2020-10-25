package com.example.service;

import com.example.dao.ArticalRepository;
import com.example.exception.CustomException;
import com.example.exception.CustomExceptionType;
import com.example.model.Artical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

/**
 * <h1>文章service</h1>
 * Created by hanqf on 2020/10/25 10:22.
 */

@Service
@Transactional
public class ArticalServcie {
    @Autowired
    private ArticalRepository articalRepository;

    public Artical findById(Long id){
        Artical artical = articalRepository.findById(id).orElse(null);
        if (artical == null) {
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR,"给定id文章不存在!");
        }
        return artical;
    }

    public List<Artical> findAll(){
        List<Artical> articals = articalRepository.findAll();
        return articals;
    }

    public Artical save(Artical artical){
        if (artical.getId() != null) {
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR,"新增操作，文章Id不能赋值，修改操作请使用put method!");
        }
        return articalRepository.save(artical);
    }

    public Artical update(Artical artical){
        if (artical.getId() == null) {
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR,"修改操作，文章Id不能为空!");
        }
        return articalRepository.save(artical);
    }

    public void deleteById(Long id){
        articalRepository.deleteById(id);
    }
}
