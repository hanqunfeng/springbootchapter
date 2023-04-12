package com.example.callAdapterFactory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.Request;
import org.springframework.util.Assert;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Response;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * <h1>适配Response</h1>
 * Created by hanqf on 2023/4/12 15:38.
 */


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ResponseCallAdapterFactory extends CallAdapter.Factory {

    public static final ResponseCallAdapterFactory INSTANCE = new ResponseCallAdapterFactory();

    @Override
    public CallAdapter<?, ?> get(Type returnType, Annotation[] annotations, Retrofit retrofit) {
        if (Response.class.isAssignableFrom(getRawType(returnType))) {
            return new ResponseCallAdapter<>(returnType);
        }
        return null;
    }

    static final class ResponseCallAdapter<R> implements CallAdapter<R, Response<R>> {

        private final Type returnType;

        ResponseCallAdapter(Type returnType) {
            this.returnType = returnType;
        }

        @Override
        public Type responseType() {
            ParameterizedType parameterizedType = (ParameterizedType)returnType;
            Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
            Assert.notEmpty(actualTypeArguments, "Response must specify generic parameters!");
            return actualTypeArguments[0];
        }

        @Override
        public Response<R> adapt(Call<R> call) {
            Request request = call.request();
            try {
                return call.execute();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
