package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.QueryHistoryMapper;
import com.chen.HospitalSelection.model.QueryHistory;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.QueryHistoryVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 查询历史服务测试类
 *
 * @author chen
 */
public class QueryHistoryServiceTest {

    @Mock
    private QueryHistoryMapper queryHistoryMapper;

    @InjectMocks
    private com.chen.HospitalSelection.service.impl.QueryHistoryServiceImpl queryHistoryService;

    private QueryHistory testHistory;
    private static final Long TEST_HISTORY_ID = 1L;
    private static final Long TEST_USER_ID = 1L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建测试查询历史
        testHistory = new QueryHistory();
        testHistory.setId(TEST_HISTORY_ID);
        testHistory.setUserId(TEST_USER_ID);
        testHistory.setQueryType(1); // 医院
        testHistory.setTargetId(100L);
        testHistory.setQueryParams("{\"hospitalLevel\":\"grade3A\"}");
        testHistory.setIsDeleted(0);
        testHistory.setCreateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("测试记录查询 - 成功")
    public void testRecordQuery_Success() {
        // Arrange
        when(queryHistoryMapper.insert(any(QueryHistory.class))).thenAnswer(invocation -> {
            QueryHistory history = invocation.getArgument(0);
            history.setId(100L);
            return 1;
        });

        // Act
        Long result = queryHistoryService.recordQuery(TEST_USER_ID, 1, 100L, "{\"level\":\"grade3A\"}");

        // Assert
        assertNotNull(result);
        assertEquals(100L, result);
        verify(queryHistoryMapper, times(1)).insert(any(QueryHistory.class));
    }

    @Test
    @DisplayName("测试获取查询历史列表 - 成功")
    public void testGetQueryHistoryList_Success() {
        // Arrange
        QueryHistory history2 = new QueryHistory();
        history2.setId(2L);
        history2.setUserId(TEST_USER_ID);
        history2.setQueryType(2); // 医生
        history2.setTargetId(200L);
        history2.setCreateTime(LocalDateTime.now());

        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        List<QueryHistory> historyList = Arrays.asList(testHistory, history2);
        when(queryHistoryMapper.selectByUserId(TEST_USER_ID)).thenReturn(historyList);

        // Act
        PageResult<QueryHistoryVO> result = queryHistoryService.getQueryHistoryList(TEST_USER_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotal());
        assertEquals(2, result.getList().size());

        verify(queryHistoryMapper, times(1)).selectByUserId(TEST_USER_ID);
    }

    @Test
    @DisplayName("测试获取查询历史列表 - 空列表")
    public void testGetQueryHistoryList_Empty() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        when(queryHistoryMapper.selectByUserId(TEST_USER_ID)).thenReturn(Collections.emptyList());

        // Act
        PageResult<QueryHistoryVO> result = queryHistoryService.getQueryHistoryList(TEST_USER_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals(0L, result.getTotal());
        assertTrue(result.getList().isEmpty());
    }

    @Test
    @DisplayName("测试删除查询历史 - 成功")
    public void testDeleteQueryHistory_Success() {
        // Arrange
        when(queryHistoryMapper.selectById(TEST_HISTORY_ID)).thenReturn(testHistory);
        when(queryHistoryMapper.deleteById(TEST_HISTORY_ID)).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> queryHistoryService.deleteQueryHistory(TEST_HISTORY_ID, TEST_USER_ID));

        // Assert
        verify(queryHistoryMapper, times(1)).deleteById(TEST_HISTORY_ID);
    }

    @Test
    @DisplayName("测试删除查询历史 - 无权删除")
    public void testDeleteQueryHistory_NoPermission() {
        // Arrange
        when(queryHistoryMapper.selectById(TEST_HISTORY_ID)).thenReturn(testHistory);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            queryHistoryService.deleteQueryHistory(TEST_HISTORY_ID, 999L);
        });

        assertEquals("无权删除此历史记录", exception.getMessage());
        verify(queryHistoryMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("测试清空查询历史 - 成功")
    public void testClearQueryHistory_Success() {
        // Arrange
        when(queryHistoryMapper.deleteByUserId(TEST_USER_ID)).thenReturn(10);

        // Act
        assertDoesNotThrow(() -> queryHistoryService.clearQueryHistory(TEST_USER_ID));

        // Assert
        verify(queryHistoryMapper, times(1)).deleteByUserId(TEST_USER_ID);
    }

    @Test
    @DisplayName("测试获取医院查询历史 - 成功")
    public void testGetHospitalQueryHistory_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        // 实际实现调用selectByUserIdAndType(userId, 1)
        List<QueryHistory> historyList = Arrays.asList(testHistory);
        when(queryHistoryMapper.selectByUserIdAndType(TEST_USER_ID, 1)).thenReturn(historyList);

        // Act
        PageResult<QueryHistoryVO> result = queryHistoryService.getHospitalQueryHistory(TEST_USER_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(1, result.getList().size());

        verify(queryHistoryMapper, times(1)).selectByUserIdAndType(TEST_USER_ID, 1);
    }

    @Test
    @DisplayName("测试获取医生查询历史 - 成功")
    public void testGetDoctorQueryHistory_Success() {
        // Arrange
        QueryHistory doctorHistory = new QueryHistory();
        doctorHistory.setId(2L);
        doctorHistory.setUserId(TEST_USER_ID);
        doctorHistory.setQueryType(2);
        doctorHistory.setTargetId(200L);
        doctorHistory.setCreateTime(LocalDateTime.now());

        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        // 实际实现调用selectByUserIdAndType(userId, 2)
        List<QueryHistory> historyList = Arrays.asList(doctorHistory);
        when(queryHistoryMapper.selectByUserIdAndType(TEST_USER_ID, 2)).thenReturn(historyList);

        // Act
        PageResult<QueryHistoryVO> result = queryHistoryService.getDoctorQueryHistory(TEST_USER_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());

        verify(queryHistoryMapper, times(1)).selectByUserIdAndType(TEST_USER_ID, 2);
    }

    @Test
    @DisplayName("测试获取话题查询历史 - 成功")
    public void testGetTopicQueryHistory_Success() {
        // Arrange
        QueryHistory topicHistory = new QueryHistory();
        topicHistory.setId(3L);
        topicHistory.setUserId(TEST_USER_ID);
        topicHistory.setQueryType(3);
        topicHistory.setTargetId(300L);
        topicHistory.setCreateTime(LocalDateTime.now());

        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        // 实际实现调用selectByUserIdAndType(userId, 3)
        List<QueryHistory> historyList = Arrays.asList(topicHistory);
        when(queryHistoryMapper.selectByUserIdAndType(TEST_USER_ID, 3)).thenReturn(historyList);

        // Act
        PageResult<QueryHistoryVO> result = queryHistoryService.getTopicQueryHistory(TEST_USER_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());

        verify(queryHistoryMapper, times(1)).selectByUserIdAndType(TEST_USER_ID, 3);
    }

    @Test
    @DisplayName("测试获取最近查询 - 成功")
    public void testGetRecentQueries_Success() {
        // Arrange
        QueryHistory history2 = new QueryHistory();
        history2.setId(2L);
        history2.setUserId(TEST_USER_ID);
        history2.setQueryType(2);
        history2.setTargetId(200L);
        history2.setCreateTime(LocalDateTime.now());

        List<QueryHistory> historyList = Arrays.asList(testHistory, history2);
        when(queryHistoryMapper.selectRecentByUserId(TEST_USER_ID, 10)).thenReturn(historyList);

        // Act
        List<QueryHistoryVO> result = queryHistoryService.getRecentQueries(TEST_USER_ID, 10);

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());

        verify(queryHistoryMapper, times(1)).selectRecentByUserId(TEST_USER_ID, 10);
    }

    @Test
    @DisplayName("测试获取热门查询 - 成功")
    public void testGetHotQueries_Success() {
        // Arrange
        // 实际实现调用selectAll()
        List<QueryHistory> historyList = Arrays.asList(testHistory);
        when(queryHistoryMapper.selectAll()).thenReturn(historyList);

        // Act
        List<QueryHistoryVO> result = queryHistoryService.getHotQueries(1, 10);

        // Assert
        assertNotNull(result);

        verify(queryHistoryMapper, times(1)).selectAll();
    }
}
