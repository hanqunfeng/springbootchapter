package com.example.mongo.dao;

import com.example.mongo.model.MongoUser;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

/**
 * Created by hanqf on 2020/3/16 14:03.
 */


public interface MongoUserRepository extends ReactiveCrudRepository<MongoUser, String> {

    Mono<MongoUser> findByUserName(String userName);

    Mono<Long> deleteByUserName(String userName);


}
