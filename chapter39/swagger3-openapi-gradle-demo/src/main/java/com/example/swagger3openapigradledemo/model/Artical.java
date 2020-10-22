package com.example.swagger3openapigradledemo.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * <h1>文章对象</h1>
 * Created by hanqf on 2020/10/15 16:24.
 */
@Data
@Table(name="tbl_artical")
@Entity
public class Artical {
    /**
     * ID
    */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 标题
    */
    @Column(name = "title")
    private String title;
    
    /**
     * 作者
    */
    @Column(name = "author")
    private String author;
    
    /**
     * 页数
    */
    @Column(name = "pageNum")
    private Integer pageNum;
    
    /**
     * 发布日期
    */
    @Schema(required = true,pattern="yyyy-MM-dd HH:mm:ss",example = "2018-10-01 12:18:48")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "publishDate")
    private LocalDateTime publishDate;
}
