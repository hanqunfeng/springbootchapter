package com.example.api;

import com.example.model.ChatBodyRequest;
import com.example.model.ChatResponse;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import io.reactivex.Single;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

/**
 * <h1>接口定义</h1>
 * Created by hanqf on 2023/4/11 17:35.
 */


@RetrofitClient(baseUrl = "${openai-api-web.baseUrl}")
public interface HttpApi {

    /**
     * 根路径调用必须加上path
     */
    @GET(value = "/openai-api/")
    Response<String> getRoot();

    @POST(value = "app")
    Single<ChatResponse> getChat(@Body ChatBodyRequest chatBodyRequest);

    @Streaming
    @POST(value = "app")
    Call<ResponseBody> getChatStream(@Body ChatBodyRequest chatBodyRequest);

}
