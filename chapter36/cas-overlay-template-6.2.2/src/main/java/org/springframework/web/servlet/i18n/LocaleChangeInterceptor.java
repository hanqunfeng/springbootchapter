//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.springframework.web.servlet.i18n;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Locale;

public class LocaleChangeInterceptor extends HandlerInterceptorAdapter {
    public static final String DEFAULT_PARAM_NAME = "locale";
    protected final Log logger = LogFactory.getLog(this.getClass());
    private String paramName = "locale";
    @Nullable
    private String[] httpMethods;
    private boolean ignoreInvalidLocale = false;

    public LocaleChangeInterceptor() {
    }

    public void setParamName(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return this.paramName;
    }

    public void setHttpMethods(@Nullable String... httpMethods) {
        this.httpMethods = httpMethods;
    }

    @Nullable
    public String[] getHttpMethods() {
        return this.httpMethods;
    }

    public void setIgnoreInvalidLocale(boolean ignoreInvalidLocale) {
        this.ignoreInvalidLocale = ignoreInvalidLocale;
    }

    public boolean isIgnoreInvalidLocale() {
        return this.ignoreInvalidLocale;
    }

    /** @deprecated */
    @Deprecated
    public void setLanguageTagCompliant(boolean languageTagCompliant) {
        if (!languageTagCompliant) {
            throw new IllegalArgumentException("LocaleChangeInterceptor always accepts BCP 47 language tags");
        }
    }

    /** @deprecated */
    @Deprecated
    public boolean isLanguageTagCompliant() {
        return true;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
        String newLocale = request.getParameter(this.getParamName());
        if (newLocale != null && this.checkHttpMethod(request.getMethod())) {

            //解决locale=en?locale=en?locale=en，语言参数无限连接的问题
            if(newLocale.contains("?")){
                int index = newLocale.indexOf("?");
                newLocale = newLocale.substring(0,index);
                logger.info("newLocale==" + newLocale);
            }

            LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
            if (localeResolver == null) {
                throw new IllegalStateException("No LocaleResolver found: not in a DispatcherServlet request?");
            }

            try {
                localeResolver.setLocale(request, response, this.parseLocaleValue(newLocale));
            } catch (IllegalArgumentException var7) {
                if (!this.isIgnoreInvalidLocale()) {
                    throw var7;
                }

                if (this.logger.isDebugEnabled()) {
                    this.logger.debug("Ignoring invalid locale value [" + newLocale + "]: " + var7.getMessage());
                }
            }
        }

        if(newLocale != null){
            response.sendRedirect(request.getContextPath());
        }

        return true;
    }

    private boolean checkHttpMethod(String currentMethod) {
        String[] configuredMethods = this.getHttpMethods();
        if (ObjectUtils.isEmpty(configuredMethods)) {
            return true;
        } else {
            String[] var3 = configuredMethods;
            int var4 = configuredMethods.length;

            for(int var5 = 0; var5 < var4; ++var5) {
                String configuredMethod = var3[var5];
                if (configuredMethod.equalsIgnoreCase(currentMethod)) {
                    return true;
                }
            }

            return false;
        }
    }

    @Nullable
    protected Locale parseLocaleValue(String localeValue) {
        return StringUtils.parseLocale(localeValue);
    }
}
