package com.example.oauth2resourceserverdemo.security;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;

import java.util.Collection;

/**
 * <p>自定义投票器</p>
 * Created by hanqf on 2020/9/10 12:06.
 */


public class CustomDynamicRoleVoter implements
        AccessDecisionVoter<Object> {
    @Override
    public boolean supports(ConfigAttribute configAttribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> collection) {
        int result = ACCESS_ABSTAIN;

        // 获取登录的用户名称
        String userId = authentication.getName();
        // 这里仅作演示，除了anonymousUser都放行
        //if (userId != null && !"anonymousUser".equals(userId)) {
        //
        //    result = ACCESS_GRANTED;
        //
        //}
        return result;
    }
}
