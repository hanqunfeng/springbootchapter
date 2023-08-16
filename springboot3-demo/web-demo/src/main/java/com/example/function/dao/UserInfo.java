
package com.example.function.dao;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.context.annotation.Lazy;

import java.io.Serializable;

/**
 * Description: <用户信息表>. <br>
 *
 * generate time:2023-6-5 14:27:25
 *
 * @author hanqf
 * @version V1.0
 */
@Data
@Entity
@Table(name = "tbl_dream_userinfo")
@Lazy(value=true)
public class UserInfo implements Serializable{
	private static final long serialVersionUID = 1L;

	/*
	 * 主键自增
	 */
	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="id")
	private Long id;




	/*
	 * 设备id
	 */
	@Column(name="device_id")
	@JsonProperty("device_id")
	private String deviceId;







	/*
	 * 创建时间
	 */
	@Column(name="create_time")
	private java.time.LocalDateTime createTime;


}



