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

/**
 * 当我们的操作接口继承的是ReactiveCrudRepository<T, ID> 或者ReactiveSortingRepository<T, ID>时，需要在实体类上使用@Table注解，这也是推荐的用法
 * 当然实体类不使用@Table注解标记时，我们还可以继承R2dbcRepository<T, ID>接口
 *
 */
public interface TestOrderRepository extends BaseR2dbcRepository<TestOrder, Long> {
    Mono<TestOrder> findByOrderId(String orderId);

    /**
     * 分页查询全部
     */
    Flux<TestOrder> findAllBy(Pageable pageable);
}
