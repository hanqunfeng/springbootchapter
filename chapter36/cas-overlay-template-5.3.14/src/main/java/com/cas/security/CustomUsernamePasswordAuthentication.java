package com.cas.security;

import com.cas.model.User;
import com.cas.utils.Md5Util;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.MessageDescriptor;
import org.apereo.cas.authentication.PreventedException;
import org.apereo.cas.authentication.UsernamePasswordCredential;
import org.apereo.cas.authentication.exceptions.AccountDisabledException;
import org.apereo.cas.authentication.exceptions.AccountPasswordMustChangeException;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.security.auth.login.AccountException;
import javax.security.auth.login.FailedLoginException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>自定义认证策略,测试，未使用</p>
 * Created by hanqf on 2020/9/22 15:20.
 */


public class CustomUsernamePasswordAuthentication extends AbstractUsernamePasswordAuthenticationHandler {
    private Logger logger = LoggerFactory.getLogger(CustomUsernamePasswordAuthentication.class);
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public CustomUsernamePasswordAuthentication(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(UsernamePasswordCredential credential, String originalPassword) throws GeneralSecurityException, PreventedException {
        String username = credential.getUsername();

        String password = credential.getPassword();

        logger.info(String.format("username = %s,password=%s", username, password));

        String sql = "SELECT username,password,email,expired,disabled FROM cas_user WHERE username = ?";

        User info = (User) jdbcTemplate.queryForObject(sql, new Object[]{username}, new BeanPropertyRowMapper(User.class));

        logger.info("database User : " + info);

        if (info != null) {
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

            //可自定义返回给客户端的多个属性信息
            Map<String, Object> returnInfo = new HashMap<>();
            returnInfo.put("username", info.getUsername());
            returnInfo.put("email", info.getEmail());
            returnInfo.put("expired", info.getExpired());

            final List<MessageDescriptor> list = new ArrayList<>();

            return createHandlerResult(credential,
                    this.principalFactory.createPrincipal(username, returnInfo), list);
        }
    }
}
