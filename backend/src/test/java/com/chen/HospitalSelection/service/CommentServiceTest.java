package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.CommentDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.mapper.CommentMapper;
import com.chen.HospitalSelection.mapper.LikeMapper;
import com.chen.HospitalSelection.mapper.TopicMapper;
import com.chen.HospitalSelection.mapper.UserMapper;
import com.chen.HospitalSelection.model.Comment;
import com.chen.HospitalSelection.model.Topic;
import com.chen.HospitalSelection.model.User;
import com.chen.HospitalSelection.vo.CommentVO;
import com.chen.HospitalSelection.vo.PageResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 评论功能集成测试
 * 测试评论的完整业务流程
 *
 * @author chen
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CommentServiceTest {

    @Autowired
    private CommunityService communityService;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private TopicMapper topicMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private LikeMapper likeMapper;

    /**
     * 测试用户ID
     */
    private static final Long TEST_USER_ID = 2L;

    /**
     * 测试话题ID
     */
    private static final Long TEST_TOPIC_ID = 1L;

    private Topic testTopic;
    private User testUser;

    @BeforeEach
    public void setUp() {
        // 初始化测试数据
        testTopic = topicMapper.selectById(TEST_TOPIC_ID);
        if (testTopic == null) {
            // 创建测试话题
            testTopic = new Topic();
            testTopic.setUserId(TEST_USER_ID);
            testTopic.setDiseaseCode("hypertension");
            testTopic.setBoardLevel1("心血管区");
            testTopic.setBoardLevel2("高血压");
            testTopic.setBoardType(1);
            testTopic.setTitle("高血压患者如何科学饮食？");
            testTopic.setContent("我最近被诊断为高血压，想请教大家如何科学饮食？");
            testTopic.setLikeCount(0);
            testTopic.setCommentCount(0);
            testTopic.setCollectCount(0);
            testTopic.setViewCount(0);
            testTopic.setStatus(1);
            testTopic.setIsDeleted(0);
            testTopic.setCreateTime(LocalDateTime.now());
            testTopic.setUpdateTime(LocalDateTime.now());
            topicMapper.insert(testTopic);
        }

        testUser = userMapper.selectById(TEST_USER_ID);
    }

    @Test
    @DisplayName("测试发表一级评论")
    public void testAddTopLevelComment() {
        // Arrange
        CommentDTO dto = new CommentDTO();
        dto.setTopicId(testTopic.getId());
        dto.setContent("这是一条测试评论");
        dto.setParentId(0L);

        // Act
        Long commentId = communityService.addComment(TEST_USER_ID, dto);

        // Assert
        assertNotNull(commentId, "评论ID不应为空");

        Comment comment = commentMapper.selectById(commentId);
        assertNotNull(comment, "评论应该存在");
        assertEquals(dto.getContent(), comment.getContent(), "评论内容应该匹配");
        assertEquals(dto.getParentId(), comment.getParentId(), "父评论ID应该是0");
        assertEquals(TEST_USER_ID, comment.getUserId(), "用户ID应该匹配");
        assertEquals(testTopic.getId(), comment.getTopicId(), "话题ID应该匹配");
    }

    @Test
    @DisplayName("测试回复评论")
    public void testReplyToComment() {
        // Arrange - 先创建一级评论
        CommentDTO parentDto = new CommentDTO();
        parentDto.setTopicId(testTopic.getId());
        parentDto.setContent("这是一条父评论");
        parentDto.setParentId(0L);
        Long parentCommentId = communityService.addComment(TEST_USER_ID, parentDto);

        // Act - 回复该评论
        CommentDTO replyDto = new CommentDTO();
        replyDto.setTopicId(testTopic.getId());
        replyDto.setContent("这是一条回复");
        replyDto.setParentId(parentCommentId);
        Long replyCommentId = communityService.addComment(TEST_USER_ID, replyDto);

        // Assert
        Comment reply = commentMapper.selectById(replyCommentId);
        assertNotNull(reply, "回复评论应该存在");
        assertEquals(parentCommentId, reply.getParentId(), "父评论ID应该匹配");
        assertEquals(replyDto.getContent(), reply.getContent(), "回复内容应该匹配");

        // 验证回复数量
        int replyCount = commentMapper.countByParentId(parentCommentId);
        assertTrue(replyCount > 0, "应该有一条回复");
    }

    @Test
    @DisplayName("测试获取话题的评论列表")
    public void testGetCommentList() {
        // Arrange - 创建测试评论
        CommentDTO dto = new CommentDTO();
        dto.setTopicId(testTopic.getId());
        dto.setContent("测试评论列表");
        dto.setParentId(0L);
        communityService.addComment(TEST_USER_ID, dto);

        PageQueryDTO pageDto = new PageQueryDTO();
        pageDto.setPage(1);
        pageDto.setPageSize(10);

        // Act
        PageResult<CommentVO> result = communityService.getCommentList(testTopic.getId(), pageDto);

        // Assert
        assertNotNull(result, "评论列表不应为空");
        assertTrue(result.getTotal() >= 1, "应该至少有一条评论");
        assertNotNull(result.getList(), "评论列表不应为null");

        if (!result.getList().isEmpty()) {
            CommentVO firstComment = result.getList().get(0);
            assertNotNull(firstComment.getNickname(), "评论者昵称应该被加载");
            assertNotNull(firstComment.getContent(), "评论内容应该存在");
        }
    }

    @Test
    @DisplayName("测试删除评论")
    public void testDeleteComment() {
        // Arrange - 创建测试评论
        CommentDTO dto = new CommentDTO();
        dto.setTopicId(testTopic.getId());
        dto.setContent("即将被删除的评论");
        dto.setParentId(0L);
        Long commentId = communityService.addComment(TEST_USER_ID, dto);

        // Act
        communityService.deleteComment(TEST_USER_ID, commentId);

        // Assert
        Comment deletedComment = commentMapper.selectById(commentId);
        assertNull(deletedComment, "删除后评论不应该存在（或被标记为已删除）");
    }

    @Test
    @DisplayName("测试删除他人评论应该失败")
    public void testDeleteOtherUserComment_ShouldFail() {
        // Arrange - 用户A创建评论
        CommentDTO dto = new CommentDTO();
        dto.setTopicId(testTopic.getId());
        dto.setContent("这是用户A的评论");
        dto.setParentId(0L);
        Long commentId = communityService.addComment(TEST_USER_ID, dto);

        // 用户B尝试删除
        Long otherUserId = 999L;

        // Act & Assert
        assertThrows(Exception.class, () -> {
            communityService.deleteComment(otherUserId, commentId);
        }, "删除他人评论应该抛出异常");
    }

    @Test
    @DisplayName("测试点赞评论")
    public void testLikeComment() {
        // Arrange - 创建测试评论
        CommentDTO dto = new CommentDTO();
        dto.setTopicId(testTopic.getId());
        dto.setContent("测试点赞的评论");
        dto.setParentId(0L);
        Long commentId = communityService.addComment(TEST_USER_ID, dto);

        // Act
        communityService.likeComment(TEST_USER_ID, commentId);

        // Assert
        Comment comment = commentMapper.selectById(commentId);
        assertTrue(comment.getLikeCount() > 0, "点赞数应该增加");

        int likeCount = likeMapper.countByUserAndTarget(TEST_USER_ID, 2, commentId);
        assertEquals(1, likeCount, "应该有一条点赞记录");
    }

    @Test
    @DisplayName("测试取消点赞评论")
    public void testUnlikeComment() {
        // Arrange - 先点赞
        CommentDTO dto = new CommentDTO();
        dto.setTopicId(testTopic.getId());
        dto.setContent("测试取消点赞的评论");
        dto.setParentId(0L);
        Long commentId = communityService.addComment(TEST_USER_ID, dto);
        communityService.likeComment(TEST_USER_ID, commentId);

        // Act
        communityService.unlikeComment(TEST_USER_ID, commentId);

        // Assert
        int likeCount = likeMapper.countByUserAndTarget(TEST_USER_ID, 2, commentId);
        assertEquals(0, likeCount, "点赞记录应该被删除");
    }

    @Test
    @DisplayName("测试重复点赞评论应该失败")
    public void testLikeCommentTwice_ShouldFail() {
        // Arrange - 创建测试评论
        CommentDTO dto = new CommentDTO();
        dto.setTopicId(testTopic.getId());
        dto.setContent("测试重复点赞");
        dto.setParentId(0L);
        Long commentId = communityService.addComment(TEST_USER_ID, dto);

        // Act - 第一次点赞
        communityService.likeComment(TEST_USER_ID, commentId);

        // Assert - 第二次点赞应该失败
        assertThrows(Exception.class, () -> {
            communityService.likeComment(TEST_USER_ID, commentId);
        }, "重复点赞应该抛出异常");
    }

    @Test
    @DisplayName("测试获取我的评论列表")
    public void testGetMyComments() {
        // Arrange - 创建测试评论
        CommentDTO dto = new CommentDTO();
        dto.setTopicId(testTopic.getId());
        dto.setContent("我的评论");
        dto.setParentId(0L);
        communityService.addComment(TEST_USER_ID, dto);

        PageQueryDTO pageDto = new PageQueryDTO();
        pageDto.setPage(1);
        pageDto.setPageSize(10);

        // Act
        PageResult<CommentVO> result = communityService.getMyComments(TEST_USER_ID, pageDto);

        // Assert
        assertNotNull(result, "我的评论列表不应为空");
        assertTrue(result.getTotal() >= 1, "应该至少有一条评论");
    }

    @Test
    @DisplayName("测试评论数统计")
    public void testCommentCount() {
        // Arrange - 创建多条评论
        int initialCount = commentMapper.countByTopicId(testTopic.getId());

        CommentDTO dto1 = new CommentDTO();
        dto1.setTopicId(testTopic.getId());
        dto1.setContent("评论1");
        dto1.setParentId(0L);
        communityService.addComment(TEST_USER_ID, dto1);

        CommentDTO dto2 = new CommentDTO();
        dto2.setTopicId(testTopic.getId());
        dto2.setContent("评论2");
        dto2.setParentId(0L);
        communityService.addComment(TEST_USER_ID, dto2);

        // Act
        int finalCount = commentMapper.countByTopicId(testTopic.getId());

        // Assert
        assertEquals(initialCount + 2, finalCount, "评论数应该增加2");

        // 验证话题的评论计数
        Topic updatedTopic = topicMapper.selectById(testTopic.getId());
        assertTrue(updatedTopic.getCommentCount() >= 2, "话题的评论计数应该正确");
    }

    @Test
    @DisplayName("测试评论嵌套结构")
    public void testCommentNestingStructure() {
        // Arrange - 创建一级评论
        CommentDTO parentDto = new CommentDTO();
        parentDto.setTopicId(testTopic.getId());
        parentDto.setContent("一级评论");
        parentDto.setParentId(0L);
        Long parentId = communityService.addComment(TEST_USER_ID, parentDto);

        // 创建多条回复
        CommentDTO reply1 = new CommentDTO();
        reply1.setTopicId(testTopic.getId());
        reply1.setContent("回复1");
        reply1.setParentId(parentId);
        communityService.addComment(TEST_USER_ID, reply1);

        CommentDTO reply2 = new CommentDTO();
        reply2.setTopicId(testTopic.getId());
        reply2.setContent("回复2");
        reply2.setParentId(parentId);
        communityService.addComment(TEST_USER_ID, reply2);

        // Act - 查询回复列表
        List<Comment> replies = commentMapper.selectByParentId(parentId);

        // Assert
        assertNotNull(replies, "回复列表不应为空");
        assertTrue(replies.size() >= 2, "应该至少有2条回复");

        // 验证所有回复的parentId都正确
        for (Comment reply : replies) {
            assertEquals(parentId, reply.getParentId(), "回复的父评论ID应该匹配");
        }
    }
}
