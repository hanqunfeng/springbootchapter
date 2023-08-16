/*
 * COPYRIGHT Beijing cp-boss-Tech Co.,Ltd.                                   *
 ****************************************************************************
 * 源文件名:  com.cp-boss.common.filter.CP_ImageFilter.java
 * 功能: cpframework框架
 * 版本:	@version 1.0
 * 编制日期: 2014年3月27日 上午11:00:33
 * 修改历史: (主要历史变动原因及说明)
 * YYYY-MM-DD |    Author      |	 Change Description
 * 2014年3月27日    |    hanqunfeng     |     Created
 */
package com.example.support.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

/**
 * Description: <类功能描述>. <br>
 * <p>
 * <使用说明>
 * </p>
 * Makedate:2014年3月27日 上午11:00:33
 *
 * @author hanqunfeng
 * @version V1.0
 *
 *
 * OncePerRequestFilter，顾名思义，它能够确保在一次请求中只通过一次filter，而不会重复的执行。
 * 大家常识上都认为，一次请求本来就只filter一次，为什么还要由此特别限定呢，
 * 往往我们的常识和实际的实现并不真的一样，经过一番资料的查阅，此方法是为了兼容不同的web container，
 * 也就是说并不是所有的container都入我们期望的只过滤一次，servlet版本不同，执行过程也不同，
 * 因此，为了兼容各种不同运行环境和版本，默认filter继承OncePerRequestFilter是一个比较稳妥的选择。
 */
@Component
@WebFilter(filterName="imageFilter",urlPatterns="/forgotPasswordEmail.do")
public class CP_ImageFilter extends OncePerRequestFilter {
    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        // TODO Auto-generated method stub

        String uri = request.getRequestURI();
        String[] checkUri = {request.getContextPath()+"/j_spring_security_check",request.getContextPath()+"/forgotPasswordEmail.do"};

        if(Arrays.asList(checkUri).contains(uri)) {

            String yanzhengm = request.getParameter("j_code");
            String sessionyanz = (String) request.getSession(true).getAttribute("checkcode");
            if (yanzhengm.equalsIgnoreCase(sessionyanz)) {
                filterChain.doFilter(request, response);
            } else {
                response.sendRedirect(request.getContextPath() + "/login.do?login_error=2");
            }
        }else {
            filterChain.doFilter(request, response);
        }
    }

}


