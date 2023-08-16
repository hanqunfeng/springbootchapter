package com.example.config;

import com.example.support.security.CP_ImageFilter;
import com.example.support.security.CP_RbacService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.LoggerListener;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.Arrays;
import java.util.List;

/**
 * <h1>springboot升级到3.x.x以后的配置方法</h1>
 */

@Slf4j
@Configuration
@EnableMethodSecurity
public class SpringSecurityConfig {


    @Autowired
    private SessionRegistry sessionRegistry;

    @Autowired
    private PersistentTokenRepository persistentTokenRepository;

    @Autowired
    private CP_RbacService rbacService;

    @Autowired
    private CP_ImageFilter imageFilter;


    private static String[] ignorings = {"/email/**", "/actuator*/**", "/admin*/**", "/logger**",
            "/rabbitmq/**", "/checkcode/**", "/resource/**", "/**/*.jsp",
            "/access/sameLogin.do", "/**/*.json*", "/**/*.xml*", "/druid/**",
            "/forgotPassword.do", "/forgotPasswordEmail.do", "/resetPassword.do"
    };

    private static List<AntPathRequestMatcher> ignoringsMatcherList;

    static {
       ignoringsMatcherList = Arrays.stream(ignorings).map(AntPathRequestMatcher::antMatcher).toList();
    }

    /**
     * 获取AuthenticationManager（认证管理器），登录时认证使用
     *
     * @param authenticationConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        log.info("AuthenticationManager");
        return authenticationConfiguration.getAuthenticationManager();
    }


    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        log.info("HttpSecurity");
        //解决不允许显示在iframe的问题
        http.headers(httpSecurityHeadersConfigurer -> httpSecurityHeadersConfigurer
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::disable));

        // 设置拦截规则
        http.authorizeHttpRequests(authorizationManagerRequestMatcherRegistry -> authorizationManagerRequestMatcherRegistry
                .requestMatchers(ignoringsMatcherList.toArray(AntPathRequestMatcher[]::new)).permitAll()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/login.do*")).permitAll() // 登录请求不拦截
                .requestMatchers(AntPathRequestMatcher.antMatcher("/index.do*")).authenticated()
                .requestMatchers(AntPathRequestMatcher.antMatcher("/*.do*"), AntPathRequestMatcher.antMatcher("/**/*.do*")).access((authentication, context) ->
                        new AuthorizationDecision(rbacService.hasPerssion(context.getRequest(),authentication.get()))));

//                .requestMatchers(AntPathRequestMatcher.antMatcher("/*.do*"),AntPathRequestMatcher.antMatcher("/**/*.do*")).access(webExpressionAuthorizationManager()));

        http.exceptionHandling(exceptionHandlingCustomizer -> exceptionHandlingCustomizer
                .accessDeniedPage("/access/denied.do"));


        // 开启默认登录页面
        http.formLogin(httpSecurityFormLoginConfigurer -> httpSecurityFormLoginConfigurer
                .loginPage("/login.do")
                .failureUrl("/login.do?login_error=1")
                .defaultSuccessUrl("/index.do", true)
                .loginProcessingUrl("/j_spring_security_check") // 默认 /login
                .usernameParameter("j_username")  // 默认username
                .passwordParameter("j_password")  // 默认password
                .permitAll());


        //开启csrf，默认开启，csrf不会拦截get请求
        http.csrf(httpSecurityCsrfConfigurer -> httpSecurityCsrfConfigurer
                //关闭仅支持http浏览器请求，这样ajax和postMan都可以访问，header需要带上X-CSRF-TOKEN
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                //哪些不需要csrf，非浏览器直接访问的地址需要进行屏蔽，因为csrf标签只有页面会自动生成，另外认证接口也需要屏蔽，因为需要第一次获取CSRF-TOKEN
                .ignoringRequestMatchers(AntPathRequestMatcher.antMatcher("/**/json.do*"), AntPathRequestMatcher.antMatcher("/**/xml.do*")));

        // 自定义注销，这里需要注意的是，如果启用了csrf(默认就是开启)，则logout只能是post提交，如果要get提交，则必须如下配置
        http.logout(httpSecurityLogoutConfigurer -> httpSecurityLogoutConfigurer
                // 在注销时清除认证信息
                .clearAuthentication(true)
                .logoutRequestMatcher(AntPathRequestMatcher.antMatcher("/logout.do")) //get
                //.logoutUrl("/logout.do")  //post
                .logoutSuccessUrl("/login.do")
                // 在注销时使HttpSession失效
                .invalidateHttpSession(true));


        // session管理
        http.sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer
                //默认开启session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                //每次登录验证将创建一个新的session
                .sessionFixation().migrateSession()
                //同一个用户最大的登录数量
                .maximumSessions(1)
                //true表示已经登录就不予许再次登录，false表示允许再次登录但是之前的登录会下线。
                .maxSessionsPreventsLogin(false)
                //session被下线(超时)之后的显示页面
                .expiredUrl("/access/sameLogin.do")
                .sessionRegistry(sessionRegistry));


        // RemeberMe 两周内免登录
        http.rememberMe(httpSecurityRememberMeConfigurer -> httpSecurityRememberMeConfigurer
                .rememberMeParameter("_spring_security_remember_me") // 默认 remember-me
                .rememberMeCookieName("remember-me-cookie")  //保存在浏览器端的cookie的名称，如果不设置默认也是remember-me
                .tokenValiditySeconds(60 * 60 * 24 * 14)
                .tokenRepository(persistentTokenRepository));

        //设置userDetailsService
        http.userDetailsService(userDetailsService());

        // 设置过滤器，这里是验证码过滤器
        http.addFilterBefore(imageFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * web表达式认证管理器
     */
//    private WebExpressionAuthorizationManager webExpressionAuthorizationManager() {
//        final var expressionHandler = new DefaultHttpSecurityExpressionHandler();
//        expressionHandler.setApplicationContext(applicationContext);
//        final var authorizationManager = new WebExpressionAuthorizationManager("@rbacService.hasPerssion(request,authentication)");
//        // 一定要设置expressionHandler，否则不生效
//        authorizationManager.setExpressionHandler(expressionHandler);
//        return authorizationManager;
//    }


    /**
     * 基于内存的userDetailsService
     */
    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        UserDetails user = User.builder()
                .username("user")
                // 123456
                .password("$2a$10$JHj.XB.5RtpY60JEuTTGjuIT4m.hYT1yWoevJ6inU6Q7JE1qcvTYC")
                .roles("USER")
                .build();

        UserDetails admin = User.builder()
                .username("admin")
                // 123456
                .password("$2a$10$JHj.XB.5RtpY60JEuTTGjuIT4m.hYT1yWoevJ6inU6Q7JE1qcvTYC")
                .roles("ADMIN")
                .build();

        return new InMemoryUserDetailsManager(user, admin);
    }

    /**
     * 认证日志
     */
    @Bean
    public LoggerListener loggerListener() {
        log.info("org.springframework.security.authentication.event.LoggerListener");
        return new LoggerListener();
    }

    /**
     * 密码加密策略
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        log.info("BCryptPasswordEncoder");
        return new BCryptPasswordEncoder();
    }

}
