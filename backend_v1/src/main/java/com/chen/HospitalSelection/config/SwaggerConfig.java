package com.chen.HospitalSelection.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Swagger API文档配置类
 *
 * 功能说明：
 * 1. 配置Swagger Docket
 * 2. 配置API基本信息（标题、描述、版本等）
 * 3. 配置扫描包路径
 * 4. 配置JWT认证
 *
 * 访问地址：
 * - Swagger UI: http://localhost:8080/swagger-ui/
 * - API Docs: http://localhost:8080/v3/api-docs
 *
 * @author chen
 * @version 1.0.0
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    /**
     * 配置Swagger Docket
     * Docket是Swagger的核心配置类
     *
     * @return Docket实例
     */
    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.SWAGGER_2)

                // ========== API基本信息 ==========
                .apiInfo(apiInfo())

                // ========== 选择要生成文档的接口 ==========
                .select()

                // 配置扫描的包路径
                // 方式1：扫描指定包
                .apis(RequestHandlerSelectors.basePackage("com.chen.HospitalSelection.controller"))

                // 方式2：扫描有指定注解的类（可选）
                // .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))

                // 方式3：扫描有指定注解的方法（可选）
                // .apis(RequestHandlerSelectors.withMethodAnnotation(Operation.class))

                // 配置路径过滤
                // PathSelectors.any()：所有路径
                // PathSelectors.none()：不过滤
                // PathSelectors.ant("/api/**")：只匹配/api开头的路径
                .paths(PathSelectors.any())

                .build()

                // ========== 配置安全认证（JWT） ==========
                // 配置安全方案
                .securitySchemes(securitySchemes())

                // 配置安全上下文
                .securityContexts(securityContexts())

                // ========== 配置协议 ==========
                // 默认协议：http
                // 如果使用HTTPS，可以在这里配置
                // .protocols(Collections.singleton("https"));
                ;
    }

    /**
     * 配置API基本信息
     *
     * @return ApiInfo对象
     */
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()

                // API标题
                .title("智能医院选择系统 API文档")

                // API描述
                .description(
                        "## 项目介绍\n" +
                        "智能医院选择系统是一个基于SpringBoot的医院查询与选择平台，" +
                        "提供医院信息查询、医生信息查询、多条件筛选、病友社区等功能。\n\n" +

                        "## 核心功能\n" +
                        "- **医院查询**：支持按地区、等级、科室等多条件筛选\n" +
                        "- **医生查询**：提供医生详细信息、专业特长、坐诊时间等\n" +
                        "- **社区交流**：病友经验分享、问答互动\n" +
                        "- **个人中心**：收藏、病史、查询历史等\n\n" +

                        "## 认证说明\n" +
                        "- **公开接口**：注册、登录、医院/医生查询等\n" +
                        "- **需要登录**：发帖、评论、收藏、私信等\n" +
                        "- **认证方式**：JWT Token（Bearer Token）\n\n" +

                        "## 响应格式\n" +
                        "所有接口统一返回格式：\n" +
                        "```json\n" +
                        "{\n" +
                        "  \"code\": 200,\n" +
                        "  \"message\": \"success\",\n" +
                        "  \"data\": {...}\n" +
                        "}\n" +
                        "```\n\n" +

                        "## 联系方式\n" +
                        "- 作者：chen\n" +
                        "- 版本：v1.0.0\n" +
                        "- 日期：2025-01-30"
                )

                // API版本
                .version("1.0.0")

                // 服务条款URL（可选）
                // .termsOfServiceUrl("http://www.yourdomain.com/terms")

                // 许可证（可选）
                .license("Apache License 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0.html")

                // 联系人信息（可选）
                .contact(new Contact(
                        "chen",                       // 姓名
                        "http://www.yourdomain.com",  // URL
                        "your-email@example.com"      // 邮箱
                ))

                .build();
    }

    /**
     * 配置安全方案
     * 定义API认证的方式：使用JWT Token
     *
     * @return 安全方案列表
     */
    private List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> schemes = new ArrayList<>();

        // 配置JWT认证
        // apiKeyName：HTTP Header的名称，必须是"Authorization"
        // passAs：Header的方式传递token
        ApiKey apiKey = new ApiKey(
                "JWT",                    // 认证方案的名称
                "Authorization",          // HTTP Header的名称
                "header"                  // 参数传递位置：header/query/cookie
        );

        schemes.add(apiKey);
        return schemes;
    }

    /**
     * 配置安全上下文
     * 定义哪些接口需要认证，哪些不需要
     *
     * @return 安全上下文列表
     */
    private List<SecurityContext> securityContexts() {
        List<SecurityContext> contexts = new ArrayList<>();

        SecurityContext securityContext = SecurityContext.builder()

                // 配置需要认证的路径
                // 这里的配置表示：所有路径都可以使用JWT认证
                // 但实际上具体的认证要求在Spring Security中配置
                .securityReferences(defaultAuth())

                // 配置路径匹配规则
                // PathSelectors.any()：所有路径都可以使用认证
                // PathSelectors.ant("/api/community/**")：只有社区相关接口需要认证
                .operationSelector(operationContext -> {
                    // 这里可以自定义逻辑判断哪些接口需要认证
                    // 目前设置为所有路径都可选认证（根据接口实际需求）
                    return true;
                })

                .build();

        contexts.add(securityContext);
        return contexts;
    }

    /**
     * 配置默认的安全引用
     * 定义JWT认证的作用域（全局）
     *
     * @return 安全引用列表
     */
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope(
                "global",           // 作用域名称
                "accessEverything"  // 作用域描述
        );

        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;

        SecurityReference securityReference = new SecurityReference(
                "JWT",                  // 引用的安全方案名称（与securitySchemes中的名称一致）
                authorizationScopes     // 作用域数组
        );

        return Collections.singletonList(securityReference);
    }

    /**
     * Swagger使用说明：
     *
     * 1. 在Controller上添加注解：
     *    @Api(tags = "用户管理")  // 标记Controller的用途
     *
     * 2. 在接口方法上添加注解：
     *    @ApiOperation(value = "用户登录", notes = "使用手机号和密码登录")
     *
     * 3. 在实体类上添加注解：
     *    @ApiModel(description = "用户登录DTO")
     *
     * 4. 在实体类属性上添加注解：
     *    @ApiModelProperty(value = "手机号", required = true, example = "13800138000")
     *
     * 5. 测试需要认证的接口：
     *    - 点击页面右上角的"Authorize"按钮
     *    - 在弹出的对话框中输入：Bearer <your-jwt-token>
     *    - 注意：Bearer和token之间有空格
     *    - 例如：Bearer eyJhbGciOiJIUzI1NiJ9.eyJ1c2VySWQiOjEsInBob25lIjoiMTM4MDAxMzgwMDAifQ.xxx
     *
     * 6. 常用注解：
     *    @Api：描述Controller类
     *    @ApiOperation：描述接口方法
     *    @ApiParam：描述方法参数
     *    @ApiModel：描述实体类
     *    @ApiModelProperty：描述实体类属性
     *    @ApiResponse：描述响应信息
     *    @ApiResponses：描述多个响应信息
     *    @ApiIgnore：忽略某个接口或类（不生成文档）
     *
     * 7. 生产环境建议：
     *    - 在生产环境禁用Swagger（通过配置文件控制）
     *    - application.yml配置：
     *      swagger:
     *        enabled: false
     */
}
