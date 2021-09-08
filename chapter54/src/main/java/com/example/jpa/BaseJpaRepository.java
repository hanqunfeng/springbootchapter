package com.example.jpa;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * ${DESCRIPTION}
 */

@NoRepositoryBean //接口不参与jpa的代理
public interface BaseJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T>, Serializable {

    EntityManager getEntityManager();

    <E> List<E> findByHql(String hql);


    List<Map> findBySql(String sql);

    List<Map> findBySql(String sql, Object[] params);

    List<Map> findBySql(String sql, Map<String, Object> params);


    Map findBySqlFirst(String sql);

    Map findBySqlFirst(String sql, Object[] params);

    Map findBySqlFirst(String sql, Map<String, Object> params);

    /**
     * basic == true 表示基本数据类型
     */
    <E> List<E> findBySql(String sql, Class clazz, boolean basic);

    <E> List<E> findBySql(String sql, Class clazz, boolean basic, Object[] params);

    <E> List<E> findBySql(String sql, Class clazz, boolean basic, Map<String, Object> params);

    /**
     * 分页查询
     */
    <E> Page<E> findPageBySql(String sql, Pageable pageable, Class clazz, boolean basic);

    <E> Page<E> findPageBySql(String sql, String countSql, Pageable pageable, Class clazz, boolean basic);

    <E> Page<E> findPageBySql(String sql, Pageable pageable, Class clazz, boolean basic, Object[] params);

    <E> Page<E> findPageBySql(String sql, String countSql, Pageable pageable, Class clazz, boolean basic, Object[] params);

    <E> Page<E> findPageBySql(String sql, Pageable pageable, Class clazz, boolean basic, Map<String, Object> params);

    <E> Page<E> findPageBySql(String sql, String countSql, Pageable pageable, Class clazz, boolean basic, Map<String, Object> params);

    /**
     * basic == true 表示基本数据类型
     */
    <E> E findBySqlFirst(String sql, Class clazz, boolean basic);

    <E> E findBySqlFirst(String sql, Class clazz, boolean basic, Object[] params);

    <E> E findBySqlFirst(String sql, Class clazz, boolean basic, Map<String, Object> params);

    T findByIdNew(ID id);

    /**
     * 批量插入
     */
    <S extends T> Iterable<S> batchSave(Iterable<S> iterable);

    /**
     * 批量更新
     */
    <S extends T> Iterable<S> batchUpdate(Iterable<S> iterable);


    void lazyInitialize(Class<T> entityClazz, List<T> l, String[] fields);

    void lazyInitialize(T obj, String[] fields);


}
