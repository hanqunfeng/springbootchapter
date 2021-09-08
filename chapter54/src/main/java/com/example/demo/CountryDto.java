package com.example.demo;


import com.example.jpa.JpaDto;
import lombok.Data;

import java.io.Serializable;

/**
 * Description: <CountryDto vo>. <br>
 * <p>
 * generate time:2021-8-27 15:47:30
 *
 * @author hanqf
 * @version V1.0
 */
@JpaDto
@Data
public class CountryDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /*
     * 主键自增
     */
    private Long id;


    /*
     * 中文简称
     */
    private String nameZh;


    /*
     * 英文简称
     */
    private String nameEn;


    /*
     * 英文全称
     */
    private String nameEnFull;


    /*
     * 两字母代码
     */
    private String codeTwo;


    /*
     * 三字母代码
     */
    private String codeThree;


    /*
     * 数字代码
     */
    private String numCode;


    /*
     * 备注
     */
    private String remark;


    public CountryDto() {
    }

    public CountryDto(Long id, String nameZh, String nameEn) {
        this.id = id;
        this.nameZh = nameZh;
        this.nameEn = nameEn;
    }


}



