package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.IntegrationTestBase;
import com.chen.HospitalSelection.dto.CollectionDTO;
import com.chen.HospitalSelection.mapper.CollectionMapper;
import com.chen.HospitalSelection.mapper.HospitalMapper;
import com.chen.HospitalSelection.mapper.UserMapper;
import com.chen.HospitalSelection.model.Hospital;
import com.chen.HospitalSelection.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 收藏控制器集成测试类
 *
 * @author chen
 */
@Transactional
public class CollectionControllerTest extends IntegrationTestBase {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Autowired
    private HospitalMapper hospitalMapper;

    @Autowired
    private UserMapper userMapper;

    private Hospital testHospital;

    /**
     * 初始化测试数据
     */
    @Override
    protected void beforeTest() {
        // 创建测试用户
        User user = new User();
        user.setPhone("13900000020");
        user.setPassword(passwordEncoder.encode("Test123456"));
        user.setNickname("测试用户");
        user.setStatus(1);
        user.setIsDeleted(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        userMapper.insert(user);

        // 创建测试医院
        testHospital = new Hospital();
        testHospital.setHospitalName("测试医院2");
        testHospital.setHospitalLevel("grade3A");
        testHospital.setProvinceCode("440000");
        testHospital.setCityCode("440100");
        testHospital.setAreaCode("440103");
        testHospital.setAddress("测试地址2");
        testHospital.setPhone("020-87654321");
        testHospital.setKeyDepartments("心内科");
        testHospital.setRating(new BigDecimal("4.6"));
        testHospital.setReviewCount(150);
        testHospital.setIsMedicalInsurance(1);
        testHospital.setIsDeleted(0);
        testHospital.setCreateTime(LocalDateTime.now());
        testHospital.setUpdateTime(LocalDateTime.now());
        hospitalMapper.insert(testHospital);
    }

    @Test
    @DisplayName("测试添加收藏 - 收藏医院")
    public void testAddCollection_Hospital() throws Exception {
        // Arrange
        User user = userMapper.selectByPhone("13900000020");
        String token = generateTestToken(user.getId(), user.getPhone());

        CollectionDTO dto = new CollectionDTO();
        dto.setTargetType(1); // 1 = 医院
        dto.setTargetId(testHospital.getId());

        // Act & Assert
        mockMvc.perform(post("/collection/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("收藏成功"));
    }

    @Test
    @DisplayName("测试添加收藏 - 重复收藏")
    public void testAddCollection_AlreadyExists() throws Exception {
        // Arrange
        User user = userMapper.selectByPhone("13900000020");
        String token = generateTestToken(user.getId(), user.getPhone());

        CollectionDTO dto = new CollectionDTO();
        dto.setTargetType(1); // 1 = 医院
        dto.setTargetId(testHospital.getId());

        // 先添加一次
        mockMvc.perform(post("/collection/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        // Act - 再次添加
        // Assert
        mockMvc.perform(post("/collection/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400)) // 业务错误 - 已收藏
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("测试取消收藏 - 成功")
    public void testCancelCollection_Success() throws Exception {
        // Arrange
        User user = userMapper.selectByPhone("13900000020");
        String token = generateTestToken(user.getId(), user.getPhone());

        CollectionDTO dto = new CollectionDTO();
        dto.setTargetType(1); // 1 = 医院
        dto.setTargetId(testHospital.getId());

        // 先添加
        mockMvc.perform(post("/collection/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        // Act & Assert
        mockMvc.perform(delete("/collection/cancel")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("取消收藏成功"));
    }

    @Test
    @DisplayName("测试获取收藏列表 - 成功")
    public void testGetCollectionList_Success() throws Exception {
        // Arrange
        User user = userMapper.selectByPhone("13900000020");
        String token = generateTestToken(user.getId(), user.getPhone());

        // 先添加一个收藏
        CollectionDTO dto = new CollectionDTO();
        dto.setTargetType(1); // 1 = 医院
        dto.setTargetId(testHospital.getId());

        mockMvc.perform(post("/collection/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        // Act & Assert
        mockMvc.perform(get("/collection/list")
                        .header("Authorization", "Bearer " + token)
                        .param("targetType", "1") // 1 = 医院
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list").isArray())
                .andExpect(jsonPath("$.data.total").exists());
    }

    @Test
    @DisplayName("测试检查是否已收藏 - 已收藏")
    public void testCheckCollected_True() throws Exception {
        // Arrange
        User user = userMapper.selectByPhone("13900000020");
        String token = generateTestToken(user.getId(), user.getPhone());

        CollectionDTO dto = new CollectionDTO();
        dto.setTargetType(1); // 1 = 医院
        dto.setTargetId(testHospital.getId());

        // 先添加收藏
        mockMvc.perform(post("/collection/add")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andReturn();

        // Act & Assert
        mockMvc.perform(get("/collection/check")
                        .header("Authorization", "Bearer " + token)
                        .param("targetType", "1") // 1 = 医院
                        .param("targetId", testHospital.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(true));
    }

    @Test
    @DisplayName("测试检查是否已收藏 - 未收藏")
    public void testCheckCollected_False() throws Exception {
        // Arrange
        User user = userMapper.selectByPhone("13900000020");
        String token = generateTestToken(user.getId(), user.getPhone());

        // Act & Assert
        mockMvc.perform(get("/collection/check")
                        .header("Authorization", "Bearer " + token)
                        .param("targetType", "1") // 1 = 医院
                        .param("targetId", "99999")) // 不存在的医院
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").value(false));
    }

    @Test
    @DisplayName("测试收藏数量统计 - 成功")
    public void testGetCollectionCount_Success() throws Exception {
        // Arrange
        User user = userMapper.selectByPhone("13900000020");
        String token = generateTestToken(user.getId(), user.getPhone());

        // Act & Assert
        mockMvc.perform(get("/collection/count")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }
}
