package com.example.r2dbc;

import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.r2dbc.core.DatabaseClient;
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

    public <R> Mono<R> execSqlToMono(String sql, Map<String, Object> bindMap, BiFunction<Row, RowMetadata, R> mappingFunction) {
        DatabaseClient.GenericExecuteSpec genericExecuteSpec = r2dbcEntityTemplate.getDatabaseClient().sql(sql);
        if (bindMap != null) {
            for (Map.Entry<String, Object> entry : bindMap.entrySet()) {
                genericExecuteSpec = genericExecuteSpec.bind(entry.getKey(), entry.getValue());
            }
        }
        return genericExecuteSpec.map(mappingFunction).first();
    }

    public <R> Flux<R> execSqlToFlux(String sql, Map<String, Object> bindMap, BiFunction<Row, RowMetadata, R> mappingFunction) {
        DatabaseClient.GenericExecuteSpec genericExecuteSpec = r2dbcEntityTemplate.getDatabaseClient().sql(sql);
        if (bindMap != null) {
            for (Map.Entry<String, Object> entry : bindMap.entrySet()) {
                genericExecuteSpec = genericExecuteSpec.bind(entry.getKey(), entry.getValue());
            }
        }
        return genericExecuteSpec.map(mappingFunction).all();
    }

}
