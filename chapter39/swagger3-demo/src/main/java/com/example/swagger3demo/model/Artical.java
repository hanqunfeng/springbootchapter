package com.example.swagger3demo.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

/**
 * <h1>文章对象</h1>
 * Created by hanqf on 2020/10/15 16:24.
 */

@Data
@Builder
public class Artical {
    /**
     * ID
    */
    private Long id;
    
    /**
     * 标题
    */
    private String title;
    
    /**
     * 作者
    */
    private String author;
    
    /**
     * 页数
    */
    private Integer pageNum;
    
    /**
     * 发布日期
    */
    private LocalDate publishDate;
}
