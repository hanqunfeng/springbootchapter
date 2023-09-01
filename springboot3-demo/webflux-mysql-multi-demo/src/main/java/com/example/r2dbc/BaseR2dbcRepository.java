package com.example.r2dbc;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;
import java.util.Map;
import java.util.function.BiFunction;

/**
 * <h1>BaseR2dbcRepository</h1>
 * Created by hanqf on 2023/8/31 15:24.
 */

@NoRepositoryBean //接口不参与jpa的代理
public interface BaseR2dbcRepository<T, ID extends Serializable> extends R2dbcRepository<T, ID> {

    R2dbcEntityOperations getR2dbcEntityOperations();

    /**
     * 分页查询
     */
    Mono<Page<T>> pageByQuery(Criteria criteria, Pageable pageable);

    Mono<Long> countByQuery(Criteria criteria);

    Flux<T> findByQuery(Criteria criteria);


    default <R> Mono<R> execSqlToMono(String sql, Map<String, Object> bindMap, BiFunction<Row, RowMetadata, R> mappingFunction) {
        DatabaseClient.GenericExecuteSpec genericExecuteSpec = getR2dbcEntityOperations().getDatabaseClient().sql(sql);
        if (bindMap != null) {
            for (Map.Entry<String, Object> entry : bindMap.entrySet()) {
                genericExecuteSpec = genericExecuteSpec.bind(entry.getKey(), entry.getValue());
            }
        }
        return genericExecuteSpec.map(mappingFunction).first();
    }

    default <R> Flux<R> execSqlToFlux(String sql, Map<String, Object> bindMap, BiFunction<Row, RowMetadata, R> mappingFunction) {
        DatabaseClient.GenericExecuteSpec genericExecuteSpec = getR2dbcEntityOperations().getDatabaseClient().sql(sql);
        if (bindMap != null) {
            for (Map.Entry<String, Object> entry : bindMap.entrySet()) {
                genericExecuteSpec = genericExecuteSpec.bind(entry.getKey(), entry.getValue());
            }
        }
        return genericExecuteSpec.map(mappingFunction).all();
    }

}
