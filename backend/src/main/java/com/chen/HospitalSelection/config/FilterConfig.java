package com.chen.HospitalSelection.config;

import com.chen.HospitalSelection.filter.ContentTypeFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Filter 配置类
 * 显式注册 ContentTypeFilter
 *
 * @author chen
 */
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<ContentTypeFilter> contentTypeFilterRegistration() {
        FilterRegistrationBean<ContentTypeFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new ContentTypeFilter());
        registration.addUrlPatterns("/*");
        registration.setOrder(1); // 最高优先级
        registration.setName("ContentTypeFilter");

        System.out.println("=== ContentTypeFilter 已注册 ===");

        return registration;
    }
}
