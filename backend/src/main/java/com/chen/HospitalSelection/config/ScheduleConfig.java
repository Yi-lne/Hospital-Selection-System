package com.chen.HospitalSelection.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务配置
 * 启用Spring的定时任务功能
 *
 * @author chen
 * @since 2025-02-16
 */
@Configuration
@EnableScheduling
public class ScheduleConfig {
    // 此类仅用于启用@EnableScheduling注解
    // 具体的定时任务在task包中定义
}
