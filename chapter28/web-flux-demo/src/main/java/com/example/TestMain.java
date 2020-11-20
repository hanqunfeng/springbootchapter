package com.example;

import com.example.model.User;
import com.example.mongo.model.MongoUser;
import lombok.SneakyThrows;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Base64Utils;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * <p></p>
 * Created by hanqf on 2020/5/4 21:38.
 */


public class TestMain {

    private static final WebClient CLIENT = WebClient.create("http://localhost:8080");

    private static final Mono<ClientResponse> HELLO = CLIENT.get()
            .uri("/hello")
            //增加了basic安全认证，所以这里需要传递header认证信息
            .header(HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
            .accept(MediaType.TEXT_PLAIN)
            .exchange(); //exchange方法不推荐使用了，推荐使用retrieve方法
    private static final Mono<ClientResponse> INDEX = CLIENT.get()
            .uri("/index")
            //增加了basic安全认证，所以这里需要传递header认证信息
            .header(HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
            .accept(MediaType.TEXT_PLAIN)
            .exchange();
    private static final Mono<ClientResponse> DEMO = CLIENT.get()
            .uri("/demo/100")
            //增加了basic安全认证，所以这里需要传递header认证信息
            .header(HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
            .accept(MediaType.TEXT_PLAIN)
            .exchange();
    private static final Mono<ClientResponse> USER = CLIENT.get()
            .uri("/user/1")
            //增加了basic安全认证，所以这里需要传递header认证信息
            .header(HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
            .accept(new MediaType("application", "json", StandardCharsets.UTF_8))
            .exchange();
    private static final Mono<ClientResponse> USER_ALL = CLIENT.get()
            .uri("/users")
            //增加了basic安全认证，所以这里需要传递header认证信息
            .header(HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
            .accept(new MediaType("application", "json", StandardCharsets.UTF_8)).exchange();
    private static final Mono<ClientResponse> USERNAME = CLIENT.get()
            .uri("/username/张三")
            //增加了basic安全认证，所以这里需要传递header认证信息
            .header(HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
            .accept(new MediaType("application", "json", StandardCharsets.UTF_8))
            .exchange();
    private static final Mono<ClientResponse> UA = CLIENT.get()
            .uri("/ua")
            //增加了basic安全认证，所以这里需要传递header认证信息
            .header(HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
            .accept(new MediaType("application", "json", StandardCharsets.UTF_8)).exchange();
    private static final Mono<ClientResponse> TIME = CLIENT.get()
            .uri("/time")
            //增加了basic安全认证，所以这里需要传递header认证信息
            .header(HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
            .accept(MediaType.TEXT_PLAIN)
            .exchange();
    private static final Mono<ClientResponse> DATE = CLIENT.get()
            .uri("/date")
            //增加了basic安全认证，所以这里需要传递header认证信息
            .header(HttpHeaders.AUTHORIZATION,
                    "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
            .accept(MediaType.TEXT_PLAIN)
            .exchange();

    private static String getHello() {
        return ">> result = " + HELLO.flatMap(res -> res.bodyToMono(String.class)).block();
    }

    private static String getIndex() {
        return ">> result = " + INDEX.flatMap(res -> res.bodyToMono(String.class)).block();
    }

    private static String getDemo() {
        return ">> result = " + DEMO.flatMap(res -> res.bodyToMono(String.class)).block();
    }

    private static String getUser() {
        User block = USER.flatMap(res -> res.bodyToMono(User.class)).block();
        return ">> result = " + block;
    }

    private static String getUserAll() {
        List<User> block = USER_ALL.flatMap(res -> res.bodyToFlux(User.class).collectList()).block();
        return ">> result = " + block;
    }

    private static String getUserName() {
        User block = USERNAME.flatMap(res -> res.bodyToMono(User.class)).block();
        return ">> result = " + block;
    }

    private static String getUa() {
        List<User> block = UA.flatMap(res -> res.bodyToFlux(User.class).collectList()).block();
        return ">> result = " + block;
    }

    private static String getTime() {
        final String block = TIME.flatMap(res -> res.bodyToMono(String.class)).block();
        return ">> result = " + block;
    }

    private static String getDate() {
        final String block = DATE.flatMap(res -> res.bodyToMono(String.class)).block();
        return ">> result = " + block;
    }


    //以下两种效果相同
    private static String getTimes() {
        final String block = CLIENT.get()
                .uri("/times")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .accept(MediaType.TEXT_EVENT_STREAM) //配置请求Header：Content-Type: text/event-stream，即SSE
                .retrieve()//异步接收服务端响应
                .bodyToFlux(String.class)
                .log() //打印接收到的数据，也可以用doOnNext(System.out::println)
                .take(10) //由于/times是一个无限流，这里取前10个后会导致流被取消
                .blockLast(); //在收到最后一个元素前会阻塞，响应式业务场景中慎用
        return ">> result = " + block; //这里会打印最后一次接收到的数据
    }

    private static String getTimes2() {
        final String block = CLIENT.get()
                .uri("/times")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .accept(MediaType.TEXT_EVENT_STREAM) //配置请求Header：Content-Type: text/event-stream，即SSE
                .exchange()//异步接收服务端响应
                .flatMapMany(res -> {
                    return res.bodyToFlux(String.class)
                            .doOnNext(System.out::println) //这里也可以用log()
                            .take(10);//由于/times是一个无限流，这里取前10个后会导致流被取消
                })
                .blockLast(); //在收到最后一个元素前会阻塞，响应式业务场景中慎用
        return ">> result = " + block; //这里会打印最后一次接收到的数据
    }

    @SneakyThrows
    public static void main(String[] args) {
        //System.out.println(TestMain.getHello());
        //System.out.println(TestMain.getIndex());
        //System.out.println(TestMain.getDemo());
        //System.out.println(TestMain.getUser());
        //System.out.println(TestMain.getUserAll());
        //System.out.println(TestMain.getUserName());
        //System.out.println(TestMain.getUa());
        //System.out.println(TestMain.getTime());
        //System.out.println(TestMain.getDate());
        //System.out.println(TestMain.getTimes());
        //System.out.println(TestMain.getTimes2());
        //TestMain.mongoUsers();
        //TestMain.redisUsers();
        //TestMain.userObject();

        //TestMain.redisSaveUser();

        TestMain.redisGetUser();
    }

    public static void userObject() {
        Mono<User> userMono = CLIENT
                .post()
                .uri("/user")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                //发送请求数据
                .body(Mono.just(new User(10L, "nnnnn")), User.class)
                //.bodyValue(new User(10L,"nnnnn"))
                .retrieve()
                .bodyToMono(User.class);


        //同步
        User block = userMono.block();
        System.out.println(block);

        //异步
        userMono.subscribe(System.out::println);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public static void mongoUsers() {
        CLIENT.get().uri("/mongouser/stream")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .accept(MediaType.APPLICATION_STREAM_JSON) // 配置请求Header：Content-Type: application/stream+json；新版本推荐使用application/x-ndjson
                .exchange() //获取response信息，返回值为ClientResponse，retrive()可以看做是exchange()方法的“快捷版”；
                .flatMapMany(response -> response.bodyToFlux(MongoUser.class))   // 使用flatMap来将ClientResponse映射为Flux；
                .log()
                .doOnNext(System.out::println)  // 只读地peek每个元素，然后打印出来，它并不是subscribe，所以不会触发流；
                .blockLast();   // 在收到最后一个元素前会阻塞，响应式业务场景中慎用
    }

    public static void redisUsers() {
        CLIENT.get().uri("/redisusers/stream")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .accept(MediaType.APPLICATION_STREAM_JSON) // 配置请求Header：Content-Type: application/stream+json；新版本推荐使用application/x-ndjson
                .exchange() //获取response信息，返回值为ClientResponse，retrive()可以看做是exchange()方法的“快捷版”；
                .flatMapMany(response -> response.bodyToFlux(User.class))   // 使用flatMap来将ClientResponse映射为Flux；
                .log()
                .doOnNext(System.out::println)  // 只读地peek每个元素，然后打印出来，它并不是subscribe，所以不会触发流；
                .blockLast();   // 在收到最后一个元素前会阻塞，响应式业务场景中慎用
    }


    public static void redisSaveUser() throws InterruptedException {
        Mono<Boolean> userMono = CLIENT.post().uri("/redisusers/save2")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .body(Mono.just(new User(12L, "张三")), User.class)
                .retrieve()
                .bodyToMono(Boolean.class);

        Boolean aBoolean = userMono.block();
        System.out.println("同步：" + aBoolean);

        //如果保存时存在相同的filedId会返回false，但实际上数据已经保存了
        userMono.subscribe(u -> {
            System.out.println("异步：" + u);
        });

        Thread.sleep(2000);
    }

    public static void redisGetUser() throws InterruptedException {
        Mono<User> userMono = CLIENT.get().uri("/redisusers/user/11")
                //增加了basic安全认证，所以这里需要传递header认证信息
                .header(HttpHeaders.AUTHORIZATION,
                        "Basic " + Base64Utils.encodeToString("admin:123456".getBytes(Charset.defaultCharset())))
                .retrieve()
                .bodyToMono(User.class);

        User user = userMono.block();
        System.out.println("同步：" + user);

        userMono.subscribe(u -> {
            System.out.println("异步：" + u);
        });

        Thread.sleep(2000);
    }
}
