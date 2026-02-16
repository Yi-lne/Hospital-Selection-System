package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.Doctor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 医生Mapper接口
 * 对应表：doctor_info
 */
@Mapper
public interface DoctorMapper {

    /**
     * 根据ID查询医生
     * @param id 医生ID
     * @return 医生对象
     */
    Doctor selectById(@Param("id") Long id);

    /**
     * 根据ID查询医生详情（包含医院和科室信息）
     * @param id 医生ID
     * @return 医生对象（包含hospitalName和deptName）
     */
    Doctor selectWithDetailsById(@Param("id") Long id);

    /**
     * 根据医院ID查询医生列表
     * @param hospitalId 医院ID
     * @return 医生列表
     */
    List<Doctor> selectByHospitalId(@Param("hospitalId") Long hospitalId);

    /**
     * 根据医院ID查询医生列表（可选择是否包含已删除）
     * @param hospitalId 医院ID
     * @param includeDeleted 是否包含已删除的医生
     * @return 医生列表
     */
    List<Doctor> selectByHospitalIdIncludingDeleted(
            @Param("hospitalId") Long hospitalId,
            @Param("includeDeleted") Boolean includeDeleted
    );

    /**
     * 根据科室ID查询医生列表
     * @param deptId 科室ID
     * @return 医生列表
     */
    List<Doctor> selectByDeptId(@Param("deptId") Long deptId);

    /**
     * 根据医院ID和科室ID查询医生列表
     * @param hospitalId 医院ID
     * @param deptId 科室ID
     * @return 医生列表
     */
    List<Doctor> selectByHospitalAndDept(@Param("hospitalId") Long hospitalId, @Param("deptId") Long deptId);

    /**
     * 根据医院ID和科室名称查询医生
     * @param hospitalId 医院ID
     * @param deptName 科室名称
     * @return 医生列表
     */
    List<Doctor> selectByHospitalAndDeptName(@Param("hospitalId") Long hospitalId, @Param("deptName") String deptName);

    /**
     * 根据职称查询医生
     * @param title 职称
     * @return 医生列表
     */
    List<Doctor> selectByTitle(@Param("title") String title);

    /**
     * 模糊搜索医生（按姓名、专业特长）
     * @param keyword 关键词
     * @return 医生列表
     */
    List<Doctor> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 模糊搜索医生（按姓名、专业特长，可选择是否包含已删除）
     * @param keyword 关键词
     * @param includeDeleted 是否包含已删除的医生
     * @return 医生列表
     */
    List<Doctor> searchByKeyword(@Param("keyword") String keyword, @Param("includeDeleted") Boolean includeDeleted);

    /**
     * 根据动态条件查询医生
     * @param hospitalId 医院ID
     * @param deptId 科室ID
     * @param title 职称
     * @return 医生列表
     */
    List<Doctor> selectByCondition(
            @Param("doctorName") String doctorName,
            @Param("hospitalId") Long hospitalId,
            @Param("deptId") Long deptId,
            @Param("title") String title,
            @Param("specialty") String specialty,
            @Param("minRating") java.math.BigDecimal minRating,
            @Param("maxFee") java.math.BigDecimal maxFee
    );

    /**
     * 根据简单条件查询医生（用于Service层分页）
     * @param hospitalId 医院ID
     * @param deptId 科室ID
     * @param title 职称
     * @return 医生列表
     */
    List<Doctor> selectBySimpleCondition(
            @Param("hospitalId") Long hospitalId,
            @Param("deptId") Long deptId,
            @Param("title") String title
    );

    /**
     * 查询所有医生
     * @return 医生列表
     */
    List<Doctor> selectAll();

    /**
     * 查询所有医生（可选择是否包含已删除）
     * @param includeDeleted 是否包含已删除的医生
     * @return 医生列表
     */
    List<Doctor> selectAllIncludingDeleted(@Param("includeDeleted") Boolean includeDeleted);

    /**
     * 插入医生
     * @param doctor 医生对象
     * @return 影响行数
     */
    int insert(Doctor doctor);

    /**
     * 批量插入医生
     * @param doctors 医生列表
     * @return 影响行数
     */
    int batchInsert(@Param("doctors") List<Doctor> doctors);

    /**
     * 更新医生信息
     * @param doctor 医生对象
     * @return 影响行数
     */
    int updateById(Doctor doctor);

    /**
     * 更新医生评分
     * @param id 医生ID
     * @param rating 评分
     * @return 影响行数
     */
    int updateRating(@Param("id") Long id, @Param("rating") java.math.BigDecimal rating);

    /**
     * 增加评价数量
     * @param id 医生ID
     * @return 影响行数
     */
    int incrementReviewCount(@Param("id") Long id);

    /**
     * 逻辑删除医生
     * @param id 医生ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据医院ID删除所有医生
     * @param hospitalId 医院ID
     * @return 影响行数
     */
    int deleteByHospitalId(@Param("hospitalId") Long hospitalId);

    /**
     * 批量逻辑删除医生
     * @param ids 医生ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 统计医生数量
     * @return 医生总数
     */
    int count();

    /**
     * 统计医院的医生数量
     * @param hospitalId 医院ID
     * @return 医生数量
     */
    int countByHospitalId(@Param("hospitalId") Long hospitalId);

    /**
     * 统计科室的医生数量
     * @param deptId 科室ID
     * @return 医生数量
     */
    int countByDeptId(@Param("deptId") Long deptId);

    /**
     * 根据科室ID删除所有医生
     * @param deptId 科室ID
     * @return 影响行数
     */
    int deleteByDeptId(@Param("deptId") Long deptId);

    /**
     * 根据医院ID恢复所有医生
     * @param hospitalId 医院ID
     * @return 影响行数
     */
    int restoreByHospitalId(@Param("hospitalId") Long hospitalId);
}
