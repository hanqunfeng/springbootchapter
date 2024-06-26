package org.example;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ClientHttpConnector;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.http.client.reactive.ReactorResourceFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * <p></p>
 * Created by hanqf on 2020/4/23 21:33.
 */

@Slf4j
public class WebClientUtil {

    private static final ReactorResourceFactory REACTOR_RESOURCE_FACTORY = new ReactorResourceFactory();

    private static final WebClient WEB_CLIENT;

    static {
        REACTOR_RESOURCE_FACTORY.setUseGlobalResources(false);
        REACTOR_RESOURCE_FACTORY.setConnectionProvider(ConnectionProvider.create("httpClient", 50));
        REACTOR_RESOURCE_FACTORY.setLoopResources(LoopResources.create("httpClient", 50, true));

        Function<HttpClient, HttpClient> mapper = client ->
                client.tcpConfiguration(c ->
                        c.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                                .option(ChannelOption.TCP_NODELAY, true)
                                .doOnConnected(conn -> {
                                    conn.addHandlerLast(new ReadTimeoutHandler(10));
                                    conn.addHandlerLast(new WriteTimeoutHandler(10));
                                }));

        ClientHttpConnector connector =
                new ReactorClientHttpConnector(REACTOR_RESOURCE_FACTORY, mapper);

        WEB_CLIENT = WebClient.builder()
                .filter((request, next) -> {  //过滤器，3次重试，header打印
                    log.info(String.format("请求地址: %s", request.url()));
                    log.info(String.format("请求头信息: %s", request.headers()));
                    Mono<ClientResponse> exchange = next.exchange(request).retry(3);
                    ClientResponse clientResponse = exchange.block();
                    log.info(String.format("响应头信息: %s", clientResponse.headers().asHttpHeaders()));
                    return exchange;
                })
                .clientConnector(connector).build();

    }


    public static String get(String url) {
        return get(url, new HashMap<>());
    }

    /**
     * <p>get请求</p>
     *
     * @param url
     * @param map
     * @return java.lang.String
     * @author hanqf
     * 2020/4/24 14:44
     */
    public static String get(String url, Map<String, Object> map) {
        if (map.size() > 0) {
            StringBuilder stringBuffer = new StringBuilder();
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
        Mono<String> mono = WEB_CLIENT.get().uri(url).retrieve().bodyToMono(String.class);
        responseResult = mono.block();

        return responseResult;
    }

    public static byte[] getBytes(String url, Map<String, Object> map) {
        if (map.size() > 0) {
            StringBuilder stringBuffer = new StringBuilder();
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
        byte[] bytes = null;
        Mono<ClientResponse> exchange = WEB_CLIENT.get().uri(url).exchange();
        ClientResponse response = exchange.block();
        if (response.statusCode() == HttpStatus.OK) {
            Mono<byte[]> mono =  response.bodyToMono(byte[].class);
            bytes = mono.block();

            //判断是否需要解压，即服务器返回是否经过了gzip压缩--start
            List<String> header = response.headers().header("Content-Encoding");
            if (header.contains("gzip")) {
                GZIPInputStream gzipInputStream = null;
                ByteArrayOutputStream out = null;
                try {
                    gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
                    out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int offset = -1;
                    while ((offset = gzipInputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, offset);
                    }
                    bytes = out.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        gzipInputStream.close();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            //判断是否需要解压，即服务器返回是否经过了gzip压缩--end
        }

        return bytes;
    }

    public static String post(String url) {
        return post(url, new HashMap<>());
    }

    /**
     * <p>post请求</p>
     *
     * @param url
     * @param params
     * @return java.lang.String
     * @author hanqf
     * 2020/4/24 14:43
     */
    public static String post(String url, Map<String, Object> params) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        if (params != null && params.size() > 0) {
            map.setAll(params);
        }
        String responseResult = null;
        Mono<String> mono = WEB_CLIENT.post().uri(url).bodyValue(map).retrieve().bodyToMono(String.class);
        responseResult = mono.block();

        return responseResult;
    }

    public static byte[] postBytes(String url, Map<String, Object> params) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        if (params != null && params.size() > 0) {
            map.setAll(params);
        }
        byte[] bytes = null;
        Mono<ClientResponse> exchange = WEB_CLIENT.post().uri(url).bodyValue(map).exchange();
        ClientResponse response = exchange.block();
        if (response.statusCode() == HttpStatus.OK) {
            Mono<byte[]> mono =  response.bodyToMono(byte[].class);
            bytes = mono.block();

            //判断是否需要解压，即服务器返回是否经过了gzip压缩--start
            List<String> header = response.headers().header("Content-Encoding");
            if (header.contains("gzip")) {
                GZIPInputStream gzipInputStream = null;
                ByteArrayOutputStream out = null;
                try {
                    gzipInputStream = new GZIPInputStream(new ByteArrayInputStream(bytes));
                    out = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int offset;
                    while ((offset = gzipInputStream.read(buffer)) != -1) {
                        out.write(buffer, 0, offset);
                    }
                    bytes = out.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        gzipInputStream.close();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            //判断是否需要解压，即服务器返回是否经过了gzip压缩--end
        }


        return bytes;
    }

    /**
     * <p>post请求，form表单提交</p>
     *
     * @param url
     * @param params 这里要注意将参数值转为字符串，否则会报类型错误
     * @return java.lang.String
     * @author hanqf
     * 2020/4/24 14:43
     */
    public static String postForm(String url, Map<String, Object> params) {
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        if (params != null && params.size() > 0) {
            for (String key : params.keySet()) {
                map.add(key, params.get(key).toString());
            }
        }
        String responseResult;
        Mono<String> mono = WEB_CLIENT.post().uri(url).contentType(MediaType.APPLICATION_FORM_URLENCODED).bodyValue(map).retrieve().bodyToMono(String.class);
        responseResult = mono.block();

        return responseResult;
    }


    public static String postJson(String url, String json) {
        return postJson(url, json, false);
    }

    /**
     * <p>post请求，json</p>
     *
     * @param url
     * @param json
     * @param gzip
     * @return java.lang.String
     * @author hanqf
     * 2020/4/24 15:45
     */
    public static String postJson(String url, String json, boolean gzip) {
        String responseResult;
        if (gzip) {
            byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
            return postBytes(url, bytes, true);
        } else {
            Mono<String> mono = WEB_CLIENT.post().uri(url).contentType(MediaType.APPLICATION_JSON).bodyValue(json).retrieve().bodyToMono(String.class);
            responseResult = mono.block();
        }

        return responseResult;
    }

    public static String postBytes(String url, byte[] bytes) {
        return postBytes(url, bytes, false);
    }

    /**
     * <p>post请求，字节流</p>
     *
     * @param url
     * @param bytes
     * @param gzip
     * @return java.lang.String
     * @author hanqf
     * 2020/4/24 15:45
     */
    public static String postBytes(String url, byte[] bytes, boolean gzip) {
        String responseResult;
        Mono<String> mono = null;
        WebClient.RequestBodySpec requestBodySpec = WEB_CLIENT.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_OCTET_STREAM);
        if (gzip) {
            try {
                //headers.add("Content-Encoding", "gzip");
                ByteArrayOutputStream originalContent = new ByteArrayOutputStream();
                originalContent.write(bytes);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                GZIPOutputStream gzipOut = new GZIPOutputStream(baos);
                originalContent.writeTo(gzipOut);
                gzipOut.finish();
                bytes = baos.toByteArray();
                mono = requestBodySpec.header("Content-Encoding", "gzip").bodyValue(bytes).retrieve().bodyToMono(String.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mono = requestBodySpec.bodyValue(bytes).retrieve().bodyToMono(String.class);
        }
        responseResult = mono.block();

        return responseResult;

    }


    public static String postStream(String url, InputStream is) {
        return postStream(url, is, false);
    }

    /**
     * <p>post请求，流</p>
     *
     * @param url
     * @param is
     * @param gzip
     * @return java.lang.String
     * @author hanqf
     * 2020/4/22 17:12
     */
    public static String postStream(String url, InputStream is, boolean gzip) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int ch;
        byte[] bytes = null;
        try {
            while ((ch = is.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, ch);
            }
            bytes = byteArrayOutputStream.toByteArray();
            byteArrayOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return postBytes(url, bytes, gzip);

    }


    public static String postFiles(String url, File[] files) {
        return postFiles(url, new HashMap<>(), files);
    }

    /**
     * <p>post请求，文件</p>
     *
     * @param url
     * @param params
     * @param files
     * @return java.lang.String
     * @author hanqf
     * 2020/4/24 15:46
     */
    public static String postFiles(String url, Map<String, Object> params, File[] files) {
        String responseResult;
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        if (params != null && params.size() > 0) {
            map.setAll(params);
        }

        for (File file : files) {
            map.add("files", new FileSystemResource(file));
        }
        Mono<String> mono = WEB_CLIENT.post().uri(url).contentType(MediaType.MULTIPART_FORM_DATA).bodyValue(map).retrieve().bodyToMono(String.class);
        responseResult = mono.block();

        return responseResult;

    }


}
