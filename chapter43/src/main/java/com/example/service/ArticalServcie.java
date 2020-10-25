package com.example.service;

import com.example.model.Artical;

import java.util.List;

/**
 * <h1>文章service接口</h1>
 * Created by hanqf on 2020/10/25 13:13.
 */

public interface ArticalServcie {

     Artical findById(Long id);

     List<Artical> findAll();

     Artical save(Artical artical);

     Artical update(Artical artical);

     void deleteById(Long id);
}
