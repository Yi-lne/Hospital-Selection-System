package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.dto.CommentDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.TopicPublishDTO;
import com.chen.HospitalSelection.service.CommunityService;
import com.chen.HospitalSelection.util.JwtUtil;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.TopicDetailVO;
import com.chen.HospitalSelection.vo.TopicVO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 社区控制器测试类
 *
 * @author chen
 */
@SpringBootTest
@AutoConfigureMockMvc
public class CommunityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommunityService communityService;

    @MockBean
    private JwtUtil jwtUtil;

    private static final String TEST_TOKEN = "test-jwt-token";

    private TopicVO testTopicVO;
    private TopicDetailVO testTopicDetailVO;
    private PageResult<TopicVO> testPageResult;

    @BeforeEach
    public void setUp() {
        when(jwtUtil.getUserIdFromToken(TEST_TOKEN)).thenReturn(1L);

        testTopicVO = new TopicVO();
        testTopicVO.setId(1L);
        testTopicVO.setUserId(1L);
        testTopicVO.setNickname("测试用户");
        testTopicVO.setBoardLevel1("心血管区");
        testTopicVO.setBoardLevel2("高血压");
        testTopicVO.setTitle("高血压患者如何科学饮食？");
        testTopicVO.setContentSummary("我最近被诊断为高血压...");
        testTopicVO.setLikeCount(56);
        testTopicVO.setCommentCount(23);
        testTopicVO.setViewCount(342);
        testTopicVO.setIsLiked(false);
        testTopicVO.setCreateTime(LocalDateTime.now());

        testTopicDetailVO = new TopicDetailVO();
        testTopicDetailVO.setId(1L);
        testTopicDetailVO.setUserId(1L);
        testTopicDetailVO.setNickname("测试用户");
        testTopicDetailVO.setTitle("高血压患者如何科学饮食？");
        testTopicDetailVO.setContent("我最近被诊断为高血压...");
        testTopicDetailVO.setLikeCount(56);
        testTopicDetailVO.setCommentCount(23);

        testPageResult = new PageResult<>(1L, 1, 10, Arrays.asList(testTopicVO));
    }

    @Test
    @DisplayName("测试话题列表 - 成功")
    public void testGetTopicList_Success() throws Exception {
        // Arrange
        when(communityService.getTopicList(any(PageQueryDTO.class))).thenReturn(testPageResult);

        // Act & Assert
        mockMvc.perform(get("/community/topics")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1))
                .andExpect(jsonPath("$.data.list[0].title").value("高血压患者如何科学饮食？"));
    }

    @Test
    @DisplayName("测试按板块查询话题 - 成功")
    public void testGetTopicsByBoard_Success() throws Exception {
        // Arrange
        when(communityService.getTopicsByBoard(anyString(), anyString(), any(PageQueryDTO.class)))
                .thenReturn(testPageResult);

        // Act & Assert
        mockMvc.perform(get("/community/topics")
                        .param("page", "1")
                        .param("pageSize", "10")
                        .param("boardLevel1", "心血管区")
                        .param("boardLevel2", "高血压"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.list[0].boardLevel1").value("心血管区"));
    }

    @Test
    @DisplayName("测试话题详情 - 成功")
    public void testGetTopicDetail_Success() throws Exception {
        // Arrange
        when(communityService.getTopicDetail(eq(1L), any())).thenReturn(testTopicDetailVO);

        // Act & Assert
        mockMvc.perform(get("/community/topic/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.title").value("高血压患者如何科学饮食？"));
    }

    @Test
    @DisplayName("测试发布话题 - 成功")
    public void testPublishTopic_Success() throws Exception {
        // Arrange
        TopicPublishDTO dto = new TopicPublishDTO();
        dto.setDiseaseCode("hypertension");
        dto.setBoardLevel1("心血管区");
        dto.setBoardLevel2("高血压");
        dto.setBoardType(1);
        dto.setTitle("新话题标题");
        dto.setContent("新话题内容");

        when(communityService.publishTopic(anyLong(), any(TopicPublishDTO.class))).thenReturn(1L);
        when(communityService.getTopicDetail(eq(1L), anyLong())).thenReturn(testTopicDetailVO);

        // Act & Assert
        mockMvc.perform(post("/community/topic")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("发布成功"));
    }

    @Test
    @DisplayName("测试修改话题 - 成功")
    public void testUpdateTopic_Success() throws Exception {
        // Arrange
        com.chen.HospitalSelection.dto.TopicUpdateDTO dto = new com.chen.HospitalSelection.dto.TopicUpdateDTO();
        dto.setTitle("修改后的标题");
        dto.setContent("修改后的内容");

        // Act & Assert
        mockMvc.perform(put("/community/topic/1")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("修改成功"));
    }

    @Test
    @DisplayName("测试删除话题 - 成功")
    public void testDeleteTopic_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(delete("/community/topic/1")
                        .header("Authorization", "Bearer " + TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("删除成功"));
    }

    @Test
    @DisplayName("测试发表评论 - 成功")
    public void testAddComment_Success() throws Exception {
        // Arrange
        CommentDTO dto = new CommentDTO();
        dto.setTopicId(1L);
        dto.setContent("这是一条评论");
        dto.setParentId(0L);

        when(communityService.addComment(anyLong(), any(CommentDTO.class))).thenReturn(1L);

        // Act & Assert
        mockMvc.perform(post("/community/comment")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("评论成功"));
    }

    @Test
    @DisplayName("测试点赞话题 - 成功")
    public void testLikeTopic_Success() throws Exception {
        // Arrange
        doNothing().when(communityService).likeTopic(anyLong(), anyLong());

        // Act & Assert
        mockMvc.perform(post("/community/like/topic")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .param("topicId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("点赞成功"));
    }

    @Test
    @DisplayName("测试取消点赞话题 - 成功")
    public void testUnlikeTopic_Success() throws Exception {
        // Arrange
        doNothing().when(communityService).unlikeTopic(anyLong(), anyLong());

        // Act & Assert
        mockMvc.perform(delete("/community/like/topic/1")
                        .header("Authorization", "Bearer " + TEST_TOKEN))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("取消点赞成功"));
    }

    @Test
    @DisplayName("测试我的话题 - 成功")
    public void testGetMyTopics_Success() throws Exception {
        // Arrange
        when(communityService.getMyTopics(anyLong(), any(PageQueryDTO.class))).thenReturn(testPageResult);

        // Act & Assert
        mockMvc.perform(get("/community/my/topics")
                        .header("Authorization", "Bearer " + TEST_TOKEN)
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.total").value(1));
    }

    @Test
    @DisplayName("测试板块列表 - 成功")
    public void testGetBoards_Success() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/community/boards"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }
}