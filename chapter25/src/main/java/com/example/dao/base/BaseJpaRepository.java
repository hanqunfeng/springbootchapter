package com.example.dao.base;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean //接口不参与jpa的代理
public interface BaseJpaRepository<T,ID extends Serializable> extends JpaRepository<T,ID>, JpaSpecificationExecutor<T>, Serializable {

    //查询hql
    List<?> findByHql(String hql);

    //根据id查询对象
    T findByIdNew(ID id);

    //针对list，对象类型entityClazz，如果查询出的某些属性没有封装，则通过该方法进行封装
    public void lazyInitialize(Class<T> entityClazz, List<T> l, String... fields);

    //针对对象，如果查询出的某些属性没有封装，则通过该方法进行封装
    public void lazyInitialize(T obj, String... fields);
}
