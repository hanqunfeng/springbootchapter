package com.example.springsecuritydemo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

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
 */

@Configuration
// 启用web安全认证，springboot mvc环境可以不用配置，自动配置WebSecurityEnablerConfiguration中已经启用，参考：http://www.zhihesj.com/?id=26
@EnableWebSecurity
//开启方法级别的安全验证注解，参考 https://www.jianshu.com/p/77b4835b6e8e
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Bean
    // 密码加密策略
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    // 配置内存用户策略
    // 登录: /login  注销: /logout
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        // 测试时，可以直接用下面的方式
        // User.UserBuilder builder = User.withDefaultPasswordEncoder();
        User.UserBuilder builder = User.builder().passwordEncoder(passwordEncoder()::encode);
        auth.inMemoryAuthentication().withUser(builder.username("admin").password("123456").roles("admin").build());
        auth.inMemoryAuthentication().withUser(builder.username("guest").password("123456").roles("guest").build());
    }
}
