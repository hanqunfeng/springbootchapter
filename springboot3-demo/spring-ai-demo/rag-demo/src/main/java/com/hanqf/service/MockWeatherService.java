package com.hanqf.service;


import java.util.function.Function;

/**
 * <h1></h1>
 * Created by hanqf on 2024/5/17 16:25.
 */


public class MockWeatherService implements Function<MockWeatherService.Request, MockWeatherService.Response> {

    public enum Unit {C, F}

    public record Request(String location, Unit unit) {
    }

    public record Response(double temp, Unit unit) {
    }

    public Response apply(Request request) {
        return new Response(Math.random() * 40, Unit.C);
    }
}
