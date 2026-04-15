package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.Topic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 话题 Mapper 接口
 * 对应表：community_topic
 */
@Mapper
public interface TopicMapper {

    /**
     * 根据 ID 查询话题
     * @param id 话题 ID
     * @return 话题对象
     */
    Topic selectById(@Param("id") Long id);

    /**
     * 根据用户 ID 查询话题列表
     * @param userId 用户 ID
     * @return 话题列表
     */
    List<Topic> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据话题板块大类查询话题
     * @param boardType 话题板块大类（1=疾病板块，2=医院评价区，3=就医经验区，4=康复护理区）
     * @return 话题列表
     */
    List<Topic> selectByBoardType(@Param("boardType") Integer boardType);


    /**
     * 根据话题板块子类查询热门话题
     * @param boardSub 话题板块子类
     * @param keyword 搜索关键词
     * @return 话题列表
     */
    List<Topic> selectHotTopicsByBoard(@Param("boardSub") String boardSub, @Param("keyword") String keyword);

    /**
     * 根据话题板块子类查询最新话题
     * @param boardSub 话题板块子类
     * @param keyword 搜索关键词
     * @return 话题列表
     */
    List<Topic> selectLatestTopicsByBoard(@Param("boardSub") String boardSub, @Param("keyword") String keyword);

    /**
     * 插入话题
     * @param topic 话题对象
     * @return 影响行数
     */
    int insert(Topic topic);

    /**
     * 更新话题信息
     * @param topic 话题对象
     * @return 影响行数
     */
    int updateById(Topic topic);

    /**
     * 增加点赞数
     * @param id 话题 ID
     * @return 影响行数
     */
    int incrementLikeCount(@Param("id") Long id);

    /**
     * 减少点赞数
     * @param id 话题 ID
     * @return 影响行数
     */
    int decrementLikeCount(@Param("id") Long id);

    /**
     * 增加评论数
     * @param id 话题 ID
     * @return 影响行数
     */
    int incrementCommentCount(@Param("id") Long id);

    /**
     * 减少评论数
     * @param id 话题 ID
     * @return 影响行数
     */
    int decrementCommentCount(@Param("id") Long id);

    /**
     * 增加收藏数
     * @param id 话题 ID
     * @return 影响行数
     */
    int incrementCollectCount(@Param("id") Long id);

    /**
     * 减少收藏数
     * @param id 话题 ID
     * @return 影响行数
     */
    int decrementCollectCount(@Param("id") Long id);

    /**
     * 增加浏览数
     * @param id 话题 ID
     * @return 影响行数
     */
    int incrementViewCount(@Param("id") Long id);

    /**
     * 逻辑删除话题
     * @param id 话题 ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
