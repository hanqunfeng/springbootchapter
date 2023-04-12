package com.example.converterFactory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * <h1>基础类型转换器</h1>
 * Created by hanqf on 2023/4/12 15:43.
 */


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BasicTypeConverterFactory extends Converter.Factory {

    public static final BasicTypeConverterFactory INSTANCE = new BasicTypeConverterFactory();

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations,
                                                          Annotation[] methodAnnotations, Retrofit retrofit) {
        return null;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        if (String.class.getTypeName().equals(type.getTypeName())) {
            return new StringResponseConverter();
        } else if (Integer.class.getTypeName().equals(type.getTypeName())) {
            return new IntegerResponseConverter();
        } else if (Long.class.getTypeName().equals(type.getTypeName())) {
            return new LongResponseConverter();
        } else if (Boolean.class.getTypeName().equals(type.getTypeName())) {
            return new BooleanResponseConverter();
        } else if (Float.class.getTypeName().equals(type.getTypeName())) {
            return new FloatResponseConverter();
        } else if (Double.class.getTypeName().equals(type.getTypeName())) {
            return new DoubleResponseConverter();
        } else {
            return null;
        }

    }

    private static final class StringResponseConverter implements Converter<ResponseBody, String> {

        @Override
        public String convert(ResponseBody value) throws IOException {
            return value.string();
        }
    }

    private static class IntegerResponseConverter implements Converter<ResponseBody, Integer> {

        @Override
        public Integer convert(ResponseBody value) throws IOException {
            return Integer.valueOf(value.string());
        }
    }

    private static class LongResponseConverter implements Converter<ResponseBody, Long> {
        @Override
        public Long convert(ResponseBody value) throws IOException {
            return Long.valueOf(value.string());
        }
    }

    private static class BooleanResponseConverter implements Converter<ResponseBody, Boolean> {
        @Override
        public Boolean convert(ResponseBody value) throws IOException {
            return Boolean.valueOf(value.string());
        }
    }

    private static class FloatResponseConverter implements Converter<ResponseBody, Float> {
        @Override
        public Float convert(ResponseBody value) throws IOException {
            return Float.valueOf(value.string());
        }
    }

    private static class DoubleResponseConverter implements Converter<ResponseBody, Double> {
        @Override
        public Double convert(ResponseBody value) throws IOException {
            return Double.valueOf(value.string());
        }
    }
}

