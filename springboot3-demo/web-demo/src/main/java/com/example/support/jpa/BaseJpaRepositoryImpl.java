package com.example.support.jpa;


import com.example.support.ApplicationContextProvider;
import jakarta.persistence.EntityManager;
import org.hibernate.Hibernate;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * ${DESCRIPTION}
 */


public class BaseJpaRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID> implements BaseJpaRepository<T, ID> {

    //批量更新时的阀值，每500条数据commit一次
    private static final Integer BATCH_SIZE = 500;


    //通过构造方法初始化EntityManager
    private final EntityManager entityManager;

    public BaseJpaRepositoryImpl(JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }


    @Override
    public EntityManager getEntityManager() {
        return entityManager;
    }


    @Override
    public T findByIdNew(ID id) {
        T t = null;

        if (id == null) {
            return null;
        }

        Optional<T> optional = this.findById(id);
        if (optional.isPresent()) {
            t = optional.get();
        }

        return t;

    }


    @Override
    @Transactional
    public <S extends T> List<S> batchSave(Iterable<S> iterable) {
        Iterator<S> iterator = iterable.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            entityManager.persist(iterator.next());
            index++;
            if (index % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        if (index % BATCH_SIZE != 0) {
            entityManager.flush();
            entityManager.clear();
        }
        return StreamSupport.stream(iterable.spliterator(), true).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public <S extends T> List<S> batchUpdate(Iterable<S> iterable) {
        Iterator<S> iterator = iterable.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            entityManager.merge(iterator.next());
            index++;
            if (index % BATCH_SIZE == 0) {
                entityManager.flush();
                entityManager.clear();
            }
        }
        if (index % BATCH_SIZE != 0) {
            entityManager.flush();
            entityManager.clear();
        }
        return StreamSupport.stream(iterable.spliterator(), true).collect(Collectors.toList());
    }


    @Override
    public void lazyInitialize(Class<T> entityClazz, List<T> l, String[] fields) {
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
    public void lazyInitialize(T obj, String[] fields) {
        if (obj != null) {
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
    }

    private String upperFirstWord(String str) {
        StringBuffer sb = new StringBuffer(str);
        sb.setCharAt(0, Character.toUpperCase(sb.charAt(0)));
        return sb.toString();
    }

    private JpaUtil getJpaUtil() {
        JpaUtil objectUtil = (JpaUtil) ApplicationContextProvider.getBean("jpaUtil");
        return objectUtil;
    }

}
