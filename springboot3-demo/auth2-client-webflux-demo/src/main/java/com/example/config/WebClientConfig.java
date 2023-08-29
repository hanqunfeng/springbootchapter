package com.example.config;

/**
 * <h1>WebClientConfig</h1>
 * Created by hanqf on 2023/8/22 16:25.
 */


import com.example.exchange.ResourceServerClient;
import com.example.exchange.WebfluxResourceServerClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

import java.time.Duration;

@Configuration
public class WebClientConfig {

    /**
     * 创建名为 resourceClient 的 bean，类型为 ResourceServerClient。
     * 使用 ReactiveOAuth2AuthorizedClientManager 作为参数创建 HttpServiceProxyFactory，然后使用它创建客户端。
     *
     * @param authorizedClientManager 用于创建 HttpServiceProxyFactory 的 ReactiveOAuth2AuthorizedClientManager 实例
     * @return ResourceServerClient 实例
     * @throws Exception 如果创建客户端时发生错误，则抛出异常
     *
     * 注意这里注册的client，其方法返回值必须是 响应式的，即 Mono 或 Flux，因为这里使用的就是 响应式的 ReactiveOAuth2AuthorizedClientManager，其不支持非响应式的返回值
     */
    @Bean
    public ResourceServerClient resourceClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) throws Exception {
        return httpServiceProxyFactory(authorizedClientManager).createClient(ResourceServerClient.class);
    }

    @Bean
    public WebfluxResourceServerClient webfluxResourceServerClient(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) throws Exception {
        return httpServiceProxyFactory(authorizedClientManager).createClient(WebfluxResourceServerClient.class);
    }


    /**
     * 创建 HttpServiceProxyFactory，以便在创建客户端时使用。
     *
     * @param authorizedClientManager 用于创建 HttpServiceProxyFactory 的 ReactiveOAuth2AuthorizedClientManager 实例
     * @return 创建的 HttpServiceProxyFactory 实例
     */
    private HttpServiceProxyFactory httpServiceProxyFactory(ReactiveOAuth2AuthorizedClientManager authorizedClientManager) {
        // 创建 ServletOAuth2AuthorizedClientExchangeFilterFunction，使用它来处理 OAuth2 认证
        ServerOAuth2AuthorizedClientExchangeFilterFunction oauth2Client = new ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        // 设置默认的 OAuth2 授权客户端
        oauth2Client.setDefaultOAuth2AuthorizedClient(true);

        // 创建 WebClient，应用 OAuth2 认证配置
        WebClient webClient = WebClient.builder().filter(oauth2Client).build();

        // 创建 WebClientAdapter，它允许我们在创建客户端时使用 WebClient
        WebClientAdapter client = WebClientAdapter.forClient(webClient);

        // 创建 HttpServiceProxyFactory，它可用于创建客户端
        return HttpServiceProxyFactory.builder(client)
                .blockTimeout(Duration.ofSeconds(60)) //设置超时时间，默认5秒
                .build();
    }

    /**
     * 创建 OAuth2AuthorizedClientManager 的 bean。
     *
     * @param clientRegistrationRepository 用于管理客户端注册信息的 ReactiveClientRegistrationRepository 实例
     * @param authorizedClientRepository   用于管理授权客户端信息的 ServerOAuth2AuthorizedClientRepository 实例
     * @return OAuth2AuthorizedClientManager 实例
     */
    @Bean
    public ReactiveOAuth2AuthorizedClientManager authorizedClientManager(ReactiveClientRegistrationRepository clientRegistrationRepository, ServerOAuth2AuthorizedClientRepository authorizedClientRepository) {

        // 创建 OAuth2AuthorizedClientProvider，用于获取授权客户端
        ReactiveOAuth2AuthorizedClientProvider authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder().authorizationCode().refreshToken().build();

        // 创建 DefaultOAuth2AuthorizedClientManager，使用它来管理授权客户端
        DefaultReactiveOAuth2AuthorizedClientManager authorizedClientManager = new DefaultReactiveOAuth2AuthorizedClientManager(clientRegistrationRepository, authorizedClientRepository);

        // 设置授权客户端提供程序
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        // 返回 OAuth2AuthorizedClientManager
        return authorizedClientManager;
    }

}
