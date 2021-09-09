package com.example.demo;

import com.example.jpa.BaseJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * Description: <conf模块JpaRepository >. <br>
 * <p>
 * </p>
 * generate time:2021-8-27 15:47:30
 *
 * @author hanqf
 * @version V1.0
 */
public interface CountryJpaRepository extends BaseJpaRepository<Country, Long> {
    //sql查询，只有SELECT * 时才可以直接返回@Entity对象，但要有无参构造方法，page要指定countQuery
    @Query(value = "SELECT * FROM tbl_country WHERE id > ?1", countQuery = "SELECT count(*) FROM tbl_country WHERE id > ?1", nativeQuery = true)
    Page<Country> findByIdAfter(Long id, Pageable pageable);

    //sql查询，返回@Entity对象，要在@Entity中创建对应的构造方法
    @Query(value = "SELECT * FROM tbl_country  WHERE name_zh = ?1",nativeQuery = true)
    Country findCountrySqlByName(String name);

    //hql查询部分属性时，返回@Entity对象，要在@Entity中创建对应的构造方法
    @Query(value = "SELECT new Country(id,nameZh,nameEn) FROM Country WHERE nameZh = ?1")
    Country findCountryHqlByName(String name);

    //hql查询部分属性时，返回非@Entity对象，要创建对应的构造方法，且必须使用全路径
    @Query(value = "SELECT new com.example.demo.CountryDto(id,nameZh,nameEn) FROM Country WHERE nameZh = ?1")
    CountryDto findCountryDtoNewHqlByName(String name);

    //hql查询部分属性时，一定有使用as别名，这里返回值不能是@Entity，并且其要标注@JpaDto注解
    @Query(value = "SELECT id as id,nameZh as nameZh,nameEn as nameEn FROM Country WHERE nameZh = ?1")
    CountryDto findCountryDtoHqlByName(String name);

    //sql查询部分属性时，一定有使用as别名，这里返回值不能是@Entity，并且其要标注@JpaDto注解
    @Query(value = "SELECT id,name_zh as nameZh,name_en as nameEn FROM tbl_country WHERE name_zh = ?1",nativeQuery = true)
    CountryDto findCountryDtoSqlByName(String name);

    //sql查询部分属性时，一定有使用as别名，这里返回值不能是@Entity，并且其要标注@JpaDto注解，page要指定countQuery
    @Query(value = "SELECT id,name_zh as nameZh,name_en as nameEn FROM tbl_country WHERE id > ?1", countQuery = "SELECT count(*) FROM tbl_country WHERE id > ?1", nativeQuery = true)
    Page<CountryDto> findByIdAfterDto(Long id, Pageable pageable);


    @Query(name = "HQL_FIND_ALL_COUNTRY")
    List<Country> nameHqlFindAll();

    @Query(name = "SQL_FIND_ALL_COUNTRY")
    List<Country> nameSqlFindAll();

    @Query(name = "SQL_FIND_ALL_COUNTRY2")
    List<Country> nameSqlFindAll2();

    @Query(name = "SQL_FIND_SOME_FIELD_COUNTRY")
    List<Country> nameSqlFindSomeField();

}
