package com.hanqf.support.jpa;

import com.hanqf.support.ApplicationContextProvider;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.Transformers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.StringUtils;

import java.math.BigInteger;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * <h1>DefaultJpaService</h1>
 * Jpa 工具服务类
 * Created by hanqf on 2023/9/4 10:55.
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DefaultJpaService {

    //通过构造方法初始化EntityManager
    private EntityManager entityManager;

    public EntityManager getEntityManager() {
        if (entityManager == null) {
            // 默认主数据源
            entityManager = ApplicationContextProvider.getBean("entityManager", EntityManager.class);
        }
        return entityManager;
    }

    public List<Map> findBySql(String sql) {
        return findBySql(sql, new HashMap<>());
    }

    public List<Map> findBySql(String sql, Object[] params) {
        Query nativeQuery = getEntityManager().createNativeQuery(sql);
        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                nativeQuery.setParameter(i + 1, params[i]);
            }
        }
        return nativeQuery.unwrap(NativeQueryImpl.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .getResultList();
    }


    public List<Map> findBySql(String sql, Map<String, Object> params) {
        Query nativeQuery = getEntityManager().createNativeQuery(sql);
        if (params != null && params.size() > 0) {
            for (String key : params.keySet()) {
                nativeQuery.setParameter(key, params.get(key));
            }
        }
        return nativeQuery.unwrap(NativeQueryImpl.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP)
                .getResultList();
    }


    public <E> List<E> findBySql(String sql, Class<E> clazz) {
        return findBySql(sql, clazz, new HashMap<>());
    }


    public <E> List<E> findBySql(String sql, Class<E> clazz, Object[] params) {
        return getJpaUtil().mapListToObjectList(findBySql(sql, params), clazz);
    }


    public <E> List<E> findBySql(String sql, Class<E> clazz, Map<String, Object> params) {
        return getJpaUtil().mapListToObjectList(findBySql(sql, params), clazz);
    }


    public <E> Page<E> findPageBySql(String sql, Pageable pageable, Class<E> clazz) {
        return findPageBySql(sql, pageable, clazz, new HashMap<>());
    }


    public <E> Page<E> findPageBySql(String sql, String countSql, Pageable pageable, Class<E> clazz) {
        return findPageBySql(sql, countSql, pageable, clazz, new HashMap<>());
    }


    public <E> Page<E> findPageBySql(String sql, Pageable pageable, Class<E> clazz, Object[] params) {
        return findPageBySql(sql, null, pageable, clazz, params);
    }


    public <E> Page<E> findPageBySql(String sql, String countSql, Pageable pageable, Class<E> clazz, Object[] params) {
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

        final Query nativeQuery = getEntityManager().createNativeQuery(sql);
        nativeQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        nativeQuery.setMaxResults(pageable.getPageSize());

        if (params != null && params.length > 0) {
            for (int i = 0; i < params.length; i++) {
                nativeQuery.setParameter(i + 1, params[i]);
            }
        }


        List<Map> resultList = nativeQuery.unwrap(NativeQueryImpl.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        final List<E> objectList = getJpaUtil().mapListToObjectList(resultList, clazz);

        if (!StringUtils.hasText(countSql)) {
            countSql = "select count(*) from ( " + sql + " ) a";
        }
        final BigInteger count = findBySqlFirst(countSql, BigInteger.class);

        Page<E> page = new PageImpl<>(objectList, pageable, count.longValue());

        return page;
    }


    public <E> Page<E> findPageBySql(String sql, Pageable pageable, Class<E> clazz, Map<String, Object> params) {
        return findPageBySql(sql, null, pageable, clazz, params);
    }


    public <E> Page<E> findPageBySql(String sql, String countSql, Pageable pageable, Class<E> clazz, Map<String, Object> params) {
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

        final Query nativeQuery = getEntityManager().createNativeQuery(sql);
        nativeQuery.setFirstResult(pageable.getPageNumber() * pageable.getPageSize());
        nativeQuery.setMaxResults(pageable.getPageSize());

        if (params != null && params.size() > 0) {
            for (String key : params.keySet()) {
                nativeQuery.setParameter(key, params.get(key));
            }
        }


        List<Map> resultList = nativeQuery.unwrap(NativeQueryImpl.class)
                .setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP).getResultList();

        final List<E> objectList = getJpaUtil().mapListToObjectList(resultList, clazz);

        if (!StringUtils.hasText(countSql)) {
            countSql = "select count(*) from ( " + sql + " ) a";
        }
        final BigInteger count = findBySqlFirst(countSql, BigInteger.class);

        Page<E> page = new PageImpl<>(objectList, pageable, count.longValue());

        return page;
    }


    public Map findBySqlFirst(String sql) {
        return findBySqlFirst(sql, new HashMap<>());
    }


    public Map findBySqlFirst(String sql, Object[] params) {
        Query nativeQuery = getEntityManager().createNativeQuery(sql);
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


    public Map findBySqlFirst(String sql, Map<String, Object> params) {
        Query nativeQuery = getEntityManager().createNativeQuery(sql);
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


    public <E> E findBySqlFirst(String sql, Class<E> clazz) {
        return findBySqlFirst(sql, clazz, new HashMap<>());
    }


    public <E> E findBySqlFirst(String sql, Class<E> clazz, Object[] params) {
        return getJpaUtil().mapToObject(findBySqlFirst(sql, params), clazz);
    }


    public <E> E findBySqlFirst(String sql, Class<E> clazz, Map<String, Object> params) {
        return getJpaUtil().mapToObject(findBySqlFirst(sql, params), clazz);
    }


    private JpaUtil getJpaUtil() {
        JpaUtil objectUtil = (JpaUtil) ApplicationContextProvider.getBean("jpaUtil");
        return objectUtil;
    }
}
