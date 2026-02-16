package com.chen.HospitalSelection;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * 医院选择系统 - 应用启动类
 *
 * @author 陈文滔
 * @version 1.0.0
 */
@SpringBootApplication
@EnableAsync
@MapperScan("com.chen.HospitalSelection.mapper")
public class HospitalSelectionApplication {

    public static void main(String[] args) {
        SpringApplication.run(HospitalSelectionApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("医院选择系统启动成功！");
        System.out.println("访问地址: http://localhost:8088/api");
        System.out.println("Swagger文档: http://localhost:8088/api/swagger-ui/");
        System.out.println("========================================\n");
    }
}
