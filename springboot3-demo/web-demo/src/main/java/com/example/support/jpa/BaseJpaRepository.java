package com.example.support.jpa;


import jakarta.persistence.EntityManager;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

/**
 * ${DESCRIPTION}
 */

@NoRepositoryBean //接口不参与jpa的代理
public interface BaseJpaRepository<T, ID extends Serializable> extends JpaRepository<T, ID>, JpaSpecificationExecutor<T>, Serializable {

    EntityManager getEntityManager();

    T findByIdNew(ID id);

    /**
     * 批量插入
     */
    <S extends T> List<S> batchSave(Iterable<S> iterable);

    /**
     * 批量更新
     */
    <S extends T> List<S> batchUpdate(Iterable<S> iterable);


    void lazyInitialize(Class<T> entityClazz, List<T> l, String[] fields);

    void lazyInitialize(T obj, String[] fields);


}
