package com.chen.HospitalSelection.config;

import com.chen.HospitalSelection.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;

/**
 * Spring Security配置类
 *
 * 功能说明：
 * 1. 配置JWT认证过滤器
 * 2. 配置CORS跨域
 * 3. 配置URL权限控制（公开接口、需要登录的接口）
 * 4. 配置密码编码器
 *
 * @author chen
 * @version 1.0.0
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SpringSecurityConfig {

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * JWT认证过滤器Bean
     * 在此定义以便在Spring Security配置中使用
     */
    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtUtil);
    }

    /**
     * 配置密码编码器
     * 使用BCrypt算法，安全性高
     *
     * @return BCryptPasswordEncoder实例
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置CORS跨域
     * 允许前端跨域访问后端API
     *
     * @return CORS配置源
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 允许的源（生产环境应该配置具体的域名）
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));

        // 允许的请求头
        configuration.setAllowedHeaders(Arrays.asList(
                "*",
                "Authorization",
                "Content-Type",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // 允许的请求方法
        configuration.setAllowedMethods(Arrays.asList(
                "GET",
                "POST",
                "PUT",
                "DELETE",
                "OPTIONS",
                "PATCH"
        ));

        // 允许携带凭证（Cookie等）
        configuration.setAllowCredentials(true);

        // 预检请求的有效期（秒）
        configuration.setMaxAge(3600L);

        // 暴露的响应头
        configuration.setExposedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /**
     * 配置安全过滤器链
     * 核心配置：定义哪些URL需要认证，哪些不需要
     *
     * @param http HttpSecurity对象
     * @return SecurityFilterChain实例
     * @throws Exception 配置异常
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 禁用CSRF（前后端分离项目通常不需要CSRF保护）
                .csrf().disable()

                // 配置CORS
                .cors().configurationSource(corsConfigurationSource()).and()

                // 配置会话管理：无状态（使用JWT，不需要Session）
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                // 配置授权规则
                .authorizeRequests()

                // ========== 公开接口（不需要登录） ==========
                // Swagger API文档相关
                .antMatchers(
                        "/swagger-ui/**",
                        "/swagger-ui.html",
                        "/v3/api-docs/**",
                        "/swagger-resources/**",
                        "/webjars/**"
                ).permitAll()

                // 用户认证相关（注册、登录）
                .antMatchers(
                        "/user/register",
                        "/user/login",
                        "/user/password/reset"
                ).permitAll()

                // 医院公开查询接口
                .antMatchers(
                        "/hospital/**",
                        "/doctor/**",
                        "/department/**",
                        "/disease/**",
                        "/area/**"
                ).permitAll()

                // 社区公开浏览接口（查看话题、评论，但发帖需要登录）
                .antMatchers(
                        "/community/topics",
                        "/community/topic/**",
                        "/community/boards"
                ).permitAll()

                // 健康检查接口
                .antMatchers("/actuator/**").permitAll()

                // 静态资源
                .antMatchers(
                        "/static/**",
                        "/images/**",
                        "/css/**",
                        "/js/**"
                ).permitAll()

                // ========== 需要登录的接口 ==========
                // 除上述公开接口外，所有接口都需要认证
                .anyRequest().authenticated()
                .and()

                // 添加JWT认证过滤器（在UsernamePasswordAuthenticationFilter之前）
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 禁用页面缓存（保证安全性）
        http.headers().cacheControl();

        return http.build();
    }

    /**
     * JWT认证过滤器
     * 内部类：拦截请求，从请求头中获取JWT token并验证
     */
    public static class JwtAuthenticationFilter extends OncePerRequestFilter {

        private final JwtUtil jwtUtil;

        public JwtAuthenticationFilter(JwtUtil jwtUtil) {
            this.jwtUtil = jwtUtil;
        }

        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                       HttpServletResponse response,
                                       FilterChain filterChain)
                throws ServletException, IOException {

            // 从请求头中获取Authorization
            String authHeader = request.getHeader("Authorization");

            // 验证token格式：Bearer <token>
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7); // 去掉"Bearer "前缀

                try {
                    // 验证token是否有效
                    if (jwtUtil.validateToken(token)) {
                        // 从token中获取用户信息
                        Long userId = jwtUtil.getUserIdFromToken(token);
                        String phone = jwtUtil.getPhoneFromToken(token);

                        // 这里可以将用户信息设置到SecurityContext中
                        // 方便后续通过SecurityContextHolder获取
                        // UsernamePasswordAuthenticationToken authentication =
                        //         new UsernamePasswordAuthenticationToken(userId, null, null);
                        // SecurityContextHolder.getContext().setAuthentication(authentication);

                        logger.debug("JWT token validated successfully for user: " + userId);
                    }
                } catch (Exception e) {
                    logger.error("JWT token validation failed: " + e.getMessage());
                }
            }

            // 继续过滤器链
            filterChain.doFilter(request, response);
        }
    }
}
