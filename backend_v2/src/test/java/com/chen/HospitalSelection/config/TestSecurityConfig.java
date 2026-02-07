package com.chen.HospitalSelection.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 测试环境Security配置
 * 禁用所有Security限制，方便测试
 *
 * 说明：Jackson配置已在主配置文件 application.yml 中完成，
 * Spring Boot会自动加载，测试环境无需单独配置 ObjectMapper
 */
@TestConfiguration
@EnableWebSecurity
public class TestSecurityConfig {

    @Bean
    @Primary
    public SecurityFilterChain testFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .cors().disable()
            .authorizeRequests()
            .anyRequest().permitAll();

        return http.build();
    }
}
