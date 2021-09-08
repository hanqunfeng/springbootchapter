package com.example.jpa;


import com.example.context.ApplicationContextProvider;
import org.hibernate.Hibernate;
import org.hibernate.query.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.Serializable;
import java.lang.reflect.Method;
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
    public List<Map> findBySql(String sql) {
        return findBySql(sql, new HashMap<>());
    }

    @Override
    public List<Map> findBySql(String sql, Object[] params) {
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


    @Override
    public <E> List<E> findBySql(String sql, Class clazz, boolean basic) {
        return findBySql(sql, clazz, basic, new HashMap<>());
    }

    @Override
    public <E> List<E> findBySql(String sql, Class clazz, boolean basic, Object[] params) {
        return getJpaUtil().mapListToObjectList(findBySql(sql, params), clazz, basic);
    }

    @Override
    public <E> List<E> findBySql(String sql, Class clazz, boolean basic, Map<String, Object> params) {
        return getJpaUtil().mapListToObjectList(findBySql(sql, params), clazz, basic);
    }

    @Override
    public <E> Page<E> findPageBySql(String sql, Pageable pageable, Class clazz, boolean basic) {
        return findPageBySql(sql, pageable, clazz, basic, new HashMap<>());
    }

    @Override
    public <E> Page<E> findPageBySql(String sql, String countSql, Pageable pageable, Class clazz, boolean basic) {
        return findPageBySql(sql, countSql, pageable, clazz, basic, new HashMap<>());
    }

    @Override
    public <E> Page<E> findPageBySql(String sql, Pageable pageable, Class clazz, boolean basic, Object[] params) {
        return findPageBySql(sql, null, pageable, clazz, basic, params);
    }

    @Override
    public <E> Page<E> findPageBySql(String sql, String countSql, Pageable pageable, Class clazz, boolean basic, Object[] params) {
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

        if (!StringUtils.hasText(countSql)) {
            countSql = "select count(*) from ( " + sql + " ) a";
        }
        final BigInteger count = findBySqlFirst(countSql, BigInteger.class, true);

        Page<E> page = new PageImpl<>(objectList, pageable, count.longValue());

        return page;
    }

    @Override
    public <E> Page<E> findPageBySql(String sql, Pageable pageable, Class clazz, boolean basic, Map<String, Object> params) {
        return findPageBySql(sql, null, pageable, clazz, basic, params);
    }

    @Override
    public <E> Page<E> findPageBySql(String sql, String countSql, Pageable pageable, Class clazz, boolean basic, Map<String, Object> params) {
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

        if (!StringUtils.hasText(countSql)) {
            countSql = "select count(*) from ( " + sql + " ) a";
        }
        final BigInteger count = findBySqlFirst(countSql, BigInteger.class, true);

        Page<E> page = new PageImpl<>(objectList, pageable, count.longValue());

        return page;
    }

    @Override
    public Map findBySqlFirst(String sql) {
        return findBySqlFirst(sql, new HashMap<>());
    }

    @Override
    public Map findBySqlFirst(String sql, Object[] params) {
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
    public <E> E findBySqlFirst(String sql, Class clazz, boolean basic) {
        return findBySqlFirst(sql, clazz, basic, new HashMap<>());
    }

    @Override
    public <E> E findBySqlFirst(String sql, Class clazz, boolean basic, Object[] params) {
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
    public void lazyInitialize(T obj,
                               String[] fields) {
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
