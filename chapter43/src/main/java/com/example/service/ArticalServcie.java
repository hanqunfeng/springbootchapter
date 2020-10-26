package com.example.service;

import com.example.model.Artical;
import com.example.views.CustomPage;
import com.example.views.CustomSort;
import org.springframework.data.domain.Page;

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

    /**
     * <h2>分页查询</h2>
     * Created by hanqf on 2020/10/26 14:22. <br>
     *
     * @param artical 查询条件
     * @param page 分页参数
     * @param sort 排序参数
     * @return org.springframework.data.domain.Page&lt;com.example.model.Artical&gt;
     * @author hanqf
     */
    Page<Artical> findAll(Artical artical, CustomPage page, CustomSort sort);
}
