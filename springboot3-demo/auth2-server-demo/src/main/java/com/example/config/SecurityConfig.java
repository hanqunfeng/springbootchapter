package com.example.config;

import com.example.security.CustomAccessDeniedHandler;
import com.example.utils.JksKeyUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext;
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.security.config.Customizer.withDefaults;

@Slf4j
@Configuration
public class SecurityConfig {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    /**
     * access_token认证后没有对应的权限时的处理方式
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler(objectMapper);
    }

    @Bean
    @Order(1)//指定执行优先级
    public SecurityFilterChain asSecurityFilterChain(HttpSecurity http) throws Exception {

        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);

        // 为 OAuth2 认证服务器添加 OIDC 支持，不设置这个是不能接受scope范围为OidcScopes中的配置
        // 同时  client和resource 通过配置 issuer-uri: http://localhost:9090 也不能与 server建立连接
        // http://127.0.0.1:9090/.well-known/openid-configuration 浏览器访问这个地址就会获取所有的认证相关地址，所以如果服务端配置oidc，client只需要配置issuer-uri即可
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .authorizationServerSettings(authorizationServerSettings())
                .oidc(withDefaults());

        //当未经身份验证的用户尝试访问受保护的资源时，将用户重定向到Security的默认登录页面
        http.exceptionHandling(e -> e
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")));

        http.authenticationProvider(authenticationProvider());

        return http.build();
    }

    @Bean
    @Order(2)
    //直接通过server的表单登录与身份验证的请求授权，这里就是正常的表单登录配置方法
    public SecurityFilterChain appSecurityFilterChain(HttpSecurity http) throws Exception {

        http.formLogin(withDefaults());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/demo").permitAll()
                .requestMatchers("/user2").hasAuthority("SCOPE_user:info")  // 此时登录用户是没有该权限的，因为这里并不是客户端认证
                .requestMatchers("/user").hasAuthority("ROLE_admin")  // 用户有该权限
                .anyRequest().authenticated());

        // 可以配置不同的 AuthenticationProvider 或者 UserDetailsService
        http.authenticationProvider(authenticationProvider());

        // 登录后没有权限时的处理方式
        http.exceptionHandling(e -> e.accessDeniedHandler(accessDeniedHandler()));
        return http.build();

    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    //该 Bean 提供了一个存储在内存中的用户
    @Bean
    public UserDetailsService userDetailsService() {
        User.UserBuilder builder = User.builder().passwordEncoder(passwordEncoder()::encode);
        UserDetails userDetails1 = builder.username("admin").password("123456").roles("admin").build();
        UserDetails userDetails2 = builder.username("guest").password("123456").roles("guest").build();
        UserDetails userDetails3 = builder.username("user").password("123456").roles("user").build();
        return new InMemoryUserDetailsManager(userDetails1, userDetails2, userDetails3);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 自定义OAuth2 Token，在claim中添加一些内容，这里将用户的权限也加入到claim中了
     */
    @Bean
    OAuth2TokenCustomizer<JwtEncodingContext> tokenCustomizer() {
        return context -> {
            Authentication principal = context.getPrincipal();

            // client认证后的用户信息是基于 id_token 创建的
            if (context.getTokenType().getValue().equals("id_token")) {
                context.getClaims()
                        .claim("Test", "Test Id Token");
            }
            // 当client需要请求服务端资源时，会使用 access_token
            if (context.getTokenType().getValue().equals("access_token")) {
                Set<String> authorities = principal.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
                context.getClaims()
                        .claim("Test", "Test Access Token")
                        .claim("authorities", authorities)
                        .claim("user", principal.getName());
            }
        };
    }


    /*
        向OAuth2认证服务器注册一个客户端应用程序进行授权
        该 Bean 提供了一个内存中的注册客户端存储，用于 OAuth2 认证服务器的客户端授权
        客户端应使用此处设置的值作为配置项

        这里可以是基于内存的配置方法：InMemoryRegisteredClientRepository
        也可以配置为基于数据库的：JdbcRegisteredClientRepository
    */
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        //生成随机UUID作为客户端唯一标识，避免多个客户端时ID冲突
        RegisteredClient registeredClient = RegisteredClient.withId(UUID.randomUUID().toString())
                .clientName("my-oauth2") // 设置客户端名称
                .clientId("client") //设置授权客户端ID
                .clientSecret(passwordEncoder().encode("secret")) //设置客户端密钥
                .scopes(strings -> {
                    strings.add(OidcScopes.OPENID);//设置客户端的范围，这里使用了 OpenID Connect 的标准范围
                    strings.add("user:info");
                })
                /*
                    设置客户端的重定向 URI，当用户授权后，OAuth2 认证服务器将重定向到该 URI
                    由于OAuth2认证服务器的安全性设置，此处必须使用127.0.0.1
                    使用localhost会导致拒绝重定向
                    redirectUri 不要求存在，固定写法
                 */
                .redirectUris(redirectUri -> {
                    redirectUri.add("http://127.0.0.1:8080/login/oauth2/code/my-oauth2"); //客户端回调，固定写法
                    redirectUri.add("https://www.baidu.com"); //用于测试获取验证码模式的code
                })
                //设置客户端的身份验证方法
                .clientAuthenticationMethods(clientAuthenticationMethods -> {
                    clientAuthenticationMethods.add(ClientAuthenticationMethod.CLIENT_SECRET_BASIC); // Basic Auth 身份验证方法
                    clientAuthenticationMethods.add(ClientAuthenticationMethod.CLIENT_SECRET_POST); // Post参数 client_id,client_secret
                })
                //设置客户端的授权类型，这里使用了授权码授权类型和刷新token
                .authorizationGrantTypes(authorizationGrantTypes -> {
                    authorizationGrantTypes.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                    authorizationGrantTypes.add(AuthorizationGrantType.REFRESH_TOKEN);
                })
                .tokenSettings(TokenSettings.builder()
                        .accessTokenTimeToLive(Duration.ofHours(12)) // accessToken有效期，默认12小时
                        .refreshTokenTimeToLive(Duration.ofDays(30)) // refreshToken有效期，默认30天
                        .build())
                .clientSettings(ClientSettings.builder()
                        .requireAuthorizationConsent(true)  // 登录时要求用户确认scope，默认为false
                        .build())
                .build();

//        return new InMemoryRegisteredClientRepository(registeredClient);


        // 基于数据库，要先在数据库中建表: oauth2_registered_client
        final JdbcRegisteredClientRepository jdbcRegisteredClientRepository = new JdbcRegisteredClientRepository(jdbcTemplate);
        // 第一次运行时初始化数据，这里是方便测试功能，正常情况下需要搞个CURD
//        jdbcRegisteredClientRepository.save(registeredClient);
        return jdbcRegisteredClientRepository;
    }

    //该Bean用于配置OAuth2认证服务器，该例中我们无需配置
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
                .build();
    }


    //解码Jwt令牌
    @Bean
    public JwtDecoder jwtDecoder() {
        log.info("JwtDecoder");
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource());
    }

    //提供Jwt令牌
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        JWK rsaKey = generateRsa();
        JWKSet jwkSet = new JWKSet(rsaKey);
        return (jwkSelector, securityContext) -> jwkSelector.select(jwkSet);
    }

    //生成JWK密钥对
    public static JWK generateRsa() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        return new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();
    }

    //创建 KeyPair 规则，可以自定义，比如使用 jks
    static KeyPair generateRsaKey() {
        KeyPair keyPair;
        try {
            // 动态创建rsa，因为没有生成公钥文件，这种方式需要 resource 服务连接 oauth-server 进行 token 的解析
//            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
//            keyPairGenerator.initialize(2048);
//            keyPair = keyPairGenerator.generateKeyPair();

            // 下面两种方式，resource 服务都可以不连接 oauth-server 进行 token 的解析，而是配置本地公钥即可
            // 基于ras公钥与私钥文件创建
//            keyPair = RsaKeyUtil.getKeyPair("classpath:id_key_rsa", "classpath:id_key_rsa.pub");

            // 基于jks密钥文件创建
            keyPair = JksKeyUtil.getKeyPair("classpath:jks_key.jks", "jks", "123456", "123456");
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
        return keyPair;

    }
}
