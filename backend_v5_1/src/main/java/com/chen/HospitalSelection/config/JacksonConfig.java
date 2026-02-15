package com.chen.HospitalSelection.config;

import org.springframework.context.annotation.Configuration;

/**
 * Jackson 配置类
 *
 * 说明：Jackson的具体配置已在 application.yml 中完成：
 * - 支持Java 8时间类型（通过jackson-datatype-jsr310依赖）
 * - 日期序列化格式：yyyy-MM-dd HH:mm:ss
 * - 禁用时间戳格式：write-dates-as-timestamps: false
 * - 忽略null值：default-property-inclusion: non_null
 *
 * Spring Boot会自动加载这些配置，无需手动定义ObjectMapper bean
 *
 * @author chen
 */
@Configuration
public class JacksonConfig {
    // 配置已移至 application.yml，由Spring Boot自动配置
}
