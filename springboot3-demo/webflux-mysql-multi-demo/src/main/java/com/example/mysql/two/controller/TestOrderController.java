package com.example.mysql.two.controller;

import com.example.mysql.two.model.TestOrder;
import com.example.mysql.two.repository.TestOrderRepository;
import com.example.r2dbc.DefaultR2dbcService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.*;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;

/**
 * <h1></h1>
 * Created by hanqf on 2023/8/31 11:40.
 */

@RestController
@RequestMapping("/order")
public class TestOrderController {

    @Autowired
    private TestOrderRepository testOrderRepository;

    @Autowired
    @Qualifier("twoTransactionalOperator")
    private TransactionalOperator twoTransactionalOperator;

    @Autowired
    @Qualifier("twoR2dbcEntityTemplate")
    private R2dbcEntityTemplate twoR2dbcEntityTemplate;


    @RequestMapping("/save")
    public Mono<TestOrder> save(@RequestBody TestOrder testOrder) {
        return testOrderRepository.save(testOrder);
    }


    @RequestMapping("/update")
    public Mono<TestOrder> update(@RequestBody TestOrder testOrder) {
        return testOrderRepository.save(testOrder);
    }

    @RequestMapping("/page")
    public Flux<TestOrder> page() {
        Pageable page = PageRequest.of(1, 10, Sort.by(Sort.Order.desc("id")));
        return testOrderRepository.findAllBy(page);
    }

    @RequestMapping("/page2")
    public Mono<Page<TestOrder>> page2() {
        Pageable page = PageRequest.of(1, 10, Sort.by(Sort.Order.desc("id")));
        return testOrderRepository.count().flatMap(total -> testOrderRepository.findAllBy(page).collectList().map(list -> new PageImpl<>(list, page, total)));
    }

    @RequestMapping("/page3")
    public Mono<Page<TestOrder>> page3() {
        Pageable page = PageRequest.of(1, 10, Sort.by(Sort.Order.desc("id")));
        return testOrderRepository.pageByQuery(Criteria.empty(), page);
    }

    @RequestMapping("/page4")
    public Mono<Page<TestOrder>> page4() {
        Pageable page = PageRequest.of(1, 10, Sort.by(Sort.Order.desc("id")));

        Criteria criteria = Criteria.empty();
        criteria = criteria.and("order_id").like("aaaaorder2020-00000%");

        return testOrderRepository.pageByQuery(criteria, page);
    }

    @RequestMapping("/count")
    public Mono<Long> count() {
        return testOrderRepository.countByQuery(Criteria.empty());
    }

    @RequestMapping("/list")
    public Flux<TestOrder> findList() {
        Criteria criteria = Criteria.empty();
        criteria = criteria.and("order_id").like("aaaaorder2020-00000%");
        return testOrderRepository.findByQuery(criteria);
    }

    /**
     * 编程式事务
     * 任意操作失败都会回滚
     */
    @RequestMapping("/tx")
    public Mono<TestOrder> tx() {
        TestOrder testOrder1 = new TestOrder();
        testOrder1.setOrderId("test");

        TestOrder testOrder2 = new TestOrder();
        testOrder2.setOrderId("test");
        // 这里orderId有唯一索引，重复 insert 会报错
        return testOrderRepository.save(testOrder1)
                .then(testOrderRepository.save(testOrder2))
                // 开启事务
                .as(twoTransactionalOperator::transactional);

    }

    /**
     * 注解式事务
     * 任意操作失败都会回滚
     * 注意这里 transactionManager 虽然飘红，但是其是支持 ReactiveTransactionManager 的，不影响代码编译和运行，这里应该是ide的问题
     */
    @Transactional(transactionManager = "twoR2dbcTransactionManager", propagation = Propagation.REQUIRED)
    @RequestMapping("/tx2")
    public Mono<TestOrder> tx2() {
        String orderId = UUID.randomUUID().toString();
        TestOrder testOrder1 = new TestOrder();
        testOrder1.setOrderId("test" + orderId);

        TestOrder testOrder2 = new TestOrder();
        testOrder2.setOrderId("test" + orderId);
        // 这里orderId有唯一索引，重复 insert 会报错
        return testOrderRepository.save(testOrder1)
                .then(testOrderRepository.save(testOrder2));

    }

    /**
     * 没有事务，虽然第二条插入失败，但是第一条会保存成功
     */
    @RequestMapping("/tx-no")
    public Mono<TestOrder> txNo() {
        String orderId = UUID.randomUUID().toString();
        TestOrder testOrder1 = new TestOrder();
        testOrder1.setOrderId("testNo" + orderId);

        TestOrder testOrder2 = new TestOrder();
        testOrder2.setOrderId("testNo" + orderId);
        // 这里orderId有唯一索引，重复 insert 会报错
        return testOrderRepository.save(testOrder1)
                .then(testOrderRepository.save(testOrder2));

    }

    @RequestMapping("/one")
    public Mono<TestOrder> getOne(String orderId) {
        String sql = "select id, order_id from test_order where order_id = :orderId";

        return testOrderRepository.execSqlToMono(sql, Map.of("orderId", orderId), (row, rowMetadata) -> {
            final TestOrder testOrder = new TestOrder();
            testOrder.setId(row.get("id", Long.class));
            testOrder.setOrderId(row.get("order_id", String.class));
            return testOrder;
        });
    }

    @RequestMapping("/many")
    public Flux<TestOrder> getMany(String orderId) {
        String sql = "select id, order_id from test_order where order_id like CONCAT('%',:orderId,'%')";

        return testOrderRepository.execSqlToFlux(sql, Map.of("orderId", orderId), (row, rowMetadata) -> {
            final TestOrder testOrder = new TestOrder();
            testOrder.setId(row.get("id", Long.class));
            testOrder.setOrderId(row.get("order_id", String.class));
            return testOrder;
        });
    }


    @RequestMapping("/many2")
    public Flux<TestOrder> getMany2(String orderId) {
        String sql = "select id, order_id from test_order where order_id like CONCAT('%',:orderId,'%')";

        return DefaultR2dbcService.builder()
                .r2dbcEntityTemplate(twoR2dbcEntityTemplate).build()
                .execSqlToFlux(sql, Map.of("orderId", orderId), (row, rowMetadata) -> {
                    final TestOrder testOrder = new TestOrder();
                    testOrder.setId(row.get("id", Long.class));
                    testOrder.setOrderId(row.get("order_id", String.class));
                    return testOrder;
                });
    }


}

