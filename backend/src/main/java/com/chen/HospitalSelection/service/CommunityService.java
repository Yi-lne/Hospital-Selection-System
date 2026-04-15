package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.CommentDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.TopicPublishDTO;
import com.chen.HospitalSelection.dto.TopicUpdateDTO;
import com.chen.HospitalSelection.vo.CommentVO;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.TopicDetailVO;
import com.chen.HospitalSelection.vo.TopicVO;

import java.util.List;

/**
 * 社区服务接口
 * 提供话题发布、评论、点赞等社区功能
 *
 * @author chen
 * @since 2025-01-30
 */
public interface CommunityService {

    /**
     * 分页查询话题列表
     *
     * @param dto 分页查询参数
     * @param sortBy 排序方式（hot=热门，latest=最新）
     * @param keyword 搜索关键词
     * @return 话题分页列表
     */
    PageResult<TopicVO> getTopicList(PageQueryDTO dto, String sortBy, String keyword);

    /**
     * 根据话题板块大类查询话题列表
     *
     * @param boardSub 话题板块子类（可选）
     * @param dto         分页查询参数
     * @param sortBy      排序方式（hot=热门，latest=最新）
     * @param keyword     搜索关键词
     * @return 话题分页列表
     */
    PageResult<TopicVO> getTopicsByBoard(String boardSub, PageQueryDTO dto, String sortBy, String keyword);

    /**
     * 获取话题详情（包含评论列表）
     *
     * @param topicId 话题 ID
     * @param userId  当前用户 ID（可选，用于判断是否点赞/收藏）
     * @return 话题详情
     */
    TopicDetailVO getTopicDetail(Long topicId, Long userId);

    /**
     * 发布话题
     *
     * @param userId 用户 ID
     * @param dto    话题信息
     * @return 发布成功的话题 ID
     */
    Long publishTopic(Long userId, TopicPublishDTO dto);

    /**
     * 修改话题
     *
     * @param userId 用户 ID
     * @param topicId 话题 ID
     * @param dto    修改信息
     * @throws RuntimeException 当用户无权修改时抛出异常
     */
    void updateTopic(Long userId, Long topicId, TopicUpdateDTO dto);

    /**
     * 删除话题
     *
     * @param userId 用户 ID
     * @param topicId 话题 ID
     * @throws RuntimeException 当用户无权删除时抛出异常
     */
    void deleteTopic(Long userId, Long topicId);

    /**
     * 获取话题的评论列表
     *
     * @param topicId 话题 ID
     * @param dto     分页查询参数
     * @return 评论分页列表
     */
    PageResult<CommentVO> getCommentList(Long topicId, PageQueryDTO dto);

    /**
     * 发表评论
     *
     * @param userId 用户 ID
     * @param dto    评论信息
     * @return 评论 ID
     */
    Long addComment(Long userId, CommentDTO dto);

    /**
     * 删除评论
     *
     * @param userId   用户 ID
     * @param commentId 评论 ID
     * @throws RuntimeException 当用户无权删除时抛出异常
     */
    void deleteComment(Long userId, Long commentId);

    /**
     * 点赞话题
     *
     * @param userId 用户 ID
     * @param topicId 话题 ID
     */
    void likeTopic(Long userId, Long topicId);

    /**
     * 取消点赞话题
     *
     * @param userId 用户 ID
     * @param topicId 话题 ID
     */
    void unlikeTopic(Long userId, Long topicId);

    /**
     * 点赞评论
     *
     * @param userId   用户 ID
     * @param commentId 评论 ID
     */
    void likeComment(Long userId, Long commentId);

    /**
     * 取消点赞评论
     *
     * @param userId   用户 ID
     * @param commentId 评论 ID
     */
    void unlikeComment(Long userId, Long commentId);

    /**
     * 获取我的话题列表
     *
     * @param userId 用户 ID
     * @param dto    分页查询参数
     * @return 话题分页列表
     */
    PageResult<TopicVO> getMyTopics(Long userId, PageQueryDTO dto);

    /**
     * 获取我的评论列表
     *
     * @param userId 用户 ID
     * @param dto    分页查询参数
     * @return 评论分页列表
     */
    PageResult<CommentVO> getMyComments(Long userId, PageQueryDTO dto);


    // ==================== 管理员管理话题功能实现 ====================
    /**
     * 管理员删除违规话题
     *
     * @param adminUserId 管理员用户 ID
     * @param topicId     话题 ID
     * @param reason      删除理由（可选）
     */
    void deleteTopicByAdmin(Long adminUserId, Long topicId, String reason);

    /**
     * 管理员删除违规评论
     *
     * @param adminUserId 管理员用户 ID
     * @param commentId   评论 ID
     * @param reason      删除理由（可选）
     */
    void deleteCommentByAdmin(Long adminUserId, Long commentId, String reason);
}
