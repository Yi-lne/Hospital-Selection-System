package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.UserCollectionItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 收藏Mapper接口
 * 对应表：user_collection
 */
@Mapper
public interface CollectionMapper {

    /**
     * 根据ID查询收藏记录
     * @param id 主键ID
     * @return 收藏对象
     */
    UserCollectionItem selectById(@Param("id") Long id);

    /**
     * 查询用户是否收藏过目标
     * @param userId 用户ID
     * @param targetType 收藏类型（1=医院，2=医生，3=话题）
     * @param targetId 目标ID
     * @return 收藏对象
     */
    UserCollectionItem selectByUserAndTarget(@Param("userId") Long userId, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    /**
     * 查询用户的所有收藏记录
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<UserCollectionItem> selectByUserId(@Param("userId") Long userId);

    /**
     * 查询用户收藏的医院列表
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<UserCollectionItem> selectHospitalsByUserId(@Param("userId") Long userId);

    /**
     * 查询用户收藏的医生列表
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<UserCollectionItem> selectDoctorsByUserId(@Param("userId") Long userId);

    /**
     * 查询用户收藏的话题列表
     * @param userId 用户ID
     * @return 收藏列表
     */
    List<UserCollectionItem> selectTopicsByUserId(@Param("userId") Long userId);

    /**
     * 查询目标的所有收藏记录
     * @param targetType 收藏类型
     * @param targetId 目标ID
     * @return 收藏列表
     */
    List<UserCollectionItem> selectByTarget(@Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    /**
     * 查询医院的所有收藏记录
     * @param hospitalId 医院ID
     * @return 收藏列表
     */
    List<UserCollectionItem> selectByHospitalId(@Param("hospitalId") Long hospitalId);

    /**
     * 查询医生的所有收藏记录
     * @param doctorId 医生ID
     * @return 收藏列表
     */
    List<UserCollectionItem> selectByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * 查询话题的所有收藏记录
     * @param topicId 话题ID
     * @return 收藏列表
     */
    List<UserCollectionItem> selectByTopicId(@Param("topicId") Long topicId);

    /**
     * 查询所有收藏记录
     * @return 收藏列表
     */
    List<UserCollectionItem> selectAll();

    /**
     * 插入收藏记录
     * @param collection 收藏对象
     * @return 影响行数
     */
    int insert(UserCollectionItem collection);

    /**
     * 取消收藏（逻辑删除）
     * @param userId 用户ID
     * @param targetType 收藏类型
     * @param targetId 目标ID
     * @return 影响行数
     */
    int cancelCollection(@Param("userId") Long userId, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    /**
     * 重新收藏（恢复逻辑删除的记录）
     * @param userId 用户ID
     * @param targetType 收藏类型
     * @param targetId 目标ID
     * @return 影响行数
     */
    int recollect(@Param("userId") Long userId, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    /**
     * 根据ID删除收藏记录
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 批量删除收藏记录
     * @param ids ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 检查用户是否已收藏
     * @param userId 用户ID
     * @param targetType 收藏类型
     * @param targetId 目标ID
     * @return 收藏数量
     */
    int countByUserAndTarget(@Param("userId") Long userId, @Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    /**
     * 统计目标的收藏数量
     * @param targetType 收藏类型
     * @param targetId 目标ID
     * @return 收藏数量
     */
    int countByTarget(@Param("targetType") Integer targetType, @Param("targetId") Long targetId);

    /**
     * 统计医院的收藏数量
     * @param hospitalId 医院ID
     * @return 收藏数量
     */
    int countByHospitalId(@Param("hospitalId") Long hospitalId);

    /**
     * 统计医生的收藏数量
     * @param doctorId 医生ID
     * @return 收藏数量
     */
    int countByDoctorId(@Param("doctorId") Long doctorId);

    /**
     * 统计话题的收藏数量
     * @param topicId 话题ID
     * @return 收藏数量
     */
    int countByTopicId(@Param("topicId") Long topicId);

    /**
     * 统计用户的收藏总数
     * @param userId 用户ID
     * @return 收藏数量
     */
    int countByUserId(@Param("userId") Long userId);
}
