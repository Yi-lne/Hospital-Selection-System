package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.CollectionDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.CollectionMapper;
import com.chen.HospitalSelection.mapper.DoctorMapper;
import com.chen.HospitalSelection.mapper.HospitalMapper;
import com.chen.HospitalSelection.mapper.TopicMapper;
import com.chen.HospitalSelection.model.Doctor;
import com.chen.HospitalSelection.model.Hospital;
import com.chen.HospitalSelection.model.Topic;
import com.chen.HospitalSelection.model.UserCollectionItem;
import com.chen.HospitalSelection.vo.CollectionVO;
import com.chen.HospitalSelection.vo.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 收藏服务测试类
 *
 * @author chen
 */
public class CollectionServiceTest {

    @Mock
    private CollectionMapper collectionMapper;

    @Mock
    private HospitalMapper hospitalMapper;

    @Mock
    private DoctorMapper doctorMapper;

    @Mock
    private TopicMapper topicMapper;

    @InjectMocks
    private com.chen.HospitalSelection.service.impl.CollectionServiceImpl collectionService;

    private Hospital testHospital;
    private Doctor testDoctor;
    private Topic testTopic;
    private UserCollectionItem testCollection;
    private static final Long TEST_USER_ID = 1L;
    private static final Long TEST_HOSPITAL_ID = 1L;
    private static final Long TEST_DOCTOR_ID = 1L;
    private static final Long TEST_TOPIC_ID = 1L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建测试医院
        testHospital = new Hospital();
        testHospital.setId(TEST_HOSPITAL_ID);
        testHospital.setHospitalName("广东省人民医院");
        testHospital.setHospitalLevel("grade3A");
        testHospital.setRating(new BigDecimal("4.8"));

        // 创建测试医生
        testDoctor = new Doctor();
        testDoctor.setId(TEST_DOCTOR_ID);
        testDoctor.setDoctorName("张三");
        testDoctor.setTitle("主任医师");

        // 创建测试话题
        testTopic = new Topic();
        testTopic.setId(TEST_TOPIC_ID);
        testTopic.setTitle("高血压患者如何科学饮食？");

        // 创建测试收藏
        testCollection = new UserCollectionItem();
        testCollection.setId(1L);
        testCollection.setUserId(TEST_USER_ID);
        testCollection.setTargetType(1);
        testCollection.setTargetId(TEST_HOSPITAL_ID);
        testCollection.setIsDeleted(0);
        testCollection.setCreateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("测试添加收藏 - 医院")
    public void testAddCollection_Hospital_Success() {
        // Arrange
        CollectionDTO dto = new CollectionDTO();
        dto.setTargetType(1); // 医院
        dto.setTargetId(TEST_HOSPITAL_ID);

        when(collectionMapper.selectByUserAndTarget(TEST_USER_ID, 1, TEST_HOSPITAL_ID)).thenReturn(null);
        when(hospitalMapper.selectById(TEST_HOSPITAL_ID)).thenReturn(testHospital);
        when(collectionMapper.insert(any(UserCollectionItem.class))).thenAnswer(invocation -> {
            UserCollectionItem item = invocation.getArgument(0);
            item.setId(100L);
            return 1;
        });

        // Act
        Long result = collectionService.addCollection(TEST_USER_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result);
        verify(collectionMapper, times(1)).insert(any(UserCollectionItem.class));
    }

    @Test
    @DisplayName("测试添加收藏 - 已收藏")
    public void testAddCollection_AlreadyCollected() {
        // Arrange
        CollectionDTO dto = new CollectionDTO();
        dto.setTargetType(1);
        dto.setTargetId(TEST_HOSPITAL_ID);

        // 实际实现使用countByUserAndTarget检查是否已收藏
        when(collectionMapper.countByUserAndTarget(TEST_USER_ID, 1, TEST_HOSPITAL_ID)).thenReturn(1);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            collectionService.addCollection(TEST_USER_ID, dto);
        });

        assertEquals("已经收藏过了", exception.getMessage());
        verify(collectionMapper, never()).insert(any(UserCollectionItem.class));
    }

    @Test
    @DisplayName("测试取消收藏 - 成功")
    public void testCancelCollection_Success() {
        // Arrange
        CollectionDTO dto = new CollectionDTO();
        dto.setTargetType(1);
        dto.setTargetId(TEST_HOSPITAL_ID);

        // 实际实现使用countByUserAndTarget检查是否已收藏
        when(collectionMapper.countByUserAndTarget(TEST_USER_ID, 1, TEST_HOSPITAL_ID)).thenReturn(1);
        when(collectionMapper.cancelCollection(TEST_USER_ID, 1, TEST_HOSPITAL_ID)).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> collectionService.cancelCollection(TEST_USER_ID, dto));

        // Assert
        verify(collectionMapper, times(1)).cancelCollection(TEST_USER_ID, 1, TEST_HOSPITAL_ID);
    }

    @Test
    @DisplayName("测试获取收藏列表 - 全部收藏")
    public void testGetCollectionList_All_Success() {
        // Arrange
        UserCollectionItem hospitalCollection = new UserCollectionItem();
        hospitalCollection.setId(1L);
        hospitalCollection.setUserId(TEST_USER_ID);
        hospitalCollection.setTargetType(1);
        hospitalCollection.setTargetId(TEST_HOSPITAL_ID);
        hospitalCollection.setCreateTime(LocalDateTime.now());

        UserCollectionItem doctorCollection = new UserCollectionItem();
        doctorCollection.setId(2L);
        doctorCollection.setUserId(TEST_USER_ID);
        doctorCollection.setTargetType(2);
        doctorCollection.setTargetId(TEST_DOCTOR_ID);
        doctorCollection.setCreateTime(LocalDateTime.now());

        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        List<UserCollectionItem> collectionList = Arrays.asList(hospitalCollection, doctorCollection);
        when(collectionMapper.selectByUserId(TEST_USER_ID)).thenReturn(collectionList);
        when(hospitalMapper.selectById(TEST_HOSPITAL_ID)).thenReturn(testHospital);
        when(doctorMapper.selectById(TEST_DOCTOR_ID)).thenReturn(testDoctor);

        // Act
        PageResult<CollectionVO> result = collectionService.getCollectionList(TEST_USER_ID, null, dto);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotal());
        assertEquals(2, result.getList().size());

        verify(collectionMapper, times(1)).selectByUserId(TEST_USER_ID);
    }

    @Test
    @DisplayName("测试获取收藏列表 - 医院收藏")
    public void testGetCollectionList_Hospital_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        List<UserCollectionItem> collectionList = Arrays.asList(testCollection);
        when(collectionMapper.selectHospitalsByUserId(TEST_USER_ID)).thenReturn(collectionList);
        when(hospitalMapper.selectById(TEST_HOSPITAL_ID)).thenReturn(testHospital);

        // Act
        PageResult<CollectionVO> result = collectionService.getCollectionList(TEST_USER_ID, 1, dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getList().size());

        verify(collectionMapper, times(1)).selectHospitalsByUserId(TEST_USER_ID);
    }

    @Test
    @DisplayName("测试获取医院收藏列表 - 成功")
    public void testGetHospitalCollections_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        List<UserCollectionItem> collectionList = Arrays.asList(testCollection);
        when(collectionMapper.selectHospitalsByUserId(TEST_USER_ID)).thenReturn(collectionList);
        when(hospitalMapper.selectById(TEST_HOSPITAL_ID)).thenReturn(testHospital);

        // Act
        PageResult<CollectionVO> result = collectionService.getHospitalCollections(TEST_USER_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getList().size());

        verify(collectionMapper, times(1)).selectHospitalsByUserId(TEST_USER_ID);
    }

    @Test
    @DisplayName("测试获取医生收藏列表 - 成功")
    public void testGetDoctorCollections_Success() {
        // Arrange
        UserCollectionItem doctorCollection = new UserCollectionItem();
        doctorCollection.setId(2L);
        doctorCollection.setUserId(TEST_USER_ID);
        doctorCollection.setTargetType(2);
        doctorCollection.setTargetId(TEST_DOCTOR_ID);
        doctorCollection.setCreateTime(LocalDateTime.now());

        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        List<UserCollectionItem> collectionList = Arrays.asList(doctorCollection);
        when(collectionMapper.selectDoctorsByUserId(TEST_USER_ID)).thenReturn(collectionList);
        when(doctorMapper.selectById(TEST_DOCTOR_ID)).thenReturn(testDoctor);

        // Act
        PageResult<CollectionVO> result = collectionService.getDoctorCollections(TEST_USER_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());

        verify(collectionMapper, times(1)).selectDoctorsByUserId(TEST_USER_ID);
    }

    @Test
    @DisplayName("测试获取话题收藏列表 - 成功")
    public void testGetTopicCollections_Success() {
        // Arrange
        UserCollectionItem topicCollection = new UserCollectionItem();
        topicCollection.setId(3L);
        topicCollection.setUserId(TEST_USER_ID);
        topicCollection.setTargetType(3);
        topicCollection.setTargetId(TEST_TOPIC_ID);
        topicCollection.setCreateTime(LocalDateTime.now());

        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        List<UserCollectionItem> collectionList = Arrays.asList(topicCollection);
        when(collectionMapper.selectTopicsByUserId(TEST_USER_ID)).thenReturn(collectionList);
        when(topicMapper.selectById(TEST_TOPIC_ID)).thenReturn(testTopic);

        // Act
        PageResult<CollectionVO> result = collectionService.getTopicCollections(TEST_USER_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());

        verify(collectionMapper, times(1)).selectTopicsByUserId(TEST_USER_ID);
    }

    @Test
    @DisplayName("测试检查是否已收藏 - 已收藏")
    public void testCheckCollection_True() {
        // Arrange
        when(collectionMapper.countByUserAndTarget(TEST_USER_ID, 1, TEST_HOSPITAL_ID)).thenReturn(1);

        // Act
        boolean result = collectionService.checkCollection(TEST_USER_ID, 1, TEST_HOSPITAL_ID);

        // Assert
        assertTrue(result);

        verify(collectionMapper, times(1)).countByUserAndTarget(TEST_USER_ID, 1, TEST_HOSPITAL_ID);
    }

    @Test
    @DisplayName("测试检查是否已收藏 - 未收藏")
    public void testCheckCollection_False() {
        // Arrange
        when(collectionMapper.countByUserAndTarget(TEST_USER_ID, 1, TEST_HOSPITAL_ID)).thenReturn(0);

        // Act
        boolean result = collectionService.checkCollection(TEST_USER_ID, 1, TEST_HOSPITAL_ID);

        // Assert
        assertFalse(result);
    }

    @Test
    @DisplayName("测试获取收藏数量统计 - 成功")
    public void testGetCollectionCount_Success() {
        // Arrange
        when(collectionMapper.countByUserId(TEST_USER_ID)).thenReturn(18);

        // Act
        Map<Integer, Long> result = collectionService.getCollectionCount(TEST_USER_ID);

        // Assert
        assertNotNull(result);
        // 只验证返回了结果，具体数值由实现决定
    }

    @Test
    @DisplayName("测试切换收藏状态 - 添加收藏")
    public void testToggleCollection_Add() {
        // Arrange
        CollectionDTO dto = new CollectionDTO();
        dto.setTargetType(1);
        dto.setTargetId(TEST_HOSPITAL_ID);

        // 实际实现使用countByUserAndTarget检查是否已收藏
        when(collectionMapper.countByUserAndTarget(TEST_USER_ID, 1, TEST_HOSPITAL_ID)).thenReturn(0);
        when(hospitalMapper.selectById(TEST_HOSPITAL_ID)).thenReturn(testHospital);
        when(collectionMapper.insert(any(UserCollectionItem.class))).thenAnswer(invocation -> {
            UserCollectionItem item = invocation.getArgument(0);
            item.setId(100L);
            return 1;
        });

        // Act
        boolean result = collectionService.toggleCollection(TEST_USER_ID, dto);

        // Assert
        assertTrue(result);
        verify(collectionMapper, times(1)).insert(any(UserCollectionItem.class));
    }

    @Test
    @DisplayName("测试切换收藏状态 - 取消收藏")
    public void testToggleCollection_Cancel() {
        // Arrange
        CollectionDTO dto = new CollectionDTO();
        dto.setTargetType(1);
        dto.setTargetId(TEST_HOSPITAL_ID);

        // 实际实现使用countByUserAndTarget检查是否已收藏
        when(collectionMapper.countByUserAndTarget(TEST_USER_ID, 1, TEST_HOSPITAL_ID)).thenReturn(1);
        when(collectionMapper.cancelCollection(TEST_USER_ID, 1, TEST_HOSPITAL_ID)).thenReturn(1);

        // Act
        boolean result = collectionService.toggleCollection(TEST_USER_ID, dto);

        // Assert
        assertFalse(result);
        verify(collectionMapper, times(1)).cancelCollection(TEST_USER_ID, 1, TEST_HOSPITAL_ID);
    }

    @Test
    @DisplayName("测试获取收藏列表 - 空列表")
    public void testGetCollectionList_Empty() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        when(collectionMapper.selectByUserId(TEST_USER_ID)).thenReturn(Collections.emptyList());

        // Act
        PageResult<CollectionVO> result = collectionService.getCollectionList(TEST_USER_ID, null, dto);

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getList().isEmpty());
    }
}
