package com.example.demo;


import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Description: <Country vo>. <br>
 * <p>
 * generate time:2021-8-27 15:47:30
 *
 * @author hanqf
 * @version V1.0
 */
@NamedQueries({
        @NamedQuery(
                //指定名称，必须全局唯一
                name = "HQL_FIND_ALL_COUNTRY",
                //要指定的hql
                query = "from Country"
        )
})
@NamedNativeQueries({
        @NamedNativeQuery(
                //指定名称，必须全局唯一
                name = "SQL_FIND_ALL_COUNTRY",
                //要指定的sql
                query = "select * from tbl_country",
                //指定返回类型
                resultClass = Country.class
        ),
        @NamedNativeQuery(
                //指定名称，必须全局唯一
                name = "SQL_FIND_ALL_COUNTRY2",
                //要指定的sql
                query = "select * from tbl_country",
                //通过@SqlResultSetMapping封装返回类型，这里是@SqlResultSetMapping的名称
                resultSetMapping = "SQL_RESULT_COUNTRY_ALL"
        ),
        @NamedNativeQuery(
                //指定名称，必须全局唯一
                name = "SQL_FIND_SOME_FIELD_COUNTRY",
                //要指定的sql
                query = "select id,name_zh as nameZh,name_en as nameEn from tbl_country",
                //通过@SqlResultSetMapping封装返回类型，这里是@SqlResultSetMapping的名称
                resultSetMapping = "SQL_RESULT_COUNTRY_SOME_FIELD"
        )
})
@SqlResultSetMappings({
        @SqlResultSetMapping(
                //指定名称，必须全局唯一
                name = "SQL_RESULT_COUNTRY_ALL",
                //指定返回类的@Entity类型
                entities = {
                        @EntityResult(
                                //指定返回类的@Entity类型
                                entityClass = Country.class
                        )
                }

        ),
        @SqlResultSetMapping(
                //指定名称，必须全局唯一
                name = "SQL_RESULT_COUNTRY_SOME_FIELD",
                //查询部分属性时，可以通过构造方法封装结果数据
                classes = {
                        @ConstructorResult(
                                //指定返回类的@Entity类型
                                targetClass = Country.class,
                                //指定调用的构造方法
                                columns = {
                                        @ColumnResult(name = "id", type = Long.class),
                                        @ColumnResult(name = "nameZh"),
                                        @ColumnResult(name = "nameEn")
                                }
                        )
                }
        )
})
@Data
@Entity
@Table(name = "tbl_country")
public class Country implements Serializable {
    private static final long serialVersionUID = 1L;

    /*
     * 主键自增
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;


    /*
     * 中文简称
     */
    @Column(name = "name_zh")
    private String nameZh;


    /*
     * 英文简称
     */
    @Column(name = "name_en")
    private String nameEn;


    /*
     * 英文全称
     */
    @Column(name = "name_en_full")
    private String nameEnFull;


    /*
     * 两字母代码
     */
    @Column(name = "code_two")
    private String codeTwo;


    /*
     * 三字母代码
     */
    @Column(name = "code_three")
    private String codeThree;


    /*
     * 数字代码
     */
    @Column(name = "num_code")
    private String numCode;


    /*
     * 备注
     */
    @Column(name = "remark")
    private String remark;


    public Country(Long id, String nameZh, String nameEn) {
        this.id = id;
        this.nameZh = nameZh;
        this.nameEn = nameEn;
    }

    public Country() {
    }

}



