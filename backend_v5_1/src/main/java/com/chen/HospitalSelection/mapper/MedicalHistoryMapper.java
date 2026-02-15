package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.MedicalHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.time.LocalDate;
import java.util.List;

/**
 * 病史Mapper接口
 * 对应表：user_medical_history
 */
@Mapper
public interface MedicalHistoryMapper {

    /**
     * 根据ID查询病史
     * @param id 主键ID
     * @return 病史对象
     */
    MedicalHistory selectById(@Param("id") Long id);

    /**
     * 根据用户ID查询所有病史
     * @param userId 用户ID
     * @return 病史列表
     */
    List<MedicalHistory> selectByUserId(@Param("userId") Long userId);

    /**
     * 根据用户ID和状态查询病史
     * @param userId 用户ID
     * @param status 状态（1=治疗中，2=已康复）
     * @return 病史列表
     */
    List<MedicalHistory> selectByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 根据用户ID查询治疗中的病史
     * @param userId 用户ID
     * @return 病史列表
     */
    List<MedicalHistory> selectByUserIdInProgress(@Param("userId") Long userId);

    /**
     * 根据用户ID查询已康复的病史
     * @param userId 用户ID
     * @return 病史列表
     */
    List<MedicalHistory> selectByUserIdRecovered(@Param("userId") Long userId);

    /**
     * 根据疾病名称查询病史
     * @param diseaseName 疾病名称
     * @return 病史列表
     */
    List<MedicalHistory> selectByDiseaseName(@Param("diseaseName") String diseaseName);

    /**
     * 根据用户ID和疾病名称查询病史
     * @param userId 用户ID
     * @param diseaseName 疾病名称
     * @return 病史列表
     */
    List<MedicalHistory> selectByUserIdAndDisease(@Param("userId") Long userId, @Param("diseaseName") String diseaseName);

    /**
     * 根据诊断日期范围查询病史
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 病史列表
     */
    List<MedicalHistory> selectByDateRange(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 根据用户ID和诊断日期范围查询病史
     * @param userId 用户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 病史列表
     */
    List<MedicalHistory> selectByUserIdAndDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 模糊搜索病史（按疾病名称）
     * @param keyword 关键词
     * @return 病史列表
     */
    List<MedicalHistory> searchByDiseaseName(@Param("keyword") String keyword);

    /**
     * 查询所有病史
     * @return 病史列表
     */
    List<MedicalHistory> selectAll();

    /**
     * 插入病史
     * @param medicalHistory 病史对象
     * @return 影响行数
     */
    int insert(MedicalHistory medicalHistory);

    /**
     * 批量插入病史
     * @param medicalHistories 病史列表
     * @return 影响行数
     */
    int batchInsert(@Param("medicalHistories") List<MedicalHistory> medicalHistories);

    /**
     * 更新病史信息
     * @param medicalHistory 病史对象
     * @return 影响行数
     */
    int updateById(MedicalHistory medicalHistory);

    /**
     * 更新病史状态
     * @param id 病史ID
     * @param status 状态
     * @return 影响行数
     */
    int updateStatus(@Param("id") Long id, @Param("status") Integer status);

    /**
     * 逻辑删除病史
     * @param id 病史ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据用户ID删除所有病史
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteByUserId(@Param("userId") Long userId);

    /**
     * 批量逻辑删除病史
     * @param ids 病史ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 统计病史总数
     * @return 病史总数
     */
    int count();

    /**
     * 统计用户的病史数量
     * @param userId 用户ID
     * @return 病史数量
     */
    int countByUserId(@Param("userId") Long userId);

    /**
     * 统计用户的特定状态病史数量
     * @param userId 用户ID
     * @param status 状态
     * @return 病史数量
     */
    int countByUserIdAndStatus(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 检查用户是否患有某种疾病
     * @param userId 用户ID
     * @param diseaseName 疾病名称
     * @return 病史数量
     */
    int countByUserIdAndDisease(@Param("userId") Long userId, @Param("diseaseName") String diseaseName);
}
