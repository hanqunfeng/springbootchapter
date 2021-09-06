package com.example.jpa;


import com.example.context.ApplicationContextProvider;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;

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
    public <E> List<E> findByHql(String hql) {
        return (List<E>) entityManager.createQuery(hql)
                .getResultList();
    }


    @Override
    public List<Map> findBySql(String sql, Object... params) {
        Query nativeQuery = entityManager.createNativeQuery(sql);
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                nativeQuery.setParameter(i + 1, params[i]);
            }
        }
        return nativeQuery.unwrap(NativeQueryImpl.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .getResultList();
    }

    @Override
    public List<Map> findBySql(String sql, Map<String, Object> params) {
        Query nativeQuery = entityManager.createNativeQuery(sql);
        if (params != null && params.size() > 0) {
            for (String key : params.keySet()) {
                nativeQuery.setParameter(key, params.get(key));
            }
        }
        return nativeQuery.unwrap(NativeQueryImpl.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .getResultList();
    }

    //1.setResultTransformer(Transformers.aliasToBean(clazz))
    //不要钱clazz是Entity，也就是不用对应数据库的表
    //Expected type: java.lang.Long, actual value: java.lang.Integer
    //如果表中的id是int类型，而对象中的id是Long类型，就会抛上面的异常
    //count(*) 返回的是java.math.BigInteger类型

    //2.addEntity(clazz) clazz必须是Entity，必须select * from table
    @Override
    public <E> List<E> findBySql(String sql, Class clazz, boolean basic, Object... params) {
        return getJpaUtil().mapListToObjectList(findBySql(sql, params), clazz, basic);
    }

    @Override
    public <E> List<E> findBySql(String sql, Class clazz, boolean basic, Map<String, Object> params) {
        return getJpaUtil().mapListToObjectList(findBySql(sql, params), clazz, basic);
    }

    @Override
    public <E> Page<E> findPageBySql(String sql, Pageable pageable, Class clazz, boolean basic, Object... params) {
        if (!sql.toLowerCase().contains("order by")) {
            StringBuilder stringBuilder = new StringBuilder(sql);
            stringBuilder.append(" order by ");
            final Sort sort = pageable.getSort();
            final List<Sort.Order> orders = sort.toList();
            for (Sort.Order order : orders) {
                stringBuilder.append(order.getProperty())
                        .append(" ")
                        .append(order.getDirection().name())
                        .append(",");
            }
            sql = stringBuilder.toString();
            sql = sql.substring(0, sql.length() - 1);
        }

        final Query nativeQuery = entityManager.createNativeQuery(sql);
        nativeQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        nativeQuery.setMaxResults(pageable.getPageSize());

        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                nativeQuery.setParameter(i + 1, params[i]);
            }
        }


        List<Map> resultList = nativeQuery.unwrap(NativeQueryImpl.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        final List<E> objectList = getJpaUtil().mapListToObjectList(resultList, clazz, basic);

        String countSql = "select count(*) from ( " + sql + " ) a";
        final BigInteger count = findBySqlFirst(countSql, BigInteger.class, true);

        Page<E> page = new PageImpl<>(objectList, pageable, count.longValue());

        return page;
    }

    @Override
    public <E> Page<E> findPageBySql(String sql, Pageable pageable, Class clazz, boolean basic, Map<String, Object> params) {
        if (!sql.toLowerCase().contains("order by")) {
            StringBuilder stringBuilder = new StringBuilder(sql);
            stringBuilder.append(" order by ");
            final Sort sort = pageable.getSort();
            final List<Sort.Order> orders = sort.toList();
            for (Sort.Order order : orders) {
                stringBuilder.append(order.getProperty())
                        .append(" ")
                        .append(order.getDirection().name())
                        .append(",");
            }
            sql = stringBuilder.toString();
            sql = sql.substring(0, sql.length() - 1);
        }

        final Query nativeQuery = entityManager.createNativeQuery(sql);
        nativeQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        nativeQuery.setMaxResults(pageable.getPageSize());

        if (params != null && params.size() > 0) {
            for (String key : params.keySet()) {
                nativeQuery.setParameter(key, params.get(key));
            }
        }


        List<Map> resultList = nativeQuery.unwrap(NativeQueryImpl.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        final List<E> objectList = getJpaUtil().mapListToObjectList(resultList, clazz, basic);

        String countSql = "select count(*) from ( " + sql + " ) a";
        final BigInteger count = findBySqlFirst(countSql, BigInteger.class, true);

        Page<E> page = new PageImpl<>(objectList, pageable, count.longValue());

        return page;
    }

    @Override
    public Map findBySqlFirst(String sql, Object... params) {
        Query nativeQuery = entityManager.createNativeQuery(sql);
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                nativeQuery.setParameter(i + 1, params[i]);
            }
        }
        final Optional first = nativeQuery.unwrap(NativeQueryImpl.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .stream().findFirst();
        if (first.isPresent()) {
            return (Map) first.get();
        }
        return null;
    }

    @Override
    public Map findBySqlFirst(String sql, Map<String, Object> params) {
        Query nativeQuery = entityManager.createNativeQuery(sql);
        if (params != null && params.size() > 0) {
            for (String key : params.keySet()) {
                nativeQuery.setParameter(key, params.get(key));
            }
        }
        final Optional first = nativeQuery.unwrap(NativeQueryImpl.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .stream().findFirst();
        if (first.isPresent()) {
            return (Map) first.get();
        }
        return null;
    }

    @Override
    public <E> E findBySqlFirst(String sql, Class clazz, boolean basic, Object... params) {
        return getJpaUtil().mapToObject(findBySqlFirst(sql, params), clazz, basic);
    }

    @Override
    public <E> E findBySqlFirst(String sql, Class clazz, boolean basic, Map<String, Object> params) {
        return getJpaUtil().mapToObject(findBySqlFirst(sql, params), clazz, basic);
    }

    @Override
    public T findByIdNew(ID id) {
        T t = null;

        Optional<T> optional = this.findById(id);
        if (optional.isPresent()) {
            t = optional.get();
        }

        return t;

    }


    private JpaUtil getJpaUtil() {
        JpaUtil jpaUtil = (JpaUtil) ApplicationContextProvider.getBean("jpaUtil");
        return jpaUtil;
    }

    @Override
    @Transactional
    public <S extends T> Iterable<S> batchSave(Iterable<S> iterable) {
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
        return iterable;
    }

    @Override
    @Transactional
    public <S extends T> Iterable<S> batchUpdate(Iterable<S> iterable) {
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
        return iterable;
    }

}
