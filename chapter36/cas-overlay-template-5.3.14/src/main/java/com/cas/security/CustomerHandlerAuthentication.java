package com.cas.security;

import com.cas.model.User;
import com.cas.utils.AesUtil;
import com.cas.utils.Md5Util;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.Credential;
import org.apereo.cas.authentication.MessageDescriptor;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.WebUtils;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.FailedLoginException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * <p></p>
 * Created by hanqf on 2020/9/22 16:48.
 */


public class CustomerHandlerAuthentication extends AbstractPreAndPostProcessingAuthenticationHandler {
    private Logger logger = LoggerFactory.getLogger(CustomerHandlerAuthentication.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;



    public CustomerHandlerAuthentication(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @Override
    public boolean supports(Credential credential) {
        //判断传递过来的Credential 是否是自己能处理的类型
        return credential instanceof CustomCredential;
    }

    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(Credential credential) throws GeneralSecurityException, PreventedException {

        CustomCredential customCredential = (CustomCredential) credential;

        String username = customCredential.getUsername();

        String password = customCredential.getPassword();

        String captcha = customCredential.getCaptcha();

        logger.info(String.format("username = %s,password = %s,captcha = %s", username, password, captcha));

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        //核对验证码是否有效,页面上已经通过ajax验证过一次了，这里就是为了演示登录表单数据
        String checkcode = (String) WebUtils.getSessionAttribute(request, "CHECK_CODE");
        if (checkcode == null) {
            Cookie cookie = WebUtils.getCookie(request, "CHECK_CODE");
            if (cookie != null) {
                checkcode = AesUtil.decrypt(cookie.getValue());
            }
        }

        logger.info(String.format("captcha = %s,checkcode = %s", captcha, checkcode));
        if (!captcha.equalsIgnoreCase(checkcode)) {
            throw new CheckCodeErrorException("Sorry,captcha is wrong");
        }

        String sql = "SELECT username,password,email,expired,disabled FROM cas_user WHERE username = ?";

        User info = (User) jdbcTemplate.queryForObject(sql, new Object[]{username}, new BeanPropertyRowMapper(User.class));

        logger.info("database User : " + info);

        if (info == null) {
            throw new AccountException("Sorry, username not found!");
        }


        if (!info.getPassword().equals(Md5Util.string2Md5(password))) {
            throw new FailedLoginException("Sorry, password not correct!");
        }
        if (info.getExpired() != null && info.getExpired() == 1) { //密码过期
            throw new AccountPasswordMustChangeException("Password has expired");
        }
        if (info.getDisabled() != null && info.getDisabled() == 1) { //帐号禁用
            throw new AccountDisabledException("Account has been disabled");
        } else {

            //可自定义返回给客户端的多个属性信息,springsecurity接收属性值时，
            // 如果使用的是GrantedAuthorityFromAssertionAttributesUserDetailsService，则属性值都是大写的
            // 客户端可以自定义UserDetailsService，参考客户端demo
            Map<String, Object> returnInfo = new HashMap<>();
            returnInfo.put("username", info.getUsername());
            returnInfo.put("email", info.getEmail());
            returnInfo.put("expired", info.getExpired());
            List<String> rolesList = Arrays.asList("ROLE_ADMIN","ROLE_TEST");
            returnInfo.put("roles", rolesList); //此处也可以返回List<String>，什么类型重点看客户端怎么接收

            final List<MessageDescriptor> list = new ArrayList<>();

            return createHandlerResult(credential,
                    this.principalFactory.createPrincipal(username, returnInfo), list);
        }
    }
}
