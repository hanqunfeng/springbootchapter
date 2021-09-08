
package com.example.demo;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Description: <Country vo>. <br>
 *
 * generate time:2021-8-27 15:47:30
 *
 * @author hanqf
 * @version V1.0
 */
@Data
@Entity
@Table(name = "tbl_country")
public class Country implements Serializable{
	private static final long serialVersionUID = 1L;

	/*
	 * 主键自增
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;




	/*
	 * 中文简称
	 */
	@Column(name="name_zh")
	private String nameZh;




	/*
	 * 英文简称
	 */
	@Column(name="name_en")
	private String nameEn;




	/*
	 * 英文全称
	 */
	@Column(name="name_en_full")
	private String nameEnFull;




	/*
	 * 两字母代码
	 */
	@Column(name="code_two")
	private String codeTwo;




	/*
	 * 三字母代码
	 */
	@Column(name="code_three")
	private String codeThree;




	/*
	 * 数字代码
	 */
	@Column(name="num_code")
	private String numCode;




	/*
	 * 备注
	 */
	@Column(name="remark")
	private String remark;


	public Country(Long id, String nameZh, String nameEn) {
		this.id = id;
		this.nameZh = nameZh;
		this.nameEn = nameEn;
	}

	public Country() {
	}

}



