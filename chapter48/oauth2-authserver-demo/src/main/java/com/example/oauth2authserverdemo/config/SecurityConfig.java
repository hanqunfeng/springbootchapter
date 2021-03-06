package com.example.oauth2authserverdemo.config;

import com.example.oauth2authserverdemo.security.CustomSecurityProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collection;
import java.util.stream.Collectors;

/**
 * Spring-Security配置类,继承WebSecurityConfigurerAdapter
 *
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomSecurityProperties customSecurityProperties;

    /**
     * 引入密码加密类
     *
     * @return
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * 为了测试方便，这里使用内存用户模型，真实系统请使用基于RBAC的权限模型
     * 用户策略设置，这里使用内存用户策略，自定义策略需要实现UserDetailsService接口
     */
    @Override
    @Bean
    public UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager inMemoryUserDetailsManager = new InMemoryUserDetailsManager();
        User.UserBuilder builder = User.builder().passwordEncoder(passwordEncoder()::encode);
        inMemoryUserDetailsManager.createUser(builder.username("admin").password("123456").roles("admin").build());
        inMemoryUserDetailsManager.createUser(builder.username("guest").password("123456").roles("guest").build());
        inMemoryUserDetailsManager.createUser(builder.username("user").password("123456").roles("user").build());
        return inMemoryUserDetailsManager;
    }


    /**
     * 指定不拦截的路径规则
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        //设置不需要拦截的路径，也就是不需要认证的路径
        web.ignoring().antMatchers(customSecurityProperties.getIgnoring());
    }

    /**
     * 配置URL访问授权,必须配置authorizeRequests(),否则启动报错,说是没有启用security技术。
     * 注意:在这里的身份进行认证与授权没有涉及到OAuth的技术：当访问要授权的URL时,请求会被DelegatingFilterProxy拦截,
     * 如果还没有授权,请求就会被重定向到登录界面。在登录成功(身份认证并授权)后,请求被重定向至之前访问的URL。
     *
     * @param http
     * @throws Exception
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/oauth/login")
                .loginProcessingUrl("/login")
                //.defaultSuccessUrl("/")
                .failureForwardUrl("/oauth/login?error=true")
                .permitAll(); //登记界面，默认是permitAll

        http.authorizeRequests()
                .antMatchers(customSecurityProperties.getPermitAll()).permitAll() //不用身份认证可以访问
                .mvcMatchers("/.well-known/jwks.json").permitAll() //开放JWK SET端点，提供给资源服务器访问获取公钥信息
                .anyRequest().authenticated(); //其它的请求要求必须有身份认证

        http.csrf().disable();

        //开启跨域
        http.cors();

        // session管理
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);


        //认证服务器同时作为资源服务器，其目的是可以对外提供访问的资源，比如"客户端认证获取用户信息"
        //鉴权时只支持Bearer Token的形式，不支持url后加参数access_token
        http.oauth2ResourceServer().jwt() //开启oauth2资源认证
                //默认情况下，权限是scope，而我们希望使用的是用户的角色，所以这里需要通过转换器进行处理
                .jwtAuthenticationConverter(jwt -> { //通过自定义Converter来指定权限，Converter是函数接口，当前上下问参数为JWT对象
                    Collection<SimpleGrantedAuthority> authorities =
                            ((Collection<String>) jwt.getClaims()
                                    .get("authorities")).stream() //获取JWT中的authorities
                                    .map(SimpleGrantedAuthority::new)
                                    .collect(Collectors.toSet());

                    //如果希望保留scope的权限，可以取出scope数据然后合并到一起，这样因为不是以ROLE_开头，所以需要使用hasAuthority('SCOPE_any')的形式
                    Collection<SimpleGrantedAuthority> scopes = ((Collection<String>) jwt.getClaims()
                            .get("scope")).stream().map(scope -> new SimpleGrantedAuthority("SCOPE_" + scope))
                            .collect(Collectors.toSet());
                    //合并权限
                    authorities.addAll(scopes);
                    return new JwtAuthenticationToken(jwt, authorities);
                });
    }


    /**
     * 支持 password 模式(配置)
     *
     * @return
     * @throws Exception
     */
    @Bean(name = BeanIds.AUTHENTICATION_MANAGER)
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }


    /**
     * 跨域配置类
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        //开放哪些ip、端口、域名的访问权限，星号表示开放所有域
        corsConfiguration.addAllowedOrigin("*");
        //corsConfiguration.setAllowedOrigins(Arrays.asList("http://localhost:8080","http://localhost:8081"));
        //开放哪些Http方法，允许跨域访问
        corsConfiguration.addAllowedMethod("*");
        //corsConfiguration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE"));
        //允许HTTP请求中的携带哪些Header信息
        corsConfiguration.addAllowedHeader("*");
        //是否允许发送Cookie信息
        corsConfiguration.setAllowCredentials(true);

        //添加映射路径，“/**”表示对所有的路径实行全局跨域访问权限的设置
        UrlBasedCorsConfigurationSource configSource = new UrlBasedCorsConfigurationSource();
        configSource.registerCorsConfiguration("/**", corsConfiguration);

        return configSource;
    }

    /**
     * 认证事件监听器，打印日志
     * <p>
     * 如：认证失败/成功、注销，等等
     */
    @Bean
    public org.springframework.security.authentication.event.LoggerListener loggerListener() {
        org.springframework.security.authentication.event.LoggerListener loggerListener = new org.springframework.security.authentication.event.LoggerListener();
        return loggerListener;
    }

    /**
     * 资源访问事件监听器，打印日志
     * <p>
     * 如：没有访问权限
     */
    @Bean
    public org.springframework.security.access.event.LoggerListener eventLoggerListener() {
        org.springframework.security.access.event.LoggerListener eventLoggerListener = new org.springframework.security.access.event.LoggerListener();
        return eventLoggerListener;
    }
}
