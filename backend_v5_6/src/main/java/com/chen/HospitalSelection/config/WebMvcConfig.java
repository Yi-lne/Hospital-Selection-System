package com.chen.HospitalSelection.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC配置类
 *
 * 功能说明：
 * 1. 配置CORS跨域（补充配置，Spring Security中已配置）
 * 2. 配置静态资源映射
 * 3. 配置拦截器（可选）
 *
 * @author chen
 * @version 1.0.0
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-path:D:/upload/hospital-selection}")
    private String uploadPath;

    /**
     * 配置CORS跨域
     *
     * 注意：由于项目使用了Spring Security，主要的CORS配置在SpringSecurityConfig中
     * 这里是补充配置，确保在没有经过Security过滤器链的情况下也能正常跨域
     *
     * 生产环境建议：
     * - 将allowedOrigins配置为具体的域名，而不是"*"
     * - 例如：allowedOrigins.add("https://www.yourdomain.com")
     *
     * @param registry CORS注册器
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // 允许的源（生产环境应该配置具体的域名）
                .allowedOriginPatterns("*")

                // 允许的请求方法
                .allowedMethods(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "OPTIONS",
                        "PATCH"
                )

                // 允许的请求头
                .allowedHeaders("*")

                // 允许携带凭证（Cookie等）
                .allowCredentials(true)

                // 预检请求的有效期（秒）
                // 在此时间内，浏览器不会再次发送预检请求
                .maxAge(3600);
    }

    /**
     * 配置静态资源映射
     *
     * 说明：
     * 1. 映射本地文件系统路径到URL访问路径
     * 2. 用于访问用户上传的文件（头像、图片等）
     * 3. 可以配置多个资源映射
     *
     * 使用示例：
     * - 文件保存路径：D:/hospital-upload/avatar/user1.jpg
     * - URL访问路径：http://localhost:8080/static/avatar/user1.jpg
     *
     * @param registry 资源处理器注册器
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // ========== 文件上传目录映射 ==========
        // 统一映射 /uploads/** 到上传目录
        // 这样上传返回的URL如 /uploads/avatar/2025/02/22/xxx.jpg 就能正确访问
        String uploadLocation = "file:" + uploadPath + "/";

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadLocation);

        // ========== 兼容旧的静态资源映射 ==========
        // 用户头像上传目录（兼容旧路径）
        registry.addResourceHandler("/static/avatar/**")
                .addResourceLocations(uploadLocation + "avatar/");

        // 医院图片上传目录
        registry.addResourceHandler("/static/hospital/**")
                .addResourceLocations(uploadLocation + "hospital/");

        // 医生图片上传目录
        registry.addResourceHandler("/static/doctor/**")
                .addResourceLocations(uploadLocation + "doctor/");

        // 社区图片上传目录
        registry.addResourceHandler("/static/community/**")
                .addResourceLocations(uploadLocation + "community/");

        // 临时文件目录
        registry.addResourceHandler("/static/temp/**")
                .addResourceLocations(uploadLocation + "temp/");

        // ========== Swagger UI资源映射 ==========
        // 如果Swagger访问有问题，可以取消下面的注释
        /*
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
        registry.addResourceHandler("/swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        */

        // ========== ClassPath资源映射 ==========
        // 用于访问resources/static目录下的静态资源
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");

        // ========== 图片缓存配置 ==========
        // 设置缓存时间（秒），提高访问性能
        // 生产环境建议设置较长的缓存时间（如86400秒=1天）
        registry.addResourceHandler("/static/**")
                .setCachePeriod(3600); // 1小时
    }

    /**
     * 配置拦截器（可选）
     *
     * 使用场景：
     * 1. 日志记录：记录请求信息
     * 2. 权限校验：自定义权限校验逻辑
     * 3. 参数验证：验证请求参数
     * 4. 性能监控：记录接口响应时间
     *
     * 示例：添加日志拦截器
     *
     * @Override
     * public void addInterceptors(InterceptorRegistry registry) {
     *     registry.addInterceptor(new LoggingInterceptor())
     *             .addPathPatterns("/**")  // 拦截所有路径
     *             .excludePathPatterns(    // 排除不需要拦截的路径
     *                     "/swagger-ui/**",
     *                     "/api/user/login",
     *                     "/api/user/register",
     *                     "/static/**"
     *             );
     * }
     */

    /**
     * 配置建议：
     *
     * 1. 文件上传路径配置：
     *    - 开发环境：使用本地路径（如D:/hospital-upload/）
     *    - 生产环境：建议使用云存储（如OSS、COS）
     *    - 路径可通过配置文件管理，便于不同环境切换
     *
     * 2. CORS配置：
     *    - Spring Security + WebMvc双重配置确保跨域正常
     *    - 生产环境务必限制allowedOrigins为具体域名
     *    - 开发环境可以使用allowedOriginPatterns("*")
     *
     * 3. 静态资源优化：
     *    - 合理设置缓存时间，减少服务器压力
     *    - 使用CDN加速静态资源访问
     *    - 图片资源考虑使用缩略图机制
     *
     * 4. 安全性考虑：
     *    - 文件上传要限制类型和大小
     *    - 防止路径遍历攻击
     *    - 敏感文件不要放在静态资源目录
     */
}
