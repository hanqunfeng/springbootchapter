package com.example.mongo.service;

import com.example.mongo.dao.MongoUserRepository;
import com.example.mongo.model.MongoUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * <p></p>
 * Created by hanqf on 2020/5/8 13:27.
 */

@Service
public class MongoUserService {

    @Autowired
    private MongoUserRepository userRepository;

    /**
     * 保存或更新。
     * 如果传入的user没有id属性，由于username是unique的，在重复的情况下有可能报错，
     * 这时找到以保存的user记录用传入的user更新它。
     */
    public Mono<MongoUser> save(MongoUser user) {
        return userRepository.save(user)
                .onErrorResume(e ->     // 1
                        userRepository.findByUserName(user.getUserName())   // 2
                                .flatMap(originalUser -> {      // 4
                                    user.setId(originalUser.getId());
                                    return userRepository.save(user);   // 3
                                }));
    }

    public Mono<Long> deleteByUserName(String userName) {
        return userRepository.deleteByUserName(userName);
    }

    public Mono<MongoUser> findByUserName(String userName) {
        return userRepository.findByUserName(userName);
    }

    public Flux<MongoUser> findAll() {
        return userRepository.findAll();
    }
}
