package com.example.jpa;

import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * <h1>主键是uuid的model基类</h1>
 * Created by hanqf on 2021/8/5 10:15.
 *
 * CustomVersionOneStrategy : uuid生成方法1的变种，基于时间戳，mac地址和进程号
 * 7f000001-7b14-143c-817b-14243cb803e5
 * 7f000001-7b14-1bc3-817b-142bc40103e7
 */

@MappedSuperclass
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class BaseDomain {
    @Id
    @GenericGenerator(
            name = "uuid",
            strategy = "uuid",
            parameters = {@org.hibernate.annotations.Parameter(
                    name = "uuid_gen_strategy_class",
                    value = "org.hibernate.id.uuid.CustomVersionOneStrategy")
            })
    @GeneratedValue(generator = "uuid")
    private String id;


    @Column
    private LocalDateTime createTime;
    @Column
    private LocalDateTime modifyTime;

    public void initInsert() {
        this.createTime = LocalDateTime.now();
        this.modifyTime = LocalDateTime.now();
    }

    public void initUpdate() {
        this.modifyTime = LocalDateTime.now();
    }
}
