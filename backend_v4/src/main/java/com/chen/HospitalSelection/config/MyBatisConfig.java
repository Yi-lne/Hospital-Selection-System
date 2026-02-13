package com.chen.HospitalSelection.config;

import com.github.pagehelper.PageInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Properties;

/**
 * MyBatis配置类
 *
 * 功能说明：
 * 1. 配置分页插件（PageHelper）
 * 2. 配置驼峰命名映射
 * 3. 配置其他MyBatis相关设置
 *
 * @author chen
 * @version 1.0.0
 */
@Configuration
public class MyBatisConfig {

    /**
     * 配置PageHelper分页插件
     *
     * 使用说明：
     * 1. 在查询方法前调用PageHelper.startPage(pageNum, pageSize)
     * 2. 执行查询后，会自动进行分页
     * 3. 返回结果可以使用PageInfo包装，获取分页信息
     *
     * 示例：
     * PageHelper.startPage(1, 10);
     * List<Hospital> list = hospitalMapper.selectAll();
     * PageInfo<Hospital> pageInfo = new PageInfo<>(list);
     *
     * @return PageInterceptor分页拦截器
     */
    @Bean
    public PageInterceptor pageInterceptor(DataSource dataSource) {
        PageInterceptor pageInterceptor = new PageInterceptor();

        // 配置分页参数
        Properties properties = new Properties();

        // ========== 分页插件参数配置 ==========

        // 分页合理化参数
        // 当pageNum<=0时，查询第一页
        // 当pageNum>pages时，查询最后一页
        properties.setProperty("reasonable", "true");

        // 支持通过Mapper接口参数来传递分页参数
        // 默认值为false，分页插件会从参数值中根据params配置的字段中获取值
        properties.setProperty("supportMethodsArguments", "false");

        // 数据库方言（自动检测）
        // 可选值：oracle,mysql,mariadb,sqlite,hsqldb,postgresql,db2,sqlserver,informix,h2,sqlserver2012
        // 不配置时，会自动根据数据库连接url判断
        properties.setProperty("helperDialect", "mysql");

        // ========== 分页参数映射配置 ==========
        // 为了支持startPage(Object params)方法，增加了该参数来配置参数映射
        // 用于从对象中根据属性名取值
        // 例如：配置为pageNum=pageNumKey;pageSize=pageSizeKey
        // 则params.put("pageNumKey", 1); params.put("pageSizeKey", 10);
        // 默认值为pageNum=pageNum;pageSize=pageSize;count=countSql;reasonable=reasonable;pageSizeZero=pageSizeZero
        properties.setProperty("params", "pageNum=pageNum;pageSize=pageSize;count=countSql;reasonable=reasonable;pageSizeZero=pageSizeZero");

        //pageSizeZero：true时，当pageSize=0时，查询全部结果
        properties.setProperty("pageSizeZero", "true");

        // 自动配置分页参数
        // 当该参数设置为true时，会自动从参数中根据params配置的字段中获取值
        properties.setProperty("autoRuntimeDialect", "true");

        // ========== 其他配置 ==========

        // 当使用纯SQL语句进行分页时，是否计算总记录数
        properties.setProperty("countSql", "true");

        // 分页查询时，是否自动进行count查询
        properties.setProperty("autoDialect", "true");

        pageInterceptor.setProperties(properties);

        return pageInterceptor;
    }

    /**
     * 配置MyBatis全局设置（可选）
     * 注意：这些配置也可以在application.yml中配置
     *
     * application.yml配置示例：
     * mybatis:
     *   configuration:
     *     # 驼峰命名映射：数据库字段user_name自动映射到Java属性userName
     *     map-underscore-to-camel-case: true
     *     # 日志实现
     *     log-impl: org.apache.ibatis.logging.stdout.StdOutImpl
     *     # 开启二级缓存
     *     cache-enabled: true
     *     # 懒加载
     *     lazy-loading-enabled: true
     *     aggressive-lazy-loading: false
     *   mapper-locations: classpath:mapper/*.xml
     *   type-aliases-package: com.chen.HospitalSelection.model
     *
     * 在application.yml中配置的优势：
     * 1. 集中管理配置
     * 2. 便于修改和维护
     * 3. 环境切换方便（dev/prod）
     */

    /**
     * MyBatis配置建议：
     *
     * 1. 分页使用建议：
     *    - 简单分页：直接使用PageHelper.startPage(pageNum, pageSize)
     *    - 复杂分页：使用PageHelper.startPage(pageNum, pageSize, count)
     *    - 不查询总数：使用PageHelper.startPage(pageNum, pageSize, false)
     *
     * 2. 驼峰命名映射：
     *    - 数据库字段：user_name, hospital_id, create_time
     *    - Java属性：userName, hospitalId, createTime
     *    - 开启后自动映射，无需在resultMap中配置
     *
     * 3. 性能优化：
     *    - 合理使用count参数，不需要总数时设置为false
     *    - 使用reasonable参数，避免查询超出范围的页码
     *    - 大数据量分页建议使用lastPage参数配合
     *
     * 4. 注意事项：
     *    - PageHelper.startPage必须紧跟在查询方法之前
     *    - 只对紧跟在startPage后的第一条查询语句生效
     *    - 在线程本地中存储分页参数，注意线程安全问题
     */
}
