package org.example.common.azure;

import lombok.extern.slf4j.Slf4j;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.util.Objects;

/**
 * <h1></h1>
 * Created by hanqf on 2023/4/24 14:06.
 */

@Slf4j
public class AzureAuthenticationInterceptor implements Interceptor {

    private final String token;

    private static final String key = "isEmptyAzure";

    AzureAuthenticationInterceptor(String token) {
        Objects.requireNonNull(token, "OpenAI token required");
        this.token = token;
    }

    private void sendEmail(Integer code) {
       //ToDo
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request()
                .newBuilder()
                .header("api-key", token)
                .build();

        log.info(String.format("请求地址: %s", request.url()));

        Response response = chain.proceed(request);
        final int code = response.code();
        log.info("响应状态码:" + code);
        if (!response.isSuccessful()) {
            //429 表示key额度超限
            //401 表示无效的apiKey invalid_api_key
            if (code == 429 || code == 401) {
                sendEmail(code);
            }
        }

        Headers headers = response.headers();
        StringBuilder stringBuffer = new StringBuilder();
        for (int i = 0; i < headers.size(); i++) {
            stringBuffer.append(headers.name(i)).append(":").append(headers.value(i)).append(",");
        }
        log.info(String.format("响应头信息: %s", stringBuffer));
        return response;
    }
}
