package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 评论Mapper接口
 * 对应表：community_comment
 */
@Mapper
public interface CommentMapper {

    /**
     * 根据ID查询评论
     * @param id 评论ID
     * @return 评论对象
     */
    Comment selectById(@Param("id") Long id);

    /**
     * 根据话题ID查询所有评论
     * @param topicId 话题ID
     * @return 评论列表
     */
    List<Comment> selectByTopicId(@Param("topicId") Long topicId);

    /**
     * 根据话题ID查询一级评论（parentId=0）
     * @param topicId 话题ID
     * @return 评论列表
     */
    List<Comment> selectTopLevelByTopicId(@Param("topicId") Long topicId);

    /**
     * 根据父评论ID查询回复列表
     * @param parentId 父评论ID
     * @return 评论列表
     */
    List<Comment> selectByParentId(@Param("parentId") Long parentId);

    /**
     * 根据用户ID查询评论列表
     * @param userId 用户ID
     * @return 评论列表
     */
    List<Comment> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询所有评论
     * @return 评论列表
     */
    List<Comment> selectAll();

    /**
     * 插入评论
     * @param comment 评论对象
     * @return 影响行数
     */
    int insert(Comment comment);

    /**
     * 更新评论内容
     * @param comment 评论对象
     * @return 影响行数
     */
    int updateById(Comment comment);

    /**
     * 增加点赞数
     * @param id 评论ID
     * @return 影响行数
     */
    int incrementLikeCount(@Param("id") Long id);

    /**
     * 减少点赞数
     * @param id 评论ID
     * @return 影响行数
     */
    int decrementLikeCount(@Param("id") Long id);

    /**
     * 逻辑删除评论
     * @param id 评论ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据话题ID删除所有评论
     * @param topicId 话题ID
     * @return 影响行数
     */
    int deleteByTopicId(@Param("topicId") Long topicId);

    /**
     * 批量逻辑删除评论
     * @param ids 评论ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 统计评论数量
     * @return 评论总数
     */
    int count();

    /**
     * 统计话题的评论数量
     * @param topicId 话题ID
     * @return 评论数量
     */
    int countByTopicId(@Param("topicId") Long topicId);

    /**
     * 统计用户发表的评论数量
     * @param userId 用户ID
     * @return 评论数量
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 统计回复数量（根据父评论ID）
     * @param parentId 父评论ID
     * @return 回复数量
     */
    int countByParentId(@Param("parentId") Long parentId);
}
