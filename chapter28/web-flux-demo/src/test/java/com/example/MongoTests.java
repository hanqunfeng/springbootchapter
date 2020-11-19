package com.example;

import com.example.mongo.model.MongoUser;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;

/**
 * <h1></h1>
 * Created by hanqf on 2020/11/18 15:45.
 */


public class MongoTests {
    private static final WebClient CLIENT = WebClient.create("http://localhost:8080");


    @Test
    public void save() throws InterruptedException {
        System.out.println("保存用户数据");
        MongoUser mongoUser = new MongoUser();
        mongoUser.setUserName("wahaha");
        mongoUser.setAge(110);
        mongoUser.setEmail("wahaha@1234.com");
        mongoUser.setBlog("https://blog.wahaha.com");
        mongoUser.setTags(new String[]{"a","b","c"});

        //此时不会发起请求，只有调用block或者subscribe方法才会发起请求
        Mono<MongoUser> userMono = CLIENT
                .post()
                .uri("/mongouser")
                //发送请求数据
                .body(Mono.just(mongoUser),MongoUser.class)
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .retrieve()
                .bodyToMono(MongoUser.class);

        //同步请求,发起一次请求，等到接收到结果才继续执行
        MongoUser block = userMono.block();
        System.out.println("block=" + block);

        //异步请求，又发起一次请求
        userMono.subscribe(user -> {
            System.out.println("subscribe=" + user);
        });
        //因为是异步请求，所以这里设置线程休眠2秒，否则主线程结束，不会收到响应
        Thread.sleep(2000);
    }

    @Test
    public void findByUserName() throws InterruptedException {
        //此时不会发起请求，只有调用block或者subscribe方法才会发起请求
        Mono<MongoUser> userMono = CLIENT
                .get()
                .uri("/mongouser/wahaha")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .retrieve()
                .bodyToMono(MongoUser.class);

        //同步请求,发起一次请求，等到接收到结果才继续执行
        MongoUser block = userMono.block();
        System.out.println("block=" + block);

        //异步请求，又发起一次请求
        userMono.subscribe(user -> {
            System.out.println("subscribe=" + user);
        });
        //因为是异步请求，所以这里设置线程休眠2秒，否则主线程结束，不会收到响应
        Thread.sleep(2000);
    }


    @Test
    public void deleteByUserName() throws InterruptedException {
        //此时不会发起请求，只有调用block或者subscribe方法才会发起请求
        Mono<Long> userMono = CLIENT
                .delete()
                .uri("/mongouser/wahaha")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .retrieve()
                .bodyToMono(Long.class);

        //返回删除的条数
        //同步请求,发起一次请求，等到接收到结果才继续执行，此时结果为1
        Long block = userMono.block();
        System.out.println("block=" + block);

        //异步请求，又发起一次请求，因为同步调用已经删除了数据，所以此时结果为0
        userMono.subscribe(result -> {
            System.out.println("subscribe=" + result);
        });
        //因为是异步请求，所以这里设置线程休眠2秒，否则主线程结束，不会收到响应
        Thread.sleep(2000);
    }

    @Test
    public void findAll() throws InterruptedException {
        Flux<MongoUser> userFlux = CLIENT
                .get()
                .uri("/mongouser")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .retrieve()
                .bodyToFlux(MongoUser.class);

        userFlux.toIterable().forEach(user ->{
            System.out.println("同步" + user);
        });

        userFlux.subscribe(user -> {
            System.out.println("异步" + user);
        });
        //因为是异步请求，所以这里设置线程休眠2秒，否则主线程结束，不会收到响应
        Thread.sleep(2000);
    }


    @Test
    public void findAllStream() throws InterruptedException {
        Flux<MongoUser> userFlux = CLIENT
                .get()
                .uri("/mongouser/stream")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .retrieve()
                .bodyToFlux(MongoUser.class);

        userFlux.toIterable().forEach(user ->{
            System.out.println("同步" + user);
        });

        userFlux.doOnNext(user ->{
            System.out.println("异步" + user);
        }).blockLast();// blockLast方法在收到最后一个元素前会阻塞，响应式业务场景中慎用

    }




}

