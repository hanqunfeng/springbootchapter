package com.example.mongo.dao;

import com.example.mongo.model.MongoUser;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

/**
 * Created by hanqf on 2020/3/16 14:03.
 */


public interface MongoUserRepository extends ReactiveMongoRepository<MongoUser, String> {

    Mono<MongoUser> findByUserName(String userName);

    Mono<Long> deleteByUserName(String userName);


}
