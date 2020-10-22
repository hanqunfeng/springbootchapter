package com.example.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotEmpty;
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
     * 在该参数的get方法上用 @JsonIgnore 然后在set方法上面用@JsonProperty 即可解决返回json时没有该属性 但是能接收到值
    */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //返回json时不显示该字段
    @JsonIgnore
    public Long getId() {
        return id;
    }

    //从json获取数据时可以正常获取
    @JsonProperty
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 标题
    */
    //数据校验
    @NotEmpty(message = "文章标题不能为空")
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
    //日期字符串格式化转换
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    @Column(name = "publishDate")
    private LocalDateTime publishDate;
}
