package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.QueryHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 查询历史Mapper接口
 * 对应表：user_query_history
 */
@Mapper
public interface QueryHistoryMapper {

    /**
     * 根据ID查询历史记录
     * @param id 主键ID
     * @return 查询历史对象
     */
    QueryHistory selectById(@Param("id") Long id);

    /**
     * 根据用户ID查询所有查询历史
     * @param userId 用户ID
     * @return 查询历史列表
     */
    List<QueryHistory> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和查询类型查询历史
     * @param userId 用户ID
     * @param queryType 查询类型（1=医院，2=医生，3=话题）
     * @return 查询历史列表
     */
    List<QueryHistory> selectByUserIdAndType(@Param("userId") Long userId, @Param("queryType") Integer queryType);

    /**
     * 根据用户ID查询医院查询历史
     * @param userId 用户ID
     * @return 查询历史列表
     */
    List<QueryHistory> selectHospitalHistoryByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询医生查询历史
     * @param userId 用户ID
     * @return 查询历史列表
     */
    List<QueryHistory> selectDoctorHistoryByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID查询话题查询历史
     * @param userId 用户ID
     * @return 查询历史列表
     */
    List<QueryHistory> selectTopicHistoryByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和目标ID查询历史
     * @param userId 用户ID
     * @param queryType 查询类型
     * @param targetId 目标ID
     * @return 查询历史列表
     */
    List<QueryHistory> selectByUserIdAndTarget(@Param("userId") Long userId, @Param("queryType") Integer queryType, @Param("targetId") Long targetId);

    /**
     * 根据用户ID查询最近的查询历史
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 查询历史列表
     */
    List<QueryHistory> selectRecentByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);

    /**
     * 根据时间范围查询历史
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 查询历史列表
     */
    List<QueryHistory> selectByTimeRange(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 根据用户ID和时间范围查询历史
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 查询历史列表
     */
    List<QueryHistory> selectByUserIdAndTimeRange(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime);

    /**
     * 查询所有查询历史
     * @return 查询历史列表
     */
    List<QueryHistory> selectAll();

    /**
     * 插入查询历史
     * @param queryHistory 查询历史对象
     * @return 影响行数
     */
    int insert(QueryHistory queryHistory);

    /**
     * 批量插入查询历史
     * @param queryHistories 查询历史列表
     * @return 影响行数
     */
    int batchInsert(@Param("queryHistories") List<QueryHistory> queryHistories);

    /**
     * 更新查询历史
     * @param queryHistory 查询历史对象
     * @return 影响行数
     */
    int updateById(QueryHistory queryHistory);

    /**
     * 逻辑删除查询历史
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据用户ID删除所有查询历史
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和查询类型删除历史
     * @param userId 用户ID
     * @param queryType 查询类型
     * @return 影响行数
     */
    int deleteByUserIdAndType(@Param("userId") Long userId, @Param("queryType") Integer queryType);

    /**
     * 批量逻辑删除查询历史
     * @param ids ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 根据时间范围删除历史记录
     * @param beforeTime 删除此时间之前的记录
     * @return 影响行数
     */
    int deleteBeforeTime(@Param("beforeTime") LocalDateTime beforeTime);

    /**
     * 统计查询历史总数
     * @return 查询历史总数
     */
    int count();

    /**
     * 统计用户的查询历史数量
     * @param userId 用户ID
     * @return 查询历史数量
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户特定类型的查询历史数量
     * @param userId 用户ID
     * @param queryType 查询类型
     * @return 查询历史数量
     */
    int countByUserIdAndType(@Param("userId") Long userId, @Param("queryType") Integer queryType);

    /**
     * 统计目标的被查询次数
     * @param queryType 查询类型
     * @param targetId 目标ID
     * @return 查询次数
     */
    int countByTarget(@Param("queryType") Integer queryType, @Param("targetId") Long targetId);
}
