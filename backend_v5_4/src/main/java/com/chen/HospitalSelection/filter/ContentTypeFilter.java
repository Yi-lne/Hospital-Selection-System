package com.chen.HospitalSelection.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Content-Type 过滤器
 * 移除 Content-Type 中的 charset 参数，解决 Spring Boot 2.7 的兼容性问题
 *
 * @author chen
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class ContentTypeFilter implements Filter {

    private static final Logger logger = LoggerFactory.getLogger(ContentTypeFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("=== ContentTypeFilter 已初始化 ===");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String originalContentType = httpRequest.getContentType();
        String uri = httpRequest.getRequestURI();

        logger.debug("请求路径: {}, 原始 Content-Type: {}", uri, originalContentType);

        // 包装请求，移除 Content-Type 中的 charset
        ContentTypeRequestWrapper wrappedRequest = new ContentTypeRequestWrapper(httpRequest);
        String modifiedContentType = wrappedRequest.getContentType();

        logger.debug("修改后 Content-Type: {}", modifiedContentType);

        chain.doFilter(wrappedRequest, response);
    }

    @Override
    public void destroy() {
        logger.info("=== ContentTypeFilter 已销毁 ===");
    }
}
