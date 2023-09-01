package com.example.r2dbc;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.repository.query.RelationalEntityInformation;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * <h1>SimpleBaseR2dbcRepository</h1>
 * Created by hanqf on 2023/8/31 15:28.
 */

@Slf4j
public class SimpleBaseR2dbcRepository<T, ID extends Serializable> extends SimpleR2dbcRepository<T, ID> implements BaseR2dbcRepository<T, ID> {

    private final R2dbcEntityOperations r2dbcEntityOperations;

    private final RelationalEntityInformation<T, ID> entity;

    public SimpleBaseR2dbcRepository(RelationalEntityInformation<T, ID> entity, R2dbcEntityOperations entityOperations, R2dbcConverter converter) {
        super(entity, entityOperations, converter);
        this.r2dbcEntityOperations = entityOperations;
        this.entity = entity;
        log.info("SimpleBaseR2dbcRepository");
    }

    @Override
    public R2dbcEntityOperations getR2dbcEntityOperations() {
        return r2dbcEntityOperations;
    }

    @Override
    public Mono<Page<T>> pageByQuery(Criteria criteria, Pageable pageable) {
        final Query query = Query.query(criteria);
        return r2dbcEntityOperations.count(query, entity.getJavaType()).flatMap(total ->
                r2dbcEntityOperations.select(query.with(pageable), entity.getJavaType())
                        .collectList()
                        .map(list -> new PageImpl<>(list, pageable, total)));
    }

    @Override
    public Mono<Long> countByQuery(Criteria criteria) {
        final Query query = Query.query(criteria);
        return r2dbcEntityOperations.count(query, entity.getJavaType());
    }

    @Override
    public Flux<T> findByQuery(Criteria criteria) {
        final Query query = Query.query(criteria);
        return r2dbcEntityOperations.select(query, entity.getJavaType());
    }
}
