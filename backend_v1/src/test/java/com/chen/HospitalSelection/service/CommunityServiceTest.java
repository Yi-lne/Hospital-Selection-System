package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.CommentDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.TopicPublishDTO;
import com.chen.HospitalSelection.dto.TopicUpdateDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.CommentMapper;
import com.chen.HospitalSelection.mapper.LikeMapper;
import com.chen.HospitalSelection.mapper.TopicMapper;
import com.chen.HospitalSelection.mapper.UserMapper;
import com.chen.HospitalSelection.model.Comment;
import com.chen.HospitalSelection.model.Topic;
import com.chen.HospitalSelection.model.User;
import com.chen.HospitalSelection.vo.CommentVO;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.TopicDetailVO;
import com.chen.HospitalSelection.vo.TopicVO;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * 社区服务测试类
 * 测试话题、评论、点赞等功能
 *
 * @author chen
 */
public class CommunityServiceTest {

    @Mock
    private TopicMapper topicMapper;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private LikeMapper likeMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private com.chen.HospitalSelection.service.impl.CommunityServiceImpl communityService;

    private Topic testTopic;
    private Comment testComment;
    private User testUser;
    private static final Long TEST_TOPIC_ID = 1L;
    private static final Long TEST_COMMENT_ID = 1L;
    private static final Long TEST_USER_ID = 1L;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // 创建测试用户
        testUser = new User();
        testUser.setId(TEST_USER_ID);
        testUser.setNickname("测试用户");
        testUser.setAvatar("http://example.com/avatar.png");

        // 创建测试话题
        testTopic = new Topic();
        testTopic.setId(TEST_TOPIC_ID);
        testTopic.setUserId(TEST_USER_ID);
        testTopic.setDiseaseCode("hypertension");
        testTopic.setBoardLevel1("心血管区");
        testTopic.setBoardLevel2("高血压");
        testTopic.setBoardType(1);
        testTopic.setTitle("高血压患者如何科学饮食？");
        testTopic.setContent("我最近被诊断为高血压，想了解饮食方面的注意事项...");
        testTopic.setLikeCount(56);
        testTopic.setCommentCount(23);
        testTopic.setCollectCount(12);
        testTopic.setViewCount(342);
        testTopic.setStatus(1);
        testTopic.setIsDeleted(0);
        testTopic.setCreateTime(LocalDateTime.now());
        testTopic.setUpdateTime(LocalDateTime.now());

        // 创建测试评论
        testComment = new Comment();
        testComment.setId(TEST_COMMENT_ID);
        testComment.setTopicId(TEST_TOPIC_ID);
        testComment.setUserId(TEST_USER_ID);
        testComment.setParentId(0L);
        testComment.setContent("这条分享很有用");
        testComment.setLikeCount(5);
        testComment.setIsDeleted(0);
        testComment.setCreateTime(LocalDateTime.now());
    }

    @Test
    @DisplayName("测试分页查询话题列表 - 成功")
    public void testGetTopicList_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        Topic topic2 = new Topic();
        topic2.setId(2L);
        topic2.setTitle("糖尿病饮食建议");

        List<Topic> topicList = Arrays.asList(testTopic, topic2);
        when(topicMapper.selectAll()).thenReturn(topicList);

        // Act
        PageResult<TopicVO> result = communityService.getTopicList(dto);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotal());
        assertEquals(1, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(2, result.getList().size());

        verify(topicMapper, times(1)).selectAll();
    }

    @Test
    @DisplayName("测试按板块查询话题 - 成功")
    public void testGetTopicsByBoard_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        List<Topic> topicList = Arrays.asList(testTopic);
        when(topicMapper.selectAll()).thenReturn(topicList);

        // Act
        PageResult<TopicVO> result = communityService.getTopicsByBoard("心血管区", "高血压", dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals("心血管区", result.getList().get(0).getBoardLevel1());
        assertEquals("高血压", result.getList().get(0).getBoardLevel2());
    }

    @Test
    @DisplayName("测试获取话题详情 - 成功")
    public void testGetTopicDetail_Success() {
        // Arrange
        when(topicMapper.selectById(TEST_TOPIC_ID)).thenReturn(testTopic);
        when(userMapper.selectById(TEST_USER_ID)).thenReturn(testUser);
        when(commentMapper.selectByTopicId(TEST_TOPIC_ID)).thenReturn(Arrays.asList(testComment));
        when(likeMapper.countByUserAndTarget(TEST_USER_ID, 1, TEST_TOPIC_ID)).thenReturn(0);

        // Act
        TopicDetailVO result = communityService.getTopicDetail(TEST_TOPIC_ID, TEST_USER_ID);

        // Assert
        assertNotNull(result);
        assertEquals(TEST_TOPIC_ID, result.getId());
        assertEquals("高血压患者如何科学饮食？", result.getTitle());
        assertEquals("测试用户", result.getNickname());
        assertEquals(Integer.valueOf(56), result.getLikeCount());
        assertEquals(Integer.valueOf(23), result.getCommentCount());
        assertFalse(result.getIsLiked());
        assertEquals(1, result.getComments().size());

        verify(topicMapper, times(1)).selectById(TEST_TOPIC_ID);
        verify(userMapper, times(2)).selectById(TEST_USER_ID); // 实际实现可能调用多次
        verify(commentMapper, times(1)).selectByTopicId(TEST_TOPIC_ID);
    }

    @Test
    @DisplayName("测试获取话题详情 - 话题不存在")
    public void testGetTopicDetail_NotFound() {
        // Arrange
        when(topicMapper.selectById(999L)).thenReturn(null);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            communityService.getTopicDetail(999L, TEST_USER_ID);
        });

        assertEquals("话题不存在", exception.getMessage());
    }

    @Test
    @DisplayName("测试发布话题 - 成功")
    public void testPublishTopic_Success() {
        // Arrange
        TopicPublishDTO dto = new TopicPublishDTO();
        dto.setDiseaseCode("hypertension");
        dto.setBoardLevel1("心血管区");
        dto.setBoardLevel2("高血压");
        dto.setBoardType(1);
        dto.setTitle("新话题标题");
        dto.setContent("新话题内容");

        when(topicMapper.insert(any(Topic.class))).thenAnswer(invocation -> {
            Topic topic = invocation.getArgument(0);
            topic.setId(100L);
            return null;
        });

        // Act
        Long result = communityService.publishTopic(TEST_USER_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result);

        verify(topicMapper, times(1)).insert(any(Topic.class));
    }

    @Test
    @DisplayName("测试修改话题 - 成功")
    public void testUpdateTopic_Success() {
        // Arrange
        TopicUpdateDTO dto = new TopicUpdateDTO();
        dto.setTitle("修改后的标题");
        dto.setContent("修改后的内容");

        when(topicMapper.selectById(TEST_TOPIC_ID)).thenReturn(testTopic);
        when(topicMapper.updateById(any(Topic.class))).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> communityService.updateTopic(TEST_USER_ID, TEST_TOPIC_ID, dto));

        // Assert
        verify(topicMapper, times(1)).updateById(any(Topic.class));
    }

    @Test
    @DisplayName("测试修改话题 - 无权修改")
    public void testUpdateTopic_NoPermission() {
        // Arrange
        TopicUpdateDTO dto = new TopicUpdateDTO();
        dto.setTitle("修改后的标题");

        when(topicMapper.selectById(TEST_TOPIC_ID)).thenReturn(testTopic);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            communityService.updateTopic(999L, TEST_TOPIC_ID, dto);
        });

        assertEquals("无权修改此话题", exception.getMessage());
        verify(topicMapper, never()).updateById(any(Topic.class));
    }

    @Test
    @DisplayName("测试删除话题 - 成功")
    public void testDeleteTopic_Success() {
        // Arrange
        when(topicMapper.selectById(TEST_TOPIC_ID)).thenReturn(testTopic);
        when(topicMapper.deleteById(TEST_TOPIC_ID)).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> communityService.deleteTopic(TEST_USER_ID, TEST_TOPIC_ID));

        // Assert
        verify(topicMapper, times(1)).deleteById(TEST_TOPIC_ID);
    }

    @Test
    @DisplayName("测试删除话题 - 无权删除")
    public void testDeleteTopic_NoPermission() {
        // Arrange
        when(topicMapper.selectById(TEST_TOPIC_ID)).thenReturn(testTopic);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            communityService.deleteTopic(999L, TEST_TOPIC_ID);
        });

        assertEquals("无权删除此话题", exception.getMessage());
        verify(topicMapper, never()).deleteById(anyLong());
    }

    @Test
    @DisplayName("测试发表评论 - 成功")
    public void testAddComment_Success() {
        // Arrange
        CommentDTO dto = new CommentDTO();
        dto.setTopicId(TEST_TOPIC_ID);
        dto.setContent("这是一条评论");
        dto.setParentId(0L);

        when(commentMapper.insert(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            comment.setId(100L);
            return null;
        });
        when(topicMapper.incrementCommentCount(TEST_TOPIC_ID)).thenReturn(1);

        // Act
        Long result = communityService.addComment(TEST_USER_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals(100L, result);

        verify(commentMapper, times(1)).insert(any(Comment.class));
        verify(topicMapper, times(1)).incrementCommentCount(TEST_TOPIC_ID);
    }

    @Test
    @DisplayName("测试点赞话题 - 成功")
    public void testLikeTopic_Success() {
        // Arrange
        when(likeMapper.countByUserAndTarget(TEST_USER_ID, 1, TEST_TOPIC_ID)).thenReturn(0);
        when(likeMapper.insert(any(com.chen.HospitalSelection.model.Like.class))).thenReturn(1);
        when(topicMapper.incrementLikeCount(TEST_TOPIC_ID)).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> communityService.likeTopic(TEST_USER_ID, TEST_TOPIC_ID));

        // Assert
        verify(likeMapper, times(1)).insert(any(com.chen.HospitalSelection.model.Like.class));
        verify(topicMapper, times(1)).incrementLikeCount(TEST_TOPIC_ID);
    }

    @Test
    @DisplayName("测试点赞话题 - 已点赞")
    public void testLikeTopic_AlreadyLiked() {
        // Arrange
        when(likeMapper.countByUserAndTarget(TEST_USER_ID, 1, TEST_TOPIC_ID)).thenReturn(1);

        // Act & Assert
        BusinessException exception = assertThrows(BusinessException.class, () -> {
            communityService.likeTopic(TEST_USER_ID, TEST_TOPIC_ID);
        });

        assertEquals("已经点赞过了", exception.getMessage());
        verify(likeMapper, never()).insert(any(com.chen.HospitalSelection.model.Like.class));
    }

    @Test
    @DisplayName("测试取消点赞话题 - 成功")
    public void testUnlikeTopic_Success() {
        // Arrange
        when(likeMapper.cancelLike(TEST_USER_ID, 1, TEST_TOPIC_ID)).thenReturn(1);
        when(topicMapper.decrementLikeCount(TEST_TOPIC_ID)).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> communityService.unlikeTopic(TEST_USER_ID, TEST_TOPIC_ID));

        // Assert
        verify(likeMapper, times(1)).cancelLike(TEST_USER_ID, 1, TEST_TOPIC_ID);
        verify(topicMapper, times(1)).decrementLikeCount(TEST_TOPIC_ID);
    }

    @Test
    @DisplayName("测试点赞评论 - 成功")
    public void testLikeComment_Success() {
        // Arrange
        when(likeMapper.countByUserAndTarget(TEST_USER_ID, 2, TEST_COMMENT_ID)).thenReturn(0);
        when(likeMapper.insert(any(com.chen.HospitalSelection.model.Like.class))).thenReturn(1);
        when(commentMapper.incrementLikeCount(TEST_COMMENT_ID)).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> communityService.likeComment(TEST_USER_ID, TEST_COMMENT_ID));

        // Assert
        verify(likeMapper, times(1)).insert(any(com.chen.HospitalSelection.model.Like.class));
        verify(commentMapper, times(1)).incrementLikeCount(TEST_COMMENT_ID);
    }

    @Test
    @DisplayName("测试取消点赞评论 - 成功")
    public void testUnlikeComment_Success() {
        // Arrange
        when(likeMapper.cancelLike(TEST_USER_ID, 2, TEST_COMMENT_ID)).thenReturn(1);
        when(commentMapper.decrementLikeCount(TEST_COMMENT_ID)).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> communityService.unlikeComment(TEST_USER_ID, TEST_COMMENT_ID));

        // Assert
        verify(likeMapper, times(1)).cancelLike(TEST_USER_ID, 2, TEST_COMMENT_ID);
        verify(commentMapper, times(1)).decrementLikeCount(TEST_COMMENT_ID);
    }

    @Test
    @DisplayName("测试查询我的话题 - 成功")
    public void testGetMyTopics_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        List<Topic> topicList = Arrays.asList(testTopic);
        when(topicMapper.selectByUserId(TEST_USER_ID)).thenReturn(topicList);

        // Act
        PageResult<TopicVO> result = communityService.getMyTopics(TEST_USER_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(TEST_TOPIC_ID, result.getList().get(0).getId());

        verify(topicMapper, times(1)).selectByUserId(TEST_USER_ID);
    }

    @Test
    @DisplayName("测试查询我的评论 - 成功")
    public void testGetMyComments_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        List<Comment> commentList = Arrays.asList(testComment);
        when(commentMapper.selectByUserId(TEST_USER_ID)).thenReturn(commentList);
        when(userMapper.selectById(TEST_USER_ID)).thenReturn(testUser);

        // Act
        PageResult<CommentVO> result = communityService.getMyComments(TEST_USER_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getTotal());
        assertEquals(TEST_COMMENT_ID, result.getList().get(0).getId());

        verify(commentMapper, times(1)).selectByUserId(TEST_USER_ID);
    }

    @Test
    @DisplayName("测试查询评论列表 - 成功")
    public void testGetCommentList_Success() {
        // Arrange
        PageQueryDTO dto = new PageQueryDTO();
        dto.setPage(1);
        dto.setPageSize(10);

        Comment comment2 = new Comment();
        comment2.setId(2L);
        comment2.setTopicId(TEST_TOPIC_ID);

        List<Comment> commentList = Arrays.asList(testComment, comment2);
        when(commentMapper.selectByTopicId(TEST_TOPIC_ID)).thenReturn(commentList);
        when(userMapper.selectById(TEST_USER_ID)).thenReturn(testUser);

        // Act
        PageResult<CommentVO> result = communityService.getCommentList(TEST_TOPIC_ID, dto);

        // Assert
        assertNotNull(result);
        assertEquals(2L, result.getTotal());
        assertEquals(2, result.getList().size());

        verify(commentMapper, times(1)).selectByTopicId(TEST_TOPIC_ID);
    }

    @Test
    @DisplayName("测试增加浏览量 - 成功")
    public void testIncrementViewCount_Success() {
        // Arrange
        when(topicMapper.incrementViewCount(TEST_TOPIC_ID)).thenReturn(1);

        // Act
        assertDoesNotThrow(() -> communityService.incrementViewCount(TEST_TOPIC_ID));

        // Assert
        verify(topicMapper, times(1)).incrementViewCount(TEST_TOPIC_ID);
    }
}
