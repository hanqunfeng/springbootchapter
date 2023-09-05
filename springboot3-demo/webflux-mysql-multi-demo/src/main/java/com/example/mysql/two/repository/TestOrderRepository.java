package com.example.mysql.two.repository;

/**
 * <h1>SysUserRepository</h1>
 * Created by hanqf on 2023/8/30 17:15.
 */


import com.example.mysql.two.model.TestOrder;
import com.example.r2dbc.BaseR2dbcRepository;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface TestOrderRepository extends BaseR2dbcRepository<TestOrder, Long> {
    Mono<TestOrder> findByOrderId(String orderId);

    /**
     * 分页查询全部
     */
    Flux<TestOrder> findAllBy(Pageable pageable);
}
