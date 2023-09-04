package com.example.r2dbc;

import com.example.support.ApplicationContextProvider;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.function.BiFunction;

/**
 * <h1></h1>
 * Created by hanqf on 2023/9/1 17:03.
 */

@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DefaultR2dbcService {
    private R2dbcEntityTemplate r2dbcEntityTemplate;

    public R2dbcEntityTemplate getR2dbcEntityTemplate() {
        if (r2dbcEntityTemplate == null) {
            // 默认主数据源
            r2dbcEntityTemplate = ApplicationContextProvider.getBean("oneR2dbcEntityTemplate", R2dbcEntityTemplate.class);
        }
        return r2dbcEntityTemplate;
    }

    private <R> RowsFetchSpec<R> execSql(String sql, Map<String, Object> bindMap, BiFunction<Row, RowMetadata, R> mappingFunction) {
        DatabaseClient.GenericExecuteSpec genericExecuteSpec = getR2dbcEntityTemplate().getDatabaseClient().sql(sql);
        if (bindMap != null) {
            for (Map.Entry<String, Object> entry : bindMap.entrySet()) {
                genericExecuteSpec = genericExecuteSpec.bind(entry.getKey(), entry.getValue());
            }
        }
        return genericExecuteSpec.map(mappingFunction);
    }

    public <R> Mono<R> execSqlToMono(String sql, BiFunction<Row, RowMetadata, R> mappingFunction) {
        return execSql(sql, null, mappingFunction).first();
    }

    public <R> Mono<R> execSqlToMono(String sql, Map<String, Object> bindMap, BiFunction<Row, RowMetadata, R> mappingFunction) {
        return execSql(sql, bindMap, mappingFunction).first();
    }

    public <R> Flux<R> execSqlToFlux(String sql, BiFunction<Row, RowMetadata, R> mappingFunction) {
        return execSql(sql, null, mappingFunction).all();
    }

    public <R> Flux<R> execSqlToFlux(String sql, Map<String, Object> bindMap, BiFunction<Row, RowMetadata, R> mappingFunction) {
        return execSql(sql, bindMap, mappingFunction).all();
    }

}
