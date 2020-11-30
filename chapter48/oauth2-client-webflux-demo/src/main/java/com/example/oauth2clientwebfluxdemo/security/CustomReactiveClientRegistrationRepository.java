package com.example.oauth2clientwebfluxdemo.security;

import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import reactor.core.publisher.Mono;

/**
 * <h1>ReactiveClientRegistrationRepository</h1>
 * Created by hanqf on 2020/11/30 14:37.
 */

//@Component
public class CustomReactiveClientRegistrationRepository implements ReactiveClientRegistrationRepository {



    @Override
    public Mono<ClientRegistration> findByRegistrationId(String registrationId) {

        //查询数据库，封装ClientRegistration
        ClientRegistration registration = ClientRegistration.withRegistrationId("flux-client")
                    .clientId("postman")
                    .clientSecret("postman")
                    .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                    .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
                    .redirectUriTemplate("http://localhost:8099/login/oauth2/code/flux-client")
                    .authorizationUri("http://localhost:8080/oauth/authorize")
                    .userInfoUri("http://localhost:8080/userInfo")
                    .userNameAttributeName("username")
                    .tokenUri("http://localhost:8080/oauth/token")
                    //.jwkSetUri("http://localhost:8080/.well-known/jwks.json")
                    .scope("any")
                    .build();
        return Mono.just(registration);
    }
}
