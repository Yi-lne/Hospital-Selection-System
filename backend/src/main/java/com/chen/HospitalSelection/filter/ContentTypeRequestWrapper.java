package com.chen.HospitalSelection.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * Content-Type 请求包装器
 * 重写 getContentType 和 getHeader 方法，移除 charset 参数
 *
 * @author chen
 */
public class ContentTypeRequestWrapper extends HttpServletRequestWrapper {

    public ContentTypeRequestWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String getContentType() {
        String contentType = super.getContentType();
        if (contentType != null && contentType.contains("charset")) {
            // 移除 charset 部分
            return contentType.substring(0, contentType.indexOf(";"));
        }
        return contentType;
    }

    @Override
    public String getHeader(String name) {
        if ("Content-Type".equalsIgnoreCase(name)) {
            String contentType = super.getHeader(name);
            if (contentType != null && contentType.contains("charset")) {
                // 移除 charset 部分
                return contentType.substring(0, contentType.indexOf(";"));
            }
            return contentType;
        }
        return super.getHeader(name);
    }
}
