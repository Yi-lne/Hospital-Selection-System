package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.dto.DepartmentCreateDTO;
import com.chen.HospitalSelection.dto.DepartmentUpdateDTO;
import com.chen.HospitalSelection.dto.DoctorCreateDTO;
import com.chen.HospitalSelection.dto.DoctorUpdateDTO;
import com.chen.HospitalSelection.dto.HospitalCreateDTO;
import com.chen.HospitalSelection.dto.HospitalUpdateDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.vo.HospitalVO;
import com.chen.HospitalSelection.vo.PageResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 管理员医院管理服务接口
 *
 * @author chen
 * @since 2025-02-07
 */
public interface AdminHospitalService {

    /**
     * 创建医院
     *
     * @param dto 医院信息
     * @return 创建的医院信息
     */
    HospitalVO createHospital(HospitalCreateDTO dto);

    /**
     * 更新医院信息
     *
     * @param id  医院ID
     * @param dto 更新的医院信息
     */
    void updateHospital(Long id, HospitalUpdateDTO dto);

    /**
     * 删除医院（逻辑删除，级联删除科室和医生）
     *
     * @param id 医院ID
     */
    void deleteHospital(Long id);

    /**
     * 恢复已删除的医院
     *
     * @param id 医院ID
     */
    void restoreHospital(Long id);

    /**
     * 获取所有医院（包括已删除）
     *
     * @param includeDeleted 是否包含已删除的医院
     * @param dto 分页参数
     * @return 医院分页列表
     */
    PageResult<HospitalVO> getAllHospitals(Boolean includeDeleted, PageQueryDTO dto);

    /**
     * 根据名称搜索医院
     *
     * @param keyword 医院名称关键词
     * @return 医院列表
     */
    List<HospitalVO> searchHospitals(String keyword);

    /**
     * 根据名称搜索医院（可选择是否包含已删除）
     *
     * @param keyword 医院名称关键词
     * @param includeDeleted 是否包含已删除的医院
     * @return 医院列表
     */
    List<HospitalVO> searchHospitals(String keyword, Boolean includeDeleted);

    /**
     * 添加科室
     *
     * @param dto 科室信息
     * @return 科室ID
     */
    Long createDepartment(DepartmentCreateDTO dto);

    /**
     * 批量添加科室
     *
     * @param hospitalId 医院ID
     * @param deptNames 科室名称列表
     * @return 添加成功的科室数量
     */
    int batchCreateDepartments(Long hospitalId, List<String> deptNames);

    /**
     * 更新科室信息
     *
     * @param deptId 科室ID
     * @param dto 更新的科室信息
     */
    void updateDepartment(Long deptId, DepartmentUpdateDTO dto);

    /**
     * 删除科室
     *
     * @param deptId 科室ID
     */
    void deleteDepartment(Long deptId);

    /**
     * 创建医生
     *
     * @param dto 医生信息
     * @return 医生ID
     */
    Long createDoctor(DoctorCreateDTO dto);

    /**
     * 批量导入医生
     *
     * @param file Excel文件
     * @return 导入结果统计
     */
    Map<String, Object> importDoctors(MultipartFile file);

    /**
     * 删除医生
     *
     * @param doctorId 医生ID
     */
    void deleteDoctor(Long doctorId);

    /**
     * 更新医生
     *
     * @param doctorId 医生ID
     * @param dto 更新的医生信息
     */
    void updateDoctor(Long doctorId, DoctorUpdateDTO dto);
}