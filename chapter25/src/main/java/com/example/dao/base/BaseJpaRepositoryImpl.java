package com.example.dao.base;

import org.hibernate.Hibernate;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;


public class BaseJpaRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseJpaRepository<T, ID> {

    //通过构造方法初始化EntityManager
    private final EntityManager entityManager;


    public BaseJpaRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public List<?> findByHql(String hql) {
        return (List<?>)entityManager.createQuery(hql).getResultList();
    }

    @Override
    public T findByIdNew(ID id) {
        T t = null;
        Optional<T> optional = this.findById(id);
        if(optional.isPresent()) {
            t = optional.get();
        }

        return t;

    }

    @Override
    public void lazyInitialize(Class<T> entityClazz, List<T> l, String... fields) {
        if (fields != null) {
            for (String field : fields) {

                String targetMethod = "get" + upperFirstWord(field);

                Method method;
                try {
                    method = entityClazz.getDeclaredMethod(targetMethod);
                    for (T o : l) {
                        Hibernate.initialize(method.invoke(o));
                    }
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    public void lazyInitialize(T obj,
                                String... fields) {
        if (fields != null) {
            for (String field : fields) {

                String targetMethod = "get" + upperFirstWord(field);

                Method method;
                try {
                    method = obj.getClass().getDeclaredMethod(targetMethod);
                    Hibernate.initialize(method.invoke(obj));
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    private String upperFirstWord(String str) {
        StringBuffer sb = new StringBuffer(str);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }
}