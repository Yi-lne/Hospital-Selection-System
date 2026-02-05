package com.chen.HospitalSelection;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import javax.sql.DataSource;

/**
 * 测试配置类
 * 为需要真实数据库连接的集成测试提供SqlSessionFactory
 *
 * @author chen
 */
@TestConfiguration
public class TestConfig {

    /**
     * 配置SqlSessionFactory用于测试
     *
     * @param dataSource 测试数据源
     * @return SqlSessionFactory实例
     * @throws Exception 配置异常
     */
    @Bean
    public SqlSessionFactory sqlSessionFactory(DataSource dataSource) throws Exception {
        SqlSessionFactoryBean sessionFactory = new SqlSessionFactoryBean();
        sessionFactory.setDataSource(dataSource);

        // 设置类型别名包
        sessionFactory.setTypeAliasesPackage("com.chen.HospitalSelection.model");

        // 设置映射文件位置
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        sessionFactory.setMapperLocations(resolver.getResources("classpath:mapper/*.xml"));

        // 配置MyBatis设置
        org.apache.ibatis.session.Configuration configuration = new org.apache.ibatis.session.Configuration();
        configuration.setMapUnderscoreToCamelCase(true);
        sessionFactory.setConfiguration(configuration);

        return sessionFactory.getObject();
    }

    /**
     * 配置SqlSessionTemplate
     *
     * @param sqlSessionFactory SqlSessionFactory实例
     * @return SqlSessionTemplate实例
     */
    @Bean
    public SqlSessionTemplate sqlSessionTemplate(SqlSessionFactory sqlSessionFactory) {
        return new SqlSessionTemplate(sqlSessionFactory);
    }
}