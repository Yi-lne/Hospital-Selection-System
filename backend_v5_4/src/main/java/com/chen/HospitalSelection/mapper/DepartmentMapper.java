package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.Department;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 科室Mapper接口
 * 对应表：hospital_department
 */
@Mapper
public interface DepartmentMapper {

    /**
     * 根据ID查询科室
     * @param id 科室ID
     * @return 科室对象
     */
    Department selectById(@Param("id") Long id);

    /**
     * 根据医院ID查询所有科室
     * @param hospitalId 医院ID
     * @return 科室列表
     */
    List<Department> selectByHospitalId(@Param("hospitalId") Long hospitalId);

    /**
     * 根据医院ID查询所有科室（可选择是否包含已删除）
     * @param hospitalId 医院ID
     * @param includeDeleted 是否包含已删除的科室
     * @return 科室列表
     */
    List<Department> selectByHospitalIdIncludingDeleted(
            @Param("hospitalId") Long hospitalId,
            @Param("includeDeleted") Boolean includeDeleted
    );

    /**
     * 根据医院ID和科室名称查询
     * @param hospitalId 医院ID
     * @param deptName 科室名称
     * @return 科室对象
     */
    Department selectByHospitalAndName(@Param("hospitalId") Long hospitalId, @Param("deptName") String deptName);

    /**
     * 模糊搜索科室（按名称）
     * @param deptName 科室名称（模糊）
     * @return 科室列表
     */
    List<Department> searchByName(@Param("deptName") String deptName);

    /**
     * 查询所有科室
     * @return 科室列表
     */
    List<Department> selectAll();

    /**
     * 查询所有有医生的科室（去重）
     * @return 有医生的科室列表（按科室名称去重）
     */
    List<Department> selectDepartmentsWithDoctors();

    /**
     * 插入科室
     * @param department 科室对象
     * @return 影响行数
     */
    int insert(Department department);

    /**
     * 批量插入科室
     * @param departments 科室列表
     * @return 影响行数
     */
    int batchInsert(@Param("departments") List<Department> departments);

    /**
     * 更新科室信息
     * @param department 科室对象
     * @return 影响行数
     */
    int updateById(Department department);

    /**
     * 逻辑删除科室
     * @param id 科室ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据医院ID删除所有科室
     * @param hospitalId 医院ID
     * @return 影响行数
     */
    int deleteByHospitalId(@Param("hospitalId") Long hospitalId);

    /**
     * 批量逻辑删除科室
     * @param ids 科室ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 获取某医院所有医生的所属科室（去重）
     * @param hospitalId 医院ID
     * @return 该医院医生所属的科室列表（按科室名称去重）
     */
    List<Department> selectDepartmentsByHospitalDoctors(@Param("hospitalId") Long hospitalId);

    /**
     * 统计医院的科室数量
     * @param hospitalId 医院ID
     * @return 科室数量
     */
    int countByHospitalId(@Param("hospitalId") Long hospitalId);

    /**
     * 根据医院ID恢复所有科室
     * @param hospitalId 医院ID
     * @return 影响行数
     */
    int restoreByHospitalId(@Param("hospitalId") Long hospitalId);
}
