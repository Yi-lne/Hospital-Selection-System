package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.Topic;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 话题Mapper接口
 * 对应表：community_topic
 */
@Mapper
public interface TopicMapper {

    /**
     * 根据ID查询话题
     * @param id 话题ID
     * @return 话题对象
     */
    Topic selectById(@Param("id") Long id);

    /**
     * 根据用户ID查询话题列表
     * @param userId 用户ID
     * @return 话题列表
     */
    List<Topic> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据疾病编码查询话题列表
     * @param diseaseCode 疾病编码
     * @return 话题列表
     */
    List<Topic> selectByDiseaseCode(@Param("diseaseCode") String diseaseCode);

    /**
     * 根据一级板块查询话题
     * @param boardLevel1 一级板块
     * @return 话题列表
     */
    List<Topic> selectByBoardLevel1(@Param("boardLevel1") String boardLevel1);

    /**
     * 根据二级板块查询话题
     * @param boardLevel2 二级板块
     * @return 话题列表
     */
    List<Topic> selectByBoardLevel2(@Param("boardLevel2") String boardLevel2);

    /**
     * 根据板块类型查询话题
     * @param boardType 板块类型（1=疾病板块，2=医院评价区，3=就医经验区，4=康复护理区）
     * @return 话题列表
     */
    List<Topic> selectByBoardType(@Param("boardType") Integer boardType);

    /**
     * 根据状态查询话题
     * @param status 状态（1=正常，0=禁用，2=审核中）
     * @return 话题列表
     */
    List<Topic> selectByStatus(@Param("status") Integer status);

    /**
     * 模糊搜索话题（按标题、内容）
     * @param keyword 关键词
     * @return 话题列表
     */
    List<Topic> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 查询热门话题（按点赞数、评论数排序）
     * @param limit 限制数量
     * @return 话题列表
     */
    List<Topic> selectHotTopics(@Param("limit") Integer limit);

    /**
     * 查询最新话题
     * @param limit 限制数量
     * @return 话题列表
     */
    List<Topic> selectLatestTopics(@Param("limit") Integer limit);

    /**
     * 根据板块查询热门话题
     * @param boardLevel1 一级板块
     * @param boardLevel2 二级板块
     * @param keyword 搜索关键词
     * @return 话题列表
     */
    List<Topic> selectHotTopicsByBoard(@Param("boardLevel1") String boardLevel1, @Param("boardLevel2") String boardLevel2, @Param("keyword") String keyword);

    /**
     * 根据板块查询最新话题
     * @param boardLevel1 一级板块
     * @param boardLevel2 二级板块
     * @param keyword 搜索关键词
     * @return 话题列表
     */
    List<Topic> selectLatestTopicsByBoard(@Param("boardLevel1") String boardLevel1, @Param("boardLevel2") String boardLevel2, @Param("keyword") String keyword);

    /**
     * 查询所有话题
     * @return 话题列表
     */
    List<Topic> selectAll();

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
     * @param id 话题ID
     * @return 影响行数
     */
    int incrementLikeCount(@Param("id") Long id);

    /**
     * 减少点赞数
     * @param id 话题ID
     * @return 影响行数
     */
    int decrementLikeCount(@Param("id") Long id);

    /**
     * 增加评论数
     * @param id 话题ID
     * @return 影响行数
     */
    int incrementCommentCount(@Param("id") Long id);

    /**
     * 减少评论数
     * @param id 话题ID
     * @return 影响行数
     */
    int decrementCommentCount(@Param("id") Long id);

    /**
     * 增加收藏数
     * @param id 话题ID
     * @return 影响行数
     */
    int incrementCollectCount(@Param("id") Long id);

    /**
     * 减少收藏数
     * @param id 话题ID
     * @return 影响行数
     */
    int decrementCollectCount(@Param("id") Long id);

    /**
     * 增加浏览数
     * @param id 话题ID
     * @return 影响行数
     */
    int incrementViewCount(@Param("id") Long id);

    /**
     * 更新话题状态
     * @param id 话题ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 逻辑删除话题
     * @param id 话题ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量逻辑删除话题
     * @param ids 话题ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 统计话题数量
     * @return 话题总数
     */
    int count();

    /**
     * 统计用户的话题数量
     * @param userId 用户ID
     * @return 话题数量
     */
    int countByUserId(@Param("userId") Long userId);
}
