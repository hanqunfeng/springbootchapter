package com.example.r2dbc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.NoRepositoryBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.Serializable;

/**
 * <h1>BaseR2dbcRepository</h1>
 * Created by hanqf on 2023/8/31 15:24.
 */

@NoRepositoryBean //接口不参与jpa的代理
public interface BaseR2dbcRepository<T, ID extends Serializable> extends R2dbcRepository<T, ID> {

    /**
     * 分页查询
     */
    Mono<Page<T>> pageByQuery(Criteria criteria, Pageable pageable);

    Mono<Long> countByQuery(Criteria criteria);

    Flux<T> findByQuery(Criteria criteria);

}
