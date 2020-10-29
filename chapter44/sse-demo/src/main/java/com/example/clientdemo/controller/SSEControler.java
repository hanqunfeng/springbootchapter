package com.example.clientdemo.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <h1>SSE-demo</h1>
 * Created by hanqf on 2020/10/28 17:54.
 */


@RestController
public class SSEControler {
    //建立之后根据订单id，将SseEmitter存到ConcurrentHashMap
    //正常应该存到数据库里面，生成数据库订单，这里我们只是模拟一下
    public static final ConcurrentHashMap<Long, SseEmitter> sseEmitters
            = new ConcurrentHashMap<>();

    //第2步：接受用户建立长连接，表示该用户已支付，已支付就可以生成订单(未确认状态)
    @GetMapping("/orderpay")
    public SseEmitter orderpay(Long payid) {
        //设置默认的超时时间60秒，超时之后服务端主动关闭连接。
        SseEmitter emitter = new SseEmitter(60 * 1000L);
        sseEmitters.put(payid,emitter);
        //超时回调触发
        emitter.onTimeout(() -> sseEmitters.remove(payid));
        //结束之后的回调触发
        emitter.onCompletion(() -> System.out.println("完成！！！"));
        return emitter;
    }

    //第3步：接受支付系统的支付结果告知，表明用户支付成功
    @GetMapping("/payback")
    public String  payback (Long payid){
        //把SSE连接取出来
        SseEmitter emitter = sseEmitters.get(payid);
        try {
            //第4步：由服务端告知浏览器端：该用户支付成功了
            //普通对象类型，会被封装到data中，每一次send，前端都会收到一条数据
            emitter.send("用户支付成功"); //触发前端message事件。
            //也可以指定数据格式，send(Object object, @Nullable MediaType mediaType)
            emitter.send("{\"result\":0}", MediaType.APPLICATION_JSON);
            //触发前端自定义的finish事件
            //SseEventBuilder类型
            emitter.send(SseEmitter.event()
                    //指定事件名称
                    .name("finish")
                    //前端接收的数据
                    .data("请调用服务端关闭连接接口"));
        } catch (IOException e) {
            emitter.completeWithError(e);   //出发前端onerror事件
        }

        return "ok";
    }

    //接口断开sse
    @GetMapping(value = "/over",produces = {MediaType.APPLICATION_JSON_VALUE})
    public String over(Long payid) {
        SseEmitter sseEmitter = sseEmitters.get(payid);
        if (sseEmitter != null) {
            //表示执行完毕，会断开连接
            sseEmitter.complete();
            sseEmitters.remove(payid);
        }
        return "{\"over\":\"success\"}";
    }
}
