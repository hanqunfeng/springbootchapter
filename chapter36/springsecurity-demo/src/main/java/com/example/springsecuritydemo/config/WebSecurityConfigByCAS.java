package com.example.springsecuritydemo.config;

import com.example.springsecuritydemo.common.CasProperties;
import com.example.springsecuritydemo.common.DynamicRoleVoter;
import org.jasig.cas.client.session.SingleSignOutFilter;
import org.jasig.cas.client.validation.Cas30ServiceTicketValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.cas.ServiceProperties;
import org.springframework.security.cas.authentication.CasAuthenticationProvider;
import org.springframework.security.cas.web.CasAuthenticationEntryPoint;
import org.springframework.security.cas.web.CasAuthenticationFilter;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>cas认证</p>
 * Created by hanqf on 2020/9/10 10:14.
 */

@ConditionalOnProperty(prefix = "security", name = "config", havingValue = "cas")
@Configuration
// 启用web安全认证，springboot mvc环境可以不用配置，自动配置WebSecurityEnablerConfiguration中已经启用，参考：http://www.zhihesj.com/?id=26
@EnableWebSecurity
//开启方法级别的安全验证注解，参考 https://www.jianshu.com/p/77b4835b6e8e
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigByCAS extends WebSecurityConfigurerAdapter {


    @Autowired
    private CasProperties casProperties;

    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    @Bean
    public DynamicRoleVoter dynamicRoleVoter() {
        return new DynamicRoleVoter();
    }

    /**
     * 用户管理，用于设置用户的验证方式
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 绑定UserDetailsService
        //configure(HttpSecurity http)方法中如果绑定了casAuthenticationProvider，此处也可以不进行绑定，则该方法就不需要Override
        auth.authenticationProvider(casAuthenticationProvider());

    }

    /**
     * 用户策略设置，这里使用内存用户策略，自定义策略需要实现UserDetailsService接口
     * <p>
     * 此时这里的用户名用于验证客户端是否存在该用户，这里密码没用，只依赖cas服务端的数据
     * <p>
     * 此处仅仅为了演示方便，正式使用时还是自定义一个UserDetailsService实现类吧
     */
    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        User.UserBuilder builder = User.builder();
        UserDetails userDetails1 = builder.username("casuser").password("123456").roles("admin").build();
        UserDetails userDetails2 = builder.username("admin").password("123456").roles("admin").build();
        return new InMemoryUserDetailsManager(userDetails1, userDetails2);
    }

    /**
     * 指定不拦截的路径规则
     */
    @Override
    public void configure(WebSecurity web) throws Exception {
        //设置不需要拦截的路径，也就是不需要认证的路径
        web.ignoring().antMatchers("/accessDenied*", "/error*","/fail*");
    }

    /**
     * 过滤器管理，用于设置资源权限、登录注销方式、session管理方式，自定义过滤器绑定，等等
     * <p>
     * 表达式	                            描述
     * hasRole([role])	                用户拥有制定的角色时返回true （Spring security默认会带有ROLE_前缀）,去除参考Remove the ROLE_
     * hasAnyRole([role1,role2])	    用户拥有任意一个制定的角色时返回true
     * hasAuthority([authority])	    等同于hasRole,但不会带有ROLE_前缀
     * hasAnyAuthority([auth1,auth2])	等同于hasAnyRole
     * permitAll	                    永远返回true
     * denyAll	                        永远返回false
     * anonymous	                    当前用户是anonymous时返回true
     * rememberMe	                    当前勇士是rememberMe用户返回true
     * authentication	                当前登录用户的authentication对象
     * fullAuthenticated	            当前用户既不是anonymous也不是rememberMe用户时返回true
     * hasIpAddress('192.168.1.0/24'))	请求发送的IP匹配时返回true
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        //super.configure(http);

        //解决不支持iframe的问题
        http.headers().frameOptions().disable();

        // 拦截路径规则设置
        http.authorizeRequests()
                .accessDecisionManager(accessDecisionManager()) //访问决策管理器，默认支持三种决策者：RoleVoter、AuthenticatedVoter、WebExpressionVoter。一般需要添加自定义决策者
                .anyRequest().access("hasRole('admin')"); // 基于表达式 参考：https://my.oschina.net/liuyuantao/blog/1924776


        // cas
        http.exceptionHandling()
                .accessDeniedPage("/accessDenied") //访问拒绝页面，认证后访问没有权限的路径，将跳转到指定路径，这里不能直接跳转到/login
                .authenticationEntryPoint(casAuthenticationEntryPoint())    //设置自定义授权认证类
                .and()
                .authenticationProvider(casAuthenticationProvider())  //此处绑定casAuthenticationProvider，则configure(AuthenticationManagerBuilder auth)方法中也可以不绑定
                .addFilter(casAuthenticationFilter())                       //设置CAS授权过滤器
                .addFilterBefore(casLogoutFilter(), LogoutFilter.class)      //登出配置
                .addFilterBefore(singleSignOutFilter(), CasAuthenticationFilter.class);

        ////开启登录功能
        //http.formLogin();
        //
        ////开启登出功能
        //http.logout();

        // 开启csrf，默认为开启，生成页面时会自动在每个form中增加一个隐藏属性<input type="hidden" name="_csrf" value="95e8706b-8d22-4d62-9a27-3da5993e0a7d">，
        // 也可以手工配置(没必要)，thymeleaf中就是<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />，js中如果需要使用时也可以使用该属性
        //http.csrf().ignoringAntMatchers("/**/*.json","/**/*.xml"); //哪些不需要csrf，非浏览器直接访问的地址需要进行屏蔽，因为csrf标签只有页面会自动生成

        // 关闭csrf，一般情况下，如果服务中大部分请求都是基于浏览器访问的，则应该开启csrf，如果大部分都是接口，则可以关闭csrf，因为只有在页面的form中才会自动生成隐藏属性，所以接口需要自己在参数中传递，反倒麻烦
        http.csrf().disable();


        // session管理
        http.sessionManagement()
                //.invalidSessionUrl("/login") // 无效session跳转地址
                .maximumSessions(1) // 最大并发登录，这里设置为1次，只要用户重新登录，则之前的登录就会失效
                //.maxSessionsPreventsLogin(true) // 达到最大并发后阻止后续的登录，默认false
                .expiredUrl("/sameLogin"); // 达到最大并发后，前一个用户session会失效，再次访问资源时的跳转地址
    }

    //Cas认证入口
    @Bean
    public CasAuthenticationEntryPoint casAuthenticationEntryPoint() {
        //用于配置Cas认证入口信息
        CasAuthenticationEntryPoint casAuthenticationEntryPoint = new CasAuthenticationEntryPoint();
        //设置登录地址
        casAuthenticationEntryPoint.setLoginUrl(casProperties.getCasServerLoginUrl());
        //返回的服务信息
        casAuthenticationEntryPoint.setServiceProperties(serviceProperties());
        return casAuthenticationEntryPoint;
    }

    /****
     * 服务配置信息
     *
     * 登录成功后的返回地址
     */
    @Bean
    public ServiceProperties serviceProperties() {
        //服务信息配置
        ServiceProperties serviceProperties = new ServiceProperties();
        //设置应用访问信息
        serviceProperties.setService(casProperties.getAppServerUrl() + casProperties.getAppLoginUrl());
        //针对非空登录用户进行身份校验
        serviceProperties.setAuthenticateAllArtifacts(true);
        return serviceProperties;
    }

    /**
     * CAS认证过滤器
     */
    @Bean
    public CasAuthenticationFilter casAuthenticationFilter() throws Exception {
        //设置CAS授权过滤器
        CasAuthenticationFilter casAuthenticationFilter = new CasAuthenticationFilter();
        //集成SpringSecurity授权认证器
        casAuthenticationFilter.setAuthenticationManager(authenticationManager());
        casAuthenticationFilter.setFilterProcessesUrl(casProperties.getAppLoginUrl());

        //认证成功控制器
        SimpleUrlAuthenticationSuccessHandler simpleUrlAuthenticationSuccessHandler = new SimpleUrlAuthenticationSuccessHandler();
        simpleUrlAuthenticationSuccessHandler.setDefaultTargetUrl("/");
        simpleUrlAuthenticationSuccessHandler.setAlwaysUseDefaultTargetUrl(true); // true:无论请求什么地址，登录成功后都跳转到缺省地址，false:跳转到请求地址
        casAuthenticationFilter.setAuthenticationSuccessHandler(simpleUrlAuthenticationSuccessHandler);

        //认证失败控制器
        SimpleUrlAuthenticationFailureHandler simpleUrlAuthenticationFailureHandler = new SimpleUrlAuthenticationFailureHandler();
        simpleUrlAuthenticationFailureHandler.setDefaultFailureUrl("/fail"); //认证失败时的跳转地址，这里只服务器端认证通过，客户端认证失败的情况，比如用户在客户端不存在，该地址不能被拦截
        casAuthenticationFilter.setAuthenticationFailureHandler(simpleUrlAuthenticationFailureHandler);

        return casAuthenticationFilter;
    }



    /**
     * cas 认证 Provider
     */
    @Bean
    public CasAuthenticationProvider casAuthenticationProvider() {
        //创建CAS授权认证器
        CasAuthenticationProvider casAuthenticationProvider = new CasAuthenticationProvider();
        //集成自定义授权认证器
        casAuthenticationProvider.setUserDetailsService(userDetailsService());
        //设置Cas授权认证器相关配置
        casAuthenticationProvider.setServiceProperties(serviceProperties());
        //设置票据校验器
        casAuthenticationProvider.setTicketValidator(cas30ServiceTicketValidator());
        casAuthenticationProvider.setKey("casAuthenticationProviderKey");

        return casAuthenticationProvider;
    }

    /**
     * 设置票据校验地址-CAS地址
     *
     * https://github.com/apereo/java-cas-client
     * Cas10ServiceTicketValidator
     * Cas20ServiceTicketValidator
     * Cas30ServiceTicketValidator
     *
     *
     * @return
     */
    @Bean
    public Cas30ServiceTicketValidator cas30ServiceTicketValidator() {
        return new Cas30ServiceTicketValidator(casProperties.getCasServerUrl());
    }

    /**
     * 单点登出过滤器
     */
    @Bean
    public SingleSignOutFilter singleSignOutFilter() {
        SingleSignOutFilter singleSignOutFilter = new SingleSignOutFilter();
        singleSignOutFilter.setIgnoreInitConfiguration(true);
        return singleSignOutFilter;
    }

    /**
     * 请求单点退出过滤器
     */
    @Bean
    public LogoutFilter casLogoutFilter() {
        LogoutFilter logoutFilter = new LogoutFilter(casProperties.getCasServerLogoutUrl(), new SecurityContextLogoutHandler());
        logoutFilter.setFilterProcessesUrl(casProperties.getAppLogoutUrl());
        return logoutFilter;
    }

    /**
     * 访问决策管理器：配置各种投决策，用以决定是否通过验证
     * <p>
     * 可以增加自定义决策，当前配置为默认配置
     * <p>
     * 类名	                描述
     * AffirmativeBased	如果有任何一个投票器允许访问，请求将被立刻允许，而不管之前可能有的拒绝决定。
     * ConsensusBased	多数票（允许或拒绝）决定了AccessDecisionManager的结果。平局的投票和空票（全是弃权的）的结果是可配置的。
     * UnanimousBased	所有的投票器必须全是允许的，否则访问将被拒绝。
     * <p>
     * RoleVoter:
     * Spring Security内置的一个AccessDecisionVoter，其会将ConfigAttribute简单的看作是一个角色名称，在投票的时如果拥有该角色即投赞成票。
     * 如果ConfigAttribute是以“ROLE_”开头的，则将使用RoleVoter进行投票。当用户拥有的权限中有一个或多个能匹配受保护对象配置的以“ROLE_”开头的ConfigAttribute时其将投赞成票；
     * 如果用户拥有的权限中没有一个能匹配受保护对象配置的以“ROLE_”开头的ConfigAttribute，则RoleVoter将投反对票；如果受保护对象配置的ConfigAttribute中没有以“ROLE_”开头的，则RoleVoter将弃权。
     * 支持的方法：.access("ROLE_ADMIN")
     * <p>
     * AuthenticatedVoter:
     * Spring Security内置的一个AccessDecisionVoter实现。其主要用来区分匿名用户、通过Remember-Me认证的用户和完全认证的用户。完全认证的用户是指由系统提供的登录入口进行成功登录认证的用户。
     * 支持的方法：.rememberMe() .fullyAuthenticated() .anonymous()
     */
    @Bean(name = "accessDecisionManager")
    public AccessDecisionManager accessDecisionManager() {
        //决策者列表
        List<AccessDecisionVoter<? extends Object>> decisionVoters = new ArrayList();
        decisionVoters.add(new RoleVoter()); // 基于角色名称的验证，必须以ROLE_开头 .access("ROLE_ADMIN")
        decisionVoters.add(new AuthenticatedVoter()); // .rememberMe()  .fullyAuthenticated() .anonymous()
        decisionVoters.add(dynamicRoleVoter());  // 自定投票器
        decisionVoters.add(new WebExpressionVoter()); // 基于表达式的验证，如：.access("hasRole('admin') or hasRole('user')") .permitAll() .hasRole("admin") .authenticated() 等等

        //AffirmativeBased ：任意决策者通过则通过
        AffirmativeBased accessDecisionManager = new AffirmativeBased(decisionVoters);
        return accessDecisionManager;
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
