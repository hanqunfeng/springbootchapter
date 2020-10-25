package com.example.service.impl;

import com.example.dao.ArticalRepository;
import com.example.exception.CustomException;
import com.example.exception.CustomExceptionType;
import com.example.model.Artical;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <h1>文章service</h1>
 * Created by hanqf on 2020/10/25 10:22.
 */

@Service
@Transactional(rollbackFor = {Exception.class })
//@Transactional(rollbackOn = {Exception.class})
//该注解的作用是表明此类上所有方法上的事务都是CGLib方式代理的。实际上spring会自动判断其代理方式。
//@Scope(proxyMode = ScopedProxyMode.TARGET_CLASS)
public class ArticalServcieImpl  {
    @Autowired
    private ArticalRepository articalRepository;


    //@Transactional(value = Transactional.TxType.SUPPORTS)
    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public Artical findById(Long id){
        Artical artical = articalRepository.findById(id).orElse(null);
        if (artical == null) {
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR,"给定id文章不存在!");
        }
        return artical;
    }


    //@Transactional(value = Transactional.TxType.SUPPORTS)
    @Transactional(readOnly = true,propagation = Propagation.SUPPORTS)
    public List<Artical> findAll(){
        return articalRepository.findAll();
    }



    public Artical save(Artical artical){
        if (artical.getId() != null) {
            throw new CustomException(CustomExceptionType.USER_INPUT_ERROR,"新增操作，文章Id不能赋值，修改操作请使用put method!");
        }
        artical = articalRepository.save(artical);
        return artical;
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
