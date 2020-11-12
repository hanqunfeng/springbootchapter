package com.example.oauth2authserverdemo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import java.util.ArrayList;
import java.util.List;

/**
 * <h1>AuthServerConfig</h1>
 * Created by hanqf on 2020/11/5 15:42.
 */
public abstract class AuthServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    protected UserDetailsService userDetailsService;

    @Autowired
    protected AuthenticationManager authenticationManager;

    @Autowired
    protected TokenStore jwtTokenStore;

    @Autowired
    protected JwtAccessTokenConverter jwtAccessTokenConverter;

    @Autowired
    protected TokenEnhancer jwtTokenEnhancer;

    @Autowired
    protected PasswordEncoder passwordEncoder;


    /**
     * 1.增加jwt 增强模式
     * 2.调用userDetailsService实现UserDetailsService接口,对客户端信息进行认证与授权
     *
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        //   jwt 增强模式
        //   对令牌的增强操作就在enhance方法中
        //   面在配置类中,将TokenEnhancer和JwtAccessConverter加到一个enhancerChain中
        //
        //   通俗点讲它做了两件事：
        //   给JWT令牌中设置附加信息和jti：jwt的唯一身份标识,主要用来作为一次性token,从而回避重放攻击
        //   判断请求中是否有refreshToken,如果有,就重新设置refreshToken并加入附加信息
        TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> enhancerList = new ArrayList<TokenEnhancer>();
        enhancerList.add(jwtTokenEnhancer);
        enhancerList.add(jwtAccessTokenConverter);
        enhancerChain.setTokenEnhancers(enhancerList); //将自定义Enhancer加入EnhancerChain的delegates数组中

        endpoints.tokenStore(jwtTokenStore)
                //支持 refresh_token 模式
                .userDetailsService(userDetailsService)
                //支持 password 模式
                .authenticationManager(authenticationManager)
                .tokenEnhancer(enhancerChain)
                .accessTokenConverter(jwtAccessTokenConverter);
    }


    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        security
                //获取公钥的请求全部允许
                .tokenKeyAccess("permitAll()")
                //验证token有效性的请求需要先登录认证
                .checkTokenAccess("isAuthenticated()")
                //允许客户端form表单认证，就是可以将client_id和client_secret放到form中提交，否则必须使用Basic认证
                .allowFormAuthenticationForClients();
    }


}


