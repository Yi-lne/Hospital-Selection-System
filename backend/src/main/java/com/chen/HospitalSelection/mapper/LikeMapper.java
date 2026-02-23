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
     * 根据ID查询点赞记录
     * @param id 主键ID
     * @return 点赞对象
     */
    Like selectById(@Param("id") Long id);

    /**
     * 查询用户是否点赞过目标
     * @param userId 用户ID
     * @param targetType 点赞类型（1=话题，2=评论）
     * @param targetId 目标ID
     * @return 点赞对象
     */
    Like selectByUserAndTarget(@Param("userId") Long userId, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    /**
     * 查询用户的所有点赞记录
     * @param userId 用户ID
     * @return 点赞列表
     */
    List<Like> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询用户对话题的点赞记录
     * @param userId 用户ID
     * @return 点赞列表
     */
    List<Like> selectTopicLikesByUserId(@Param("userId") Long userId);

    /**
     * 查询用户对评论的点赞记录
     * @param userId 用户ID
     * @return 点赞列表
     */
    List<Like> selectCommentLikesByUserId(@Param("userId") Long userId);

    /**
     * 查询目标的所有点赞记录
     * @param targetType 点赞类型
     * @param targetId 目标ID
     * @return 点赞列表
     */
    List<Like> selectByTarget(@Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    /**
     * 查询话题的所有点赞记录
     * @param topicId 话题ID
     * @return 点赞列表
     */
    List<Like> selectByTopicId(@Param("topicId") Long topicId);

    /**
     * 查询评论的所有点赞记录
     * @param commentId 评论ID
     * @return 点赞列表
     */
    List<Like> selectByCommentId(@Param("commentId") Long commentId);

    /**
     * 查询所有点赞记录
     * @return 点赞列表
     */
    List<Like> selectAll();

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
     * 批量删除点赞记录
     * @param ids ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 检查用户是否已点赞
     * @param userId 用户ID
     * @param targetType 点赞类型
     * @param targetId 目标ID
     * @return 点赞数量
     */
    int countByUserAndTarget(@Param("userId") Long userId, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    /**
     * 统计目标的点赞数量
     * @param targetType 点赞类型
     * @param targetId 目标ID
     * @return 点赞数量
     */
    int countByTarget(@Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    /**
     * 统计话题的点赞数量
     * @param topicId 话题ID
     * @return 点赞数量
     */
    int countByTopicId(@Param("topicId") Long topicId);

    /**
     * 统计评论的点赞数量
     * @param commentId 评论ID
     * @return 点赞数量
     */
    int countByCommentId(@Param("commentId") Long commentId);

    /**
     * 统计用户的点赞总数
     * @param userId 用户ID
     * @return 点赞数量
     */
    int countByUserId(@Param("userId") Long userId);
}
