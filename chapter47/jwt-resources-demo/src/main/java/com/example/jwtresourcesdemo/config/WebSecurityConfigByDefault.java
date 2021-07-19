package com.example.jwtresourcesdemo.config;


import com.example.jwtresourcesdemo.security.CustomAccessDeniedHandler;
import com.example.jwtresourcesdemo.security.CustomDynamicRoleVoter;
import com.example.jwtresourcesdemo.security.CustomSecurityProperties;
import com.example.jwtresourcesdemo.security.JwtAuthenticationTokenFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.vote.AffirmativeBased;
import org.springframework.security.access.vote.AuthenticatedVoter;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.access.expression.WebExpressionVoter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.ArrayList;
import java.util.List;

/**
 * <p></p>
 * Created by hanqf on 2020/8/10 10:42.
 * <p>
 * 参考：https://www.jianshu.com/p/77b4835b6e8e
 * 要开启Spring方法级安全，在添加了@Configuration注解的类上再添加@EnableGlobalMethodSecurity注解即可
 * 1、@EnableGlobalMethodSecurity(securedEnabled=true) 确定安全注解 [@Secured] 是否启用
 * 其有1个注解:
 *
 * @Secured: 注解是用来定义业务方法的安全配置, 不支持Spring EL表达式。不够灵活。并且指定的角色必须以ROLE_开头，不可省略。
 * 示例：
 * # 具有admin或者user权限即可访问，不支持and
 * @Secured({"ROLE_admin", "ROLE_user"})
 * <p>
 * 2、@EnableGlobalMethodSecurity(jsr250Enabled=true)  确定 JSR-250注解 [@RolesAllowed..]是否启用
 * 其有3个注解:
 * @DenyAll： 拒绝所有访问
 * @RolesAllowed({"USER", "ADMIN"})： 该方法只要具有"USER", "ADMIN"任意一种权限就可以访问。这里可以省略前缀ROLE_，实际的权限可能是ROLE_ADMIN
 * @PermitAll： 允许所有访问
 * <p>
 * 3、@EnableGlobalMethodSecurity(prePostEnabled=true) 确定 前置注解[@PreAuthorize,@PostAuthorize,..] 是否启用
 * 其有4个注解：
 * <p>
 * 3.1 @PreAuthorize 进入方法之前验证授权。可以将登录用户的roles参数传到方法中验证。
 * 示例：
 * @PreAuthorize("hasRole('ADMIN') AND hasRole('DBA')")
 * <p>
 * // 只能user角色可以访问
 * @PreAuthorize (" hasAnyRole ( ' user ')")
 * // user 角色或者 admin 角色都可访问
 * @PreAuthorize (" hasAnyRole ( ' user ') or hasAnyRole('admin')")
 * // 同时拥有 user 和 admin 角色才能访问
 * @PreAuthorize (" hasAnyRole ( ' user ') and hasAnyRole('admin')")
 * // 限制只能查询 id 小于 10 的用户
 * @PreAuthorize("#id < 10")
 * User findById(int id);
 * <p>
 * // 只能查询自己的信息
 * @PreAuthorize("principal.username.equals(#username)") User find(String username);
 * <p>
 * // 限制只能新增用户名称为abc的用户
 * @PreAuthorize("#user.name.equals('abc')") void add(User user)
 * <p>
 * 3.2 @PostAuthorize 该注解使用不多，在方法执行后再进行权限验证。 适合验证带有返回值的权限。Spring EL 提供 返回对象能够在表达式语言中获取返回的对象returnObject。
 * 示例：
 * // 查询到用户信息后，再验证用户名是否和登录用户名一致
 * @PostAuthorize("returnObject.name == authentication.name")
 * @GetMapping("/get-user") public User getUser(String name){
 * return userService.getUser(name);
 * }
 * // 验证返回的数是否是偶数
 * @PostAuthorize("returnObject % 2 == 0")
 * public Integer test(){
 * // ...
 * return id;
 * }
 * <p>
 * 3.3 @PostFilter 对集合类型的返回值进行过滤，移除结果为false的元素
 * 示例：
 * // 指定过滤的参数，过滤偶数
 * @PreFilter(filterTarget="ids", value="filterObject%2==0")
 * public void delete(List<Integer> ids, List<String> username)
 * <p>
 * 3.4 @PreFilter 对集合类型的参数执行过滤，移除结果为false的元素
 * 示例：
 * @PostFilter("filterObject.id%2==0") public List<User> findAll(){
 * ...
 * return userList;
 * }
 * <p>
 * <p>
 * WebSecurityConfigurerAdapter:
 * 主要规则通过实现WebSecurityConfigurerAdapter的三个configure方法来设置
 * 1.configure(AuthenticationManagerBuilder auth) ：用户管理，用于设置用户的验证方式
 * 2.configure(WebSecurity web) ：指定不拦截的路径规则
 * 3.configure(HttpSecurity http) ：过滤器管理，用于设置资源权限、登录注销方式、session管理方式，自定义过滤器绑定，等等
 * <p>
 * 另外，userDetailsService()方法也需要重写，这里是自定义用户的地方
 */

@Configuration
//开启方法级别的安全验证注解，参考 https://www.jianshu.com/p/77b4835b6e8e
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfigByDefault extends WebSecurityConfigurerAdapter {



    @Autowired
    private JwtAuthenticationTokenFilter jwtAuthenticationTokenFilter;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    private CustomSecurityProperties customSecurityProperties;



    @Bean
    // 密码加密策略
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 用户管理，用于设置用户的验证方式
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 绑定UserDetailsService
        auth.userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder()); // 绑定密码规则

    }


    /**
     * 用户策略设置，这里使用内存用户策略，自定义策略需要实现UserDetailsService接口
     * 因为是jwt服务器认证，所以这里密码没用
     * 正式使用时可以查询数据库得到用户信息
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
     * isFullyAuthenticated()	        当前用户既不是anonymous也不是rememberMe用户时返回true
     * hasIpAddress('192.168.1.0/24'))	请求发送的IP匹配时返回true
     * isAnonymous()                    当前用户是否是一个匿名用户
     * isRememberMe()                   表示当前用户是否是通过Remember-Me自动登录的
     * isAuthenticated()                表示当前用户是否已经登录认证成功了。
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 拦截路径规则设置
        http.authorizeRequests()
                .accessDecisionManager(accessDecisionManager()) //访问决策管理器，默认支持三种决策者：RoleVoter、AuthenticatedVoter、WebExpressionVoter。一般需要添加自定义决策者
                .antMatchers(customSecurityProperties.getPermitAll()).permitAll()
                .antMatchers("/demo/**").access("hasRole('admin') or hasRole('user')") // 基于表达式 参考：https://my.oschina.net/liuyuantao/blog/1924776
                .anyRequest().authenticated(); // 除以上规则外，其它路径只要登录就可以访问


        // 开启csrf，默认为开启，生成页面时会自动在每个form中增加一个隐藏属性<input type="hidden" name="_csrf" value="95e8706b-8d22-4d62-9a27-3da5993e0a7d">，
        // 也可以手工配置(没必要)，thymeleaf中就是<input type="hidden" th:name="${_csrf.parameterName}" th:value="${_csrf.token}" />，js中如果需要使用时也可以使用该属性
        //http.csrf().ignoringAntMatchers("/**/*.json","/**/*.xml"); //哪些不需要csrf，非浏览器直接访问的地址需要进行屏蔽，因为csrf标签只有页面会自动生成

        // 关闭csrf，一般情况下，如果服务中大部分请求都是基于浏览器访问的，则应该开启csrf，如果大部分都是接口，则可以关闭csrf，因为只有在页面的form中才会自动生成隐藏属性，所以接口需要自己在参数中传递，反倒麻烦
        http.csrf().disable();

        // 注销设置，这里需要注意的是，如果启用了csrf(默认就是开启)，则logout只能是post提交，如果要get提交，则必须如下配置
        //http.logout() // 注销: 默认 /logout
        //        .logoutRequestMatcher(new AntPathRequestMatcher("/logout")) //get
        //        //.logoutUrl("/logout")  //post
        //        .logoutSuccessUrl("/login")
        //        .deleteCookies("JSESSIONID") // 注销时删除无用的cookie
        //        .invalidateHttpSession(true); //注销后使session无效

        //http.rememberMe()
        //        // 服务端token存储位置默认为内存中(InMemoryTokenRepositoryImpl)，客户端会携带cookie中的token与服务端比对，服务重启则失效
        //        // 可以使用JdbcTokenRepositoryImpl将信息存储到数据库，需要配置datasource，其支持自动创建数据表，待结合数据库时再具体说明
        //        .tokenRepository(new InMemoryTokenRepositoryImpl()) // 默认内存存储服务端token
        //        .tokenValiditySeconds(60 * 60 * 24 * 14); // 单位秒 两周=60*60*24*14  默认14天


        // session管理
        http.sessionManagement()
                //无状态策略
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //配置jwt认证过滤器
        http.addFilterBefore(jwtAuthenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
        //授权异常处理器，用户登录成功但是没有权限访问对应的资源时会调用
        http.exceptionHandling().accessDeniedHandler(customAccessDeniedHandler);
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
        decisionVoters.add(new CustomDynamicRoleVoter());  // 自定投票器
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


}
