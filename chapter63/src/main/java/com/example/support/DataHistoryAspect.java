package com.example.support;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.Id;
import javax.persistence.Table;
import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.StreamSupport;

/**
 * <h1>历史数据保存拦截器</h1>
 * Created by hanqf on 2022/9/20 16:11.
 *
 * 要求：
 * 1.所有Entity都要有唯一主键@Id
 */

@Aspect
@Slf4j
public class DataHistoryAspect {

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 新增和修改
     */
    /**
     * CrudRepository
     * <S extends T> S save(S entity);
     */
    @Pointcut("execution(* com.lexing.function..dao.*.save(..))")
    public void savePointCut() {
    }

    /**
     * JpaRepository
     * <S extends T> S saveAndFlush(S entity);
     */
    @Pointcut("execution(* com.lexing.function..dao.*.saveAndFlush(..))")
    public void saveAndFlushPointCut() {
    }

    /**
     * JpaRepository
     * <S extends T> List<S> saveAll(Iterable<S> entities);
     */
    @Pointcut("execution(* com.lexing.function..dao.*.saveAll(Iterable))")
    public void saveAllPointCut() {
    }

    /**
     * JpaRepository
     * <S extends T> List<S> saveAllAndFlush(Iterable<S> entities);
     */
    @Pointcut("execution(* com.lexing.function..dao.*.saveAllAndFlush(Iterable))")
    public void saveAllAndFlushPointCut() {
    }

    /**
     * BaseJpaRepository
     * <S extends T> List<S> batchSave(Iterable<S> iterable);
     */
    @Pointcut("execution(* com.lexing.function..dao.*.batchSave(Iterable))")
    public void batchSavePointCut() {
    }

    /**
     * BaseJpaRepository
     * <S extends T> List<S> batchUpdate(Iterable<S> iterable);
     */
    @Pointcut("execution(* com.lexing.function..dao.*.batchUpdate(Iterable))")
    public void batchUpdatePointCut() {
    }

    /**
     * 删除
     */
    /**
     * CrudRepository
     * void deleteById(ID id);
     */
    @Pointcut("execution(* com.lexing.function..dao.*.deleteById(..))")
    public void deleteByIdPointCut() {
    }

    /**
     * CrudRepository
     * void deleteAllById(Iterable<? extends ID> ids);
     */
    @Pointcut("execution(* com.lexing.function..dao.*.deleteAllById(Iterable))")
    public void deleteAllByIdPointCut() {
    }

    /**
     * CrudRepository
     * void deleteAll(Iterable<? extends T> entities);
     */
    @Pointcut("execution(* com.lexing.function..dao.*.deleteAll(Iterable))")
    public void deleteAllIterablePointCut() {
    }

    /**
     * CrudRepository
     * void delete();
     */
    @Pointcut("execution(* com.lexing.function..dao.*.delete(..))")
    public void deletePointCut() {
    }

    /**
     * CrudRepository
     * void deleteAll();
     */
    @Pointcut("execution(* com.lexing.function..dao.*.deleteAll())")
    public void deleteAllPointCut() {
    }

    /**
     * JpaRepository
     * void deleteAllInBatch();
     */
    @Pointcut("execution(* com.lexing.function..dao.*.deleteAllInBatch())")
    public void deleteAllInBatchPointCut() {
    }

    /**
     * JpaRepository
     * void deleteAllInBatch(Iterable<T> entities);
     */
    @Pointcut("execution(* com.lexing.function..dao.*.deleteAllInBatch(Iterable))")
    public void deleteAllInBatchIterablePointCut() {
    }

    /**
     * JpaRepository
     * void deleteAllInBatch(Iterable<T> entities);
     */
    @Pointcut("execution(* com.lexing.function..dao.*.deleteAllByIdInBatch(Iterable))")
    public void deleteAllByIdInBatchPointCut() {
    }


    @AfterReturning(value = "savePointCut() || saveAndFlushPointCut()", returning = "result")
    public void saveAfterReturning(JoinPoint joinPoint, Object result) {
        log.info("DataHistoryAspect afterReturning....");
        try {
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            final String methodName = method.getName();
            Object model = result;
            Class<?> modelClass = model.getClass();

            //必须被@Table注解的类
            final Table table = modelClass.getAnnotation(Table.class);
            if (table != null && !"tbl_cp_logger".equals(table.name())) {
                String dataId = String.valueOf(CP_ClassUtil.getFieldValue(model, CP_ClassUtil.getField(modelClass, Id.class)));
                makeHistory(methodName, objectMapper.writeValueAsString(model), table.name(), dataId);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterReturning(value = "saveAllPointCut() || saveAllAndFlushPointCut() || batchSavePointCut() || batchUpdatePointCut()", returning = "result")
    public void saveAllAfterReturning(JoinPoint joinPoint, List result) {
        log.info("DataHistoryAspect saveAllAfterReturning....");
        try {
            final Class targetInterface = CP_ClassUtil.getProxyTargetInterface(joinPoint.getTarget(), 0);
            final Class<?> modelClass = CP_ClassUtil.getTIClass(targetInterface, 0, 0);

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            final String methodName = method.getName();

            //必须被@Table注解的类
            final Table table = modelClass.getAnnotation(Table.class);
            if (table != null && !"tbl_cp_logger".equals(table.name())) {
                result.stream().forEach(entity -> {
                    String dataId = String.valueOf(CP_ClassUtil.getFieldValue(entity, CP_ClassUtil.getField(modelClass, Id.class)));
                    try {
                        makeHistory(methodName, objectMapper.writeValueAsString(entity), table.name(), dataId);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterReturning("(deleteAllIterablePointCut() || deleteAllInBatchIterablePointCut()) && args(entities)")
    public void deleteAllIterablePointCut(JoinPoint joinPoint, Iterable entities) {
        log.info("DataHistoryAspect deleteByIdAfterReturning....");
        try {
            final Class targetInterface = CP_ClassUtil.getProxyTargetInterface(joinPoint.getTarget(), 0);
            final Class<?> modelClass = CP_ClassUtil.getTIClass(targetInterface, 0, 0);
            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            final String methodName = method.getName();
            if (modelClass != null) {
                //必须被@Table注解的类
                final Table table = modelClass.getAnnotation(Table.class);
                if (table != null && !"tbl_cp_logger".equals(table.name())) {
                    StreamSupport.stream(entities.spliterator(), true)
                            .forEach(entity -> {
                                try {
                                    String dataId = String.valueOf(CP_ClassUtil.getFieldValue(entity, CP_ClassUtil.getField(entity.getClass(), Id.class)));
                                    makeHistory(methodName, objectMapper.writeValueAsString(entity), table.name(), dataId);
                                } catch (JsonProcessingException e) {
                                    throw new RuntimeException();
                                }
                            });
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterReturning("(deleteAllByIdPointCut() || deleteAllByIdInBatchPointCut()) && args(ids)")
    public void deleteAllByIdPointCutAfterReturning(JoinPoint joinPoint, Iterable ids) {
        log.info("DataHistoryAspect deleteAllByIdPointCutAfterReturning....");
        try {
            final Class targetInterface = CP_ClassUtil.getProxyTargetInterface(joinPoint.getTarget(), 0);
            final Class<?> modelClass = CP_ClassUtil.getTIClass(targetInterface, 0, 0);

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            final String methodName = method.getName();

            if (modelClass != null) {
                //必须被@Table注解的类
                final Table table = modelClass.getAnnotation(Table.class);
                if (table != null && !"tbl_cp_logger".equals(table.name())) {
                    StreamSupport.stream(ids.spliterator(), true)
                            .forEach(id -> makeHistory(methodName, String.valueOf(id), table.name(), String.valueOf(id)));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterReturning("deleteByIdPointCut() && args(id)")
    public void deleteByIdAfterReturning(JoinPoint joinPoint, Object id) {
        log.info("DataHistoryAspect deleteByIdAfterReturning....");
        try {
            final Class targetInterface = CP_ClassUtil.getProxyTargetInterface(joinPoint.getTarget(), 0);
            final Class<?> modelClass = CP_ClassUtil.getTIClass(targetInterface, 0, 0);

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            final String methodName = method.getName();

            if (modelClass != null) {
                //必须被@Table注解的类
                final Table table = modelClass.getAnnotation(Table.class);
                if (table != null && !"tbl_cp_logger".equals(table.name())) {
                    makeHistory(methodName, String.valueOf(id), table.name(), String.valueOf(id));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterReturning("deletePointCut() && args(entity)")
    public void deleteAfterReturning(JoinPoint joinPoint, Object entity) {
        log.info("DataHistoryAspect deleteAfterReturning....");
        try {
            final Class targetInterface = CP_ClassUtil.getProxyTargetInterface(joinPoint.getTarget(), 0);
            final Class<?> modelClass = CP_ClassUtil.getTIClass(targetInterface, 0, 0);

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            final String methodName = method.getName();

            if (modelClass != null) {
                //必须被@Table注解的类
                final Table table = modelClass.getAnnotation(Table.class);
                if (table != null && !"tbl_cp_logger".equals(table.name())) {
                    String dataId = String.valueOf(CP_ClassUtil.getFieldValue(entity, CP_ClassUtil.getField(modelClass, Id.class)));
                    makeHistory(methodName, objectMapper.writeValueAsString(entity), table.name(), dataId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @AfterReturning("deleteAllPointCut() || deleteAllInBatchPointCut()")
    public void deleteAllAfterReturning(JoinPoint joinPoint) {
        log.info("DataHistoryAspect deleteAllAfterReturning....");
        try {
            final Class targetInterface = CP_ClassUtil.getProxyTargetInterface(joinPoint.getTarget(), 0);
            final Class<?> modelClass = CP_ClassUtil.getTIClass(targetInterface, 0, 0);

            MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
            Method method = methodSignature.getMethod();
            final String methodName = method.getName();

            //必须被@Table注解的类
            final Table table = modelClass.getAnnotation(Table.class);
            if (table != null && !"tbl_cp_logger".equals(table.name())) {
                makeHistory(methodName, "deleteAll", table.name(), null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 保存数据
    */
    private void makeHistory(String historyType, String data, String tableName, String dataId) {
        System.out.println();
    }

}
