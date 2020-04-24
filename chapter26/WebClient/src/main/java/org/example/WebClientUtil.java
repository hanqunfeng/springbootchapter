package org.example;

import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * <p></p>
 * Created by hanqf on 2020/4/23 21:33.
 */


public class WebClientUtil {

    private static WebClient webClient = WebClient.create();

    public static String get(String url) {
        return get(url, new HashMap<>());
    }

    public static String get(String url, Map<String, Object> map) {
        if (map.size() > 0) {
            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.append(url);
            if (url.contains("?")) {
                stringBuffer.append("&");
            } else {
                stringBuffer.append("?");
            }
            for (String key : map.keySet()) {
                stringBuffer.append(key).append("=").append(map.get(key).toString()).append("&");
            }
            url = stringBuffer.toString();
        }
        String responseResult = null;
        Mono<String> mono = webClient.get().uri(url).retrieve().bodyToMono(String.class);
        responseResult = mono.block();

        return responseResult;
    }

    public static String post(String url) {
        return post(url, new HashMap<>());
    }

    public static String post(String url, Map<String, Object> params) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        if (params != null && params.size() > 0) {
            map.setAll(params);
        }
        String responseResult = null;
        Mono<String> mono = webClient.post().uri(url).bodyValue(map).retrieve().bodyToMono(String.class);
        responseResult = mono.block();

        return responseResult;
    }


}
