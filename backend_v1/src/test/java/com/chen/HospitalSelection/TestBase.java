package com.chen.HospitalSelection;

import com.chen.HospitalSelection.config.SpringSecurityConfig;
import com.chen.HospitalSelection.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * 测试基类
 * 提供测试所需的公共配置和工具方法
 *
 * @author chen
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public abstract class TestBase {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JwtUtil jwtUtil;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    /**
     * 测试用的用户ID
     */
    protected static final Long TEST_USER_ID = 1L;

    /**
     * 测试用的手机号
     */
    protected static final String TEST_PHONE = "13800000001";

    /**
     * 测试用的密码
     */
    protected static final String TEST_PASSWORD = "Test123456";

    /**
     * 测试用的另一个用户ID
     */
    protected static final Long TEST_USER_ID_2 = 2L;

    /**
     * 测试用的医院ID
     */
    protected static final Long TEST_HOSPITAL_ID = 1L;

    /**
     * 测试用的医生ID
     */
    protected static final Long TEST_DOCTOR_ID = 1L;

    /**
     * 测试用的话题ID
     */
    protected static final Long TEST_TOPIC_ID = 1L;

    @BeforeEach
    public void setUp() {
        // 每个测试前的初始化操作
        beforeTest();
    }

    /**
     * 子类可重写的初始化方法
     */
    protected void beforeTest() {
        // 默认空实现，子类可重写
    }

    /**
     * 生成测试用的JWT Token
     *
     * @param userId 用户ID
     * @param phone  手机号
     * @return JWT Token
     */
    protected String generateTestToken(Long userId, String phone) {
        return jwtUtil.generateToken(userId, phone);
    }

    /**
     * 生成默认测试用户的JWT Token
     *
     * @return JWT Token
     */
    protected String generateDefaultTestToken() {
        return generateTestToken(TEST_USER_ID, TEST_PHONE);
    }

    /**
     * 获取Bearer Token格式的Authorization头
     *
     * @param token JWT Token
     * @return Authorization头值
     */
    protected String getAuthorizationHeader(String token) {
        return "Bearer " + token;
    }

    /**
     * 获取默认测试用户的Authorization头
     *
     * @return Authorization头值
     */
    protected String getDefaultAuthorizationHeader() {
        return getAuthorizationHeader(generateDefaultTestToken());
    }
}