package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.Like;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 点赞Mapper接口
 * 对应表：community_like
 */
@Mapper
public interface LikeMapper {

    /**
     * 插入点赞记录
     * @param like 点赞对象
     * @return 影响行数
     */
    int insert(Like like);

    /**
     * 取消点赞（逻辑删除）
     * @param userId 用户ID
     * @param targetType 点赞类型
     * @param targetId 目标ID
     * @return 影响行数
     */
    int cancelLike(@Param("userId") Long userId, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    /**
     * 重新点赞（恢复逻辑删除的记录）
     * @param userId 用户ID
     * @param targetType 点赞类型
     * @param targetId 目标ID
     * @return 影响行数
     */
    int relike(@Param("userId") Long userId, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    /**
     * 根据ID删除点赞记录
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 检查用户是否已点赞
     * @param userId 用户ID
     * @param targetType 点赞类型
     * @param targetId 目标ID
     * @return 点赞数量
     */
    int countByUserAndTarget(@Param("userId") Long userId, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    /**
     * 删除话题的所有点赞记录（级联删除用）
     * @param topicId 话题ID
     * @return 影响行数
     */
    int deleteByTopicId(@Param("topicId") Long topicId);

    /**
     * 删除评论的所有点赞记录（级联删除用）
     * @param commentId 评论ID
     * @return 影响行数
     */
    int deleteByCommentId(@Param("commentId") Long commentId);
}
