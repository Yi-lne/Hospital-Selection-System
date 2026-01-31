package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.MedicalHistoryDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.MedicalHistoryMapper;
import com.chen.HospitalSelection.model.MedicalHistory;
import com.chen.HospitalSelection.vo.MedicalHistoryVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 病史服务测试类
 *
 * @author chen
 */
public class MedicalHistoryServiceTest {

    @Mock
    private MedicalHistoryMapper medicalHistoryMapper;

    @InjectMocks
    private com.chen.HospitalSelection.service.impl.MedicalHistoryServiceImpl medicalHistoryService;

    private MedicalHistory testHistory;
    private static final Long TEST_HISTORY_ID = 1L;
    private static final Long TEST_USER_ID = 1L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建测试病史
        testHistory = new MedicalHistory();
        testHistory.setId(TEST_HISTORY_ID);
        testHistory.setUserId(TEST_USER_ID);
        testHistory.setDiseaseName("高血压");
        testHistory.setDiagnosisDate(LocalDate.of(2023, 1, 15));
        testHistory.setStatus(1); // 治疗中
        testHistory.setIsDeleted(0);
        testHistory.setCreateTime(LocalDateTime.now());
        testHistory.setUpdateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("测试添加病史 - 成功")
    public void testAddMedicalHistory_Success() {
        // Arrange
        MedicalHistoryDTO dto = new MedicalHistoryDTO();
        dto.setDiseaseName("糖尿病");
        dto.setDiagnosisDate(LocalDate.now());
        dto.setStatus(1);

        when(medicalHistoryMapper.insert(any(MedicalHistory.class))).thenAnswer(invocation -> {
            MedicalHistory history = invocation.getArgument(0);
            history.setId(100L);
            return null;
        });

        // Act
        Long result = medicalHistoryService.addMedicalHistory(TEST_USER_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result);

        verify(medicalHistoryMapper, times(1)).insert(any(MedicalHistory.class));
    }

    @Test
    @DisplayName("测试获取病史列表 - 成功")
    public void testGetMedicalHistoryList_Success() {
        // Arrange
        MedicalHistory history2 = new MedicalHistory();
        history2.setId(2L);
        history2.setUserId(TEST_USER_ID);
        history2.setDiseaseName("冠心病");

        List<MedicalHistory> historyList = Arrays.asList(testHistory, history2);
        when(medicalHistoryMapper.selectByUserId(TEST_USER_ID)).thenReturn(historyList);

        // Act
        List<MedicalHistoryVO> result = medicalHistoryService.getMedicalHistoryList(TEST_USER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("高血压", result.get(0).getDiseaseName());
        assertEquals("冠心病", result.get(1).getDiseaseName());

        verify(medicalHistoryMapper, times(1)).selectByUserId(TEST_USER_ID);
    }

    @Test
    @DisplayName("测试获取病史详情 - 成功")
    public void testGetMedicalHistoryDetail_Success() {
        // Arrange
        when(medicalHistoryMapper.selectById(TEST_HISTORY_ID)).thenReturn(testHistory);

        // Act
        MedicalHistoryVO result = medicalHistoryService.getMedicalHistoryDetail(TEST_HISTORY_ID, TEST_USER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_HISTORY_ID, result.getId());
        assertEquals("高血压", result.getDiseaseName());

        verify(medicalHistoryMapper, times(1)).selectById(TEST_HISTORY_ID);
    }

    @Test
    @DisplayName("测试获取病史详情 - 无权访问")
    public void testGetMedicalHistoryDetail_NoPermission() {
        // Arrange
        when(medicalHistoryMapper.selectById(TEST_HISTORY_ID)).thenReturn(testHistory);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            medicalHistoryService.getMedicalHistoryDetail(TEST_HISTORY_ID, 999L);
        });

        assertEquals("无权查看此病史记录", exception.getMessage());
    }

    @Test
    @DisplayName("测试修改病史 - 成功")
    public void testUpdateMedicalHistory_Success() {
        // Arrange
        MedicalHistoryDTO dto = new MedicalHistoryDTO();
        dto.setDiseaseName("高血压（更新）");
        dto.setStatus(2); // 已康复

        when(medicalHistoryMapper.selectById(TEST_HISTORY_ID)).thenReturn(testHistory);
        when(medicalHistoryMapper.updateById(any(MedicalHistory.class))).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> medicalHistoryService.updateMedicalHistory(TEST_HISTORY_ID, TEST_USER_ID, dto));

        // Assert
        verify(medicalHistoryMapper, times(1)).updateById(any(MedicalHistory.class));
    }

    @Test
    @DisplayName("测试修改病史 - 无权修改")
    public void testUpdateMedicalHistory_NoPermission() {
        // Arrange
        MedicalHistoryDTO dto = new MedicalHistoryDTO();
        dto.setDiseaseName("高血压（更新）");

        when(medicalHistoryMapper.selectById(TEST_HISTORY_ID)).thenReturn(testHistory);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            medicalHistoryService.updateMedicalHistory(TEST_HISTORY_ID, 999L, dto);
        });

        assertEquals("无权修改此病史记录", exception.getMessage());
        verify(medicalHistoryMapper, never()).updateById(any(MedicalHistory.class));
    }

    @Test
    @DisplayName("测试删除病史 - 成功")
    public void testDeleteMedicalHistory_Success() {
        // Arrange
        when(medicalHistoryMapper.selectById(TEST_HISTORY_ID)).thenReturn(testHistory);
        when(medicalHistoryMapper.deleteById(TEST_HISTORY_ID)).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> medicalHistoryService.deleteMedicalHistory(TEST_HISTORY_ID, TEST_USER_ID));

        // Assert
        verify(medicalHistoryMapper, times(1)).deleteById(TEST_HISTORY_ID);
    }

    @Test
    @DisplayName("测试删除病史 - 无权删除")
    public void testDeleteMedicalHistory_NoPermission() {
        // Arrange
        when(medicalHistoryMapper.selectById(TEST_HISTORY_ID)).thenReturn(testHistory);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            medicalHistoryService.deleteMedicalHistory(TEST_HISTORY_ID, 999L);
        });

        assertEquals("无权删除此病史记录", exception.getMessage());
        verify(medicalHistoryMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("测试获取病史列表 - 空列表")
    public void testGetMedicalHistoryList_Empty() {
        // Arrange
        when(medicalHistoryMapper.selectByUserId(TEST_USER_ID)).thenReturn(Collections.emptyList());

        // Act
        List<MedicalHistoryVO> result = medicalHistoryService.getMedicalHistoryList(TEST_USER_ID);

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
