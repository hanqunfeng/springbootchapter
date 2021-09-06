
package com.example.demo;


import com.example.jpa.BaseDomain;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "tbl_demo")
public class DemoEntity extends BaseDomain implements Serializable{
	private static final long serialVersionUID = 1L;


	/*
	 * 名称
	 */
	@Column(name="name")
	private String name;


}



