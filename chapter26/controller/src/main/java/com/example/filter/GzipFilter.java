package com.example.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * <p>解压缩，如果请求内容是流或者json一般应该先进行gzip压缩再进行传输，所以这里判断是否对请求内容进行解压缩</p>
 * Created by hanqf on 2020/4/21 21:18.
 */
@WebFilter(urlPatterns = {"/demo/*"})
public class GzipFilter implements Filter {

    public static final Logger LOGGER = LoggerFactory.getLogger(GzipFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        chain.doFilter(new GzipRequestWrapper((HttpServletRequest) request), response);
    }

    @Override
    public void destroy() {

    }

    /**
     * <p>解压缩封装类</p>
     * Created by hanqf on 2020/4/21 21:18.
     */
    private class GzipRequestWrapper extends HttpServletRequestWrapper {

        private HttpServletRequest request;

        public GzipRequestWrapper(HttpServletRequest request) {
            super(request);
            this.request = request;
        }

        @Override
        public ServletInputStream getInputStream() throws IOException {
            ServletInputStream stream = request.getInputStream();
            String contentEncoding = request.getHeader("Content-Encoding");
            // 如果对内容进行了压缩，则解压
            if (null != contentEncoding && contentEncoding.indexOf("gzip") != -1) {
                try {
                    final GZIPInputStream gzipInputStream = new GZIPInputStream(stream);

                    ServletInputStream newStream = new ServletInputStream() {

                        @Override
                        public boolean isFinished() {
                            return false;
                        }

                        @Override
                        public boolean isReady() {
                            return false;
                        }

                        @Override
                        public void setReadListener(ReadListener readListener) {

                        }

                        @Override
                        public int read() throws IOException {
                            return gzipInputStream.read();
                        }
                    };
                    return newStream;
                } catch (Exception e) {
                    LOGGER.debug("ungzip content fail.", e);
                }
            }
            return stream;
        }
    }


}
