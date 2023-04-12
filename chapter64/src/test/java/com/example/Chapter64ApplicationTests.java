package com.example;

import com.example.api.HttpApi;
import com.example.model.ChatBodyRequest;
import com.example.model.ChatResponse;
import com.example.model.ChatStreamResponse;
import com.example.support.FluxResponseBodyCallback;
import com.example.support.ResponseBodyCallback;
import com.example.support.SSE;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import retrofit2.Call;
import retrofit2.Response;

@SpringBootTest
class Chapter64ApplicationTests {

    @Autowired
    private HttpApi httpApi;

    private static final String JSON = "{\n" +
            "    \"data\": {\n" +
            "        \"command\": \"chat\",\n" +
            "        \"userinfo\": {\n" +
            "            \"deviceId\": \"xx-xx-xx-xx\"\n" +
            "        },\n" +
            "        \"chatRequest\": {\n" +
            "            \"messages\": [{\n" +
            "                \"role\": \"user\",\n" +
            "                \"content\": \"Are you OK?\"\n" +
            "            }]\n" +
            "        },\n" +
            "        \"stream\": true\n" +
            "    }\n" +
            "}";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final ChatBodyRequest chatBodyRequest;

    static {
        try {
            chatBodyRequest = objectMapper.readValue(JSON, ChatBodyRequest.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void root() {
        final Response<String> result = httpApi.getRoot();
        if (result.isSuccessful()) {
            System.out.println(result.body());
        }
    }


    @Test
    void chat() {
        final Single<ChatResponse> result = httpApi.getChat(chatBodyRequest);
        System.out.println(result.blockingGet());
    }

    @Test
    void chatStream(){
        //封装为指定对象
        final Call<ResponseBody> chatStream = httpApi.getChatStream(chatBodyRequest);
        final Flowable<ChatStreamResponse> stringFlowable = Flowable.<SSE>create(emitter ->
                        chatStream.enqueue(new ResponseBodyCallback(emitter, false)), BackpressureStrategy.BUFFER)
                .map(sse -> objectMapper.readValue(sse.getData(), ChatStreamResponse.class));

        stringFlowable.blockingForEach(System.out::println);

        //封装为字符串
        final Call<ResponseBody> chatStream2 = httpApi.getChatStream(chatBodyRequest);
        final Flowable<String> stringFlowableStr = Flowable.<SSE>create(emitter ->
                        chatStream2.enqueue(new ResponseBodyCallback(emitter, false)), BackpressureStrategy.BUFFER)
                .map(SSE::getData);

        stringFlowableStr.blockingForEach(System.out::println);

    }

    @Test
    void chatStreamFlux(){
        //封装为指定对象
        final Call<ResponseBody> chatStream = httpApi.getChatStream(chatBodyRequest);
        final Flux<ChatStreamResponse> objFlux = Flux.<SSE>create(emitter -> chatStream.enqueue(new FluxResponseBodyCallback(emitter, false)), FluxSink.OverflowStrategy.BUFFER)
                .map(sse -> {
                    try {
                        return objectMapper.readValue(sse.getData(), ChatStreamResponse.class);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }
                });
        objFlux.toIterable(1024).forEach(System.out::println);

        //封装为字符串
        final Call<ResponseBody> chatStream2 = httpApi.getChatStream(chatBodyRequest);
        final Flux<String> stringFlux = Flux.<SSE>create(emitter -> chatStream2.enqueue(new FluxResponseBodyCallback(emitter, false)), FluxSink.OverflowStrategy.BUFFER)
                .map(SSE::getData);
        stringFlux.toIterable(1024).forEach(System.out::println);

    }

}
