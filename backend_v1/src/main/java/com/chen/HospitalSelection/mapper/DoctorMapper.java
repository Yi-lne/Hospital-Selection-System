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
     * 根据医院ID查询医生列表
     * @param hospitalId 医院ID
     * @return 医生列表
     */
    List<Doctor> selectByHospitalId(@Param("hospitalId") Long hospitalId);

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
     * 查询所有医生
     * @return 医生列表
     */
    List<Doctor> selectAll();

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
}
