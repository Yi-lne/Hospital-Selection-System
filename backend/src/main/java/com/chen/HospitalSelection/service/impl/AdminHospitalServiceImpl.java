package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.DepartmentCreateDTO;
import com.chen.HospitalSelection.dto.DepartmentUpdateDTO;
import com.chen.HospitalSelection.dto.DoctorCreateDTO;
import com.chen.HospitalSelection.dto.HospitalCreateDTO;
import com.chen.HospitalSelection.dto.HospitalUpdateDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.exception.ParameterException;
import com.chen.HospitalSelection.mapper.CollectionMapper;
import com.chen.HospitalSelection.mapper.DepartmentMapper;
import com.chen.HospitalSelection.mapper.DoctorMapper;
import com.chen.HospitalSelection.mapper.HospitalMapper;
import com.chen.HospitalSelection.model.Department;
import com.chen.HospitalSelection.model.Doctor;
import com.chen.HospitalSelection.model.Hospital;
import com.chen.HospitalSelection.service.AdminHospitalService;
import com.chen.HospitalSelection.vo.HospitalVO;
import com.chen.HospitalSelection.vo.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员医院管理服务实现类
 *
 * @author chen
 * @since 2025-02-07
 */
@Slf4j
@Service
public class AdminHospitalServiceImpl implements AdminHospitalService {

    @Autowired
    private HospitalMapper hospitalMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private CollectionMapper collectionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HospitalVO createHospital(HospitalCreateDTO dto) {
        log.info("管理员添加医院：{}", dto.getHospitalName());

        // 1. 检查医院名称是否重复
        Hospital existHospital = hospitalMapper.selectByName(dto.getHospitalName());
        if (existHospital != null) {
            throw new ParameterException("医院名称已存在");
        }

        // 2. 创建医院对象
        Hospital hospital = new Hospital();
        BeanUtils.copyProperties(dto, hospital, getNullPropertyNames(dto));
        hospital.setIsDeleted(0);
        if (hospital.getRating() == null) {
            hospital.setRating(BigDecimal.ZERO);
        }
        hospital.setCreateTime(LocalDateTime.now());
        hospital.setUpdateTime(LocalDateTime.now());

        // 3. 保存医院
        hospitalMapper.insert(hospital);

        log.info("医院添加成功，ID：{}", hospital.getId());
        return convertToVO(hospital);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateHospital(Long id, HospitalUpdateDTO dto) {
        log.info("管理员更新医院信息，ID：{}", id);

        // 1. 检查医院是否存在
        Hospital hospital = hospitalMapper.selectById(id);
        if (hospital == null || hospital.getIsDeleted() == 1) {
            throw new BusinessException("医院不存在或已删除");
        }

        // 2. 如果修改医院名称，检查是否重复
        if (dto.getHospitalName() != null && !dto.getHospitalName().equals(hospital.getHospitalName())) {
            Hospital existHospital = hospitalMapper.selectByName(dto.getHospitalName());
            if (existHospital != null && !existHospital.getId().equals(id)) {
                throw new BusinessException("医院名称已存在");
            }
        }

        // 3. 更新字段
        BeanUtils.copyProperties(dto, hospital, getNullPropertyNames(dto));
        hospital.setUpdateTime(LocalDateTime.now());

        hospitalMapper.updateById(hospital);

        log.info("医院信息更新成功，ID：{}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHospital(Long id) {
        log.info("管理员删除医院，ID：{}", id);

        // 1. 检查医院是否存在
        Hospital hospital = hospitalMapper.selectById(id);
        if (hospital == null) {
            throw new BusinessException("医院不存在");
        }

        if (hospital.getIsDeleted() == 1) {
            throw new BusinessException("医院已被删除");
        }

        // 2. 级联逻辑删除该医院下的所有医生和科室
        List<Department> departments = departmentMapper.selectByHospitalId(id);
        int doctorCount = 0;
        for (Department dept : departments) {
            // 删除该科室下所有医生的收藏
            doctorMapper.selectByDeptId(dept.getId()).forEach(doctor -> {
                collectionMapper.deleteByDoctorId(doctor.getId());
            });
            // 删除医生
            int count = doctorMapper.deleteByDeptId(dept.getId());
            doctorCount += count;
            // 逻辑删除科室
            departmentMapper.deleteById(dept.getId());
        }

        // 3. 删除医院的收藏（target_type=1 表示医院）
        collectionMapper.deleteByHospitalId(id);

        // 4. 逻辑删除医院
        hospital.setIsDeleted(1);
        hospital.setUpdateTime(LocalDateTime.now());
        hospitalMapper.updateById(hospital);

        log.info("医院删除成功，ID：{}，已级联删除{}个科室和{}个医生", id, departments.size(), doctorCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void restoreHospital(Long id) {
        log.info("管理员恢复医院，ID：{}", id);

        // 1. 恢复医院
        Hospital hospital = hospitalMapper.selectById(id);
        if (hospital == null) {
            throw new BusinessException("医院不存在");
        }

        if (hospital.getIsDeleted() == 0) {
            throw new BusinessException("医院未被删除，无需恢复");
        }

        hospital.setIsDeleted(0);
        hospital.setUpdateTime(LocalDateTime.now());
        hospitalMapper.updateById(hospital);

        // 2. 恢复所有科室
        departmentMapper.restoreByHospitalId(id);

        // 3. 恢复所有医生
        doctorMapper.restoreByHospitalId(id);

        log.info("医院恢复成功，ID：{}", id);
    }

    @Override
    public PageResult<HospitalVO> getAllHospitals(Boolean includeDeleted, PageQueryDTO dto) {
        log.info("管理员查询所有医院，包含已删除：{}", includeDeleted);

        // 使用PageHelper进行物理分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        List<Hospital> hospitalList = hospitalMapper.selectAllIncludingDeleted(includeDeleted);
        PageInfo<Hospital> pageInfo = new PageInfo<>(hospitalList);

        // 转换为VO
        List<HospitalVO> hospitalVOList = new ArrayList<>();
        for (Hospital hospital : hospitalList) {
            hospitalVOList.add(convertToVO(hospital));
        }

        return new PageResult<>(pageInfo.getTotal(), dto.getPage(), dto.getPageSize(), hospitalVOList);
    }

    @Override
    public List<HospitalVO> searchHospitals(String keyword, Boolean includeDeleted) {
        log.info("管理员搜索医院，关键词：{}，包含已删除：{}", keyword, includeDeleted);

        List<Hospital> hospitalList;
        if (includeDeleted != null && includeDeleted) {
            hospitalList = hospitalMapper.searchByKeywordIncludingDeleted(keyword, includeDeleted);
        } else {
            hospitalList = hospitalMapper.searchByKeyword(keyword);
        }

        List<HospitalVO> hospitalVOList = new ArrayList<>();
        for (Hospital hospital : hospitalList) {
            hospitalVOList.add(convertToVO(hospital));
        }

        return hospitalVOList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDepartment(DepartmentCreateDTO dto) {
        log.info("管理员添加科室，医院ID：{}，科室名称：{}", dto.getHospitalId(), dto.getDeptName());

        // 1. 检查医院是否存在
        Hospital hospital = hospitalMapper.selectById(dto.getHospitalId());
        if (hospital == null || hospital.getIsDeleted() == 1) {
            throw new BusinessException("医院不存在或已删除");
        }

        // 2. 检查科室名称是否重复
        Department existDept = departmentMapper.selectByHospitalAndName(
                dto.getHospitalId(), dto.getDeptName());
        if (existDept != null) {
            throw new BusinessException("该科室已存在");
        }

        // 3. 创建科室
        Department department = new Department();
        BeanUtils.copyProperties(dto, department);
        department.setIsDeleted(0);
        department.setCreateTime(LocalDateTime.now());
        department.setUpdateTime(LocalDateTime.now());

        departmentMapper.insert(department);

        log.info("科室添加成功，ID：{}", department.getId());
        return department.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDepartment(Long deptId, DepartmentUpdateDTO dto) {
        log.info("管理员更新科室信息，ID：{}", deptId);

        // 1. 检查科室是否存在
        Department department = departmentMapper.selectById(deptId);
        if (department == null || department.getIsDeleted() == 1) {
            throw new BusinessException("科室不存在或已删除");
        }

        // 2. 如果修改科室名称，检查是否重复
        if (dto.getDeptName() != null && !dto.getDeptName().equals(department.getDeptName())) {
            Department existDept = departmentMapper.selectByHospitalAndName(
                    department.getHospitalId(), dto.getDeptName());
            if (existDept != null && !existDept.getId().equals(deptId)) {
                throw new BusinessException("该科室名称已存在");
            }
        }

        // 3. 更新字段
        department.setDeptName(dto.getDeptName());
        if (dto.getDeptIntro() != null) {
            department.setDeptIntro(dto.getDeptIntro());
        }
        department.setUpdateTime(LocalDateTime.now());

        departmentMapper.updateById(department);

        log.info("科室信息更新成功，ID：{}", deptId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDepartment(Long deptId) {
        log.info("管理员删除科室，ID：{}", deptId);

        Department department = departmentMapper.selectById(deptId);
        if (department == null) {
            throw new BusinessException("科室不存在");
        }

        // 删除该科室下所有医生的收藏
        doctorMapper.selectByDeptId(deptId).forEach(doctor -> {
            collectionMapper.deleteByDoctorId(doctor.getId());
        });

        // 删除该科室下的所有医生
        int doctorCount = doctorMapper.deleteByDeptId(deptId);

        // 删除科室
        departmentMapper.deleteById(deptId);

        log.info("科室删除成功，ID：{}，已删除{}个医生", deptId, doctorCount);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createDoctor(DoctorCreateDTO dto) {
        log.info("管理员添加医生：{}，医院ID：{}，科室ID：{}",
                dto.getDoctorName(), dto.getHospitalId(), dto.getDeptId());

        // 1. 检查医院是否存在
        Hospital hospital = hospitalMapper.selectById(dto.getHospitalId());
        if (hospital == null || hospital.getIsDeleted() == 1) {
            throw new BusinessException("医院不存在或已删除");
        }

        // 2. 检查科室是否存在
        Department department = departmentMapper.selectById(dto.getDeptId());
        if (department == null || department.getIsDeleted() == 1) {
            throw new BusinessException("科室不存在或已删除");
        }

        // 3. 检查科室是否属于该医院
        if (!department.getHospitalId().equals(dto.getHospitalId())) {
            throw new BusinessException("科室不属于该医院");
        }

        // 4. 创建医生
        Doctor doctor = new Doctor();
        BeanUtils.copyProperties(dto, doctor);
        doctor.setIsDeleted(0);
        if (doctor.getRating() == null) {
            doctor.setRating(BigDecimal.ZERO);
        }
        doctor.setCreateTime(LocalDateTime.now());
        doctor.setUpdateTime(LocalDateTime.now());

        doctorMapper.insert(doctor);

        log.info("医生添加成功，ID：{}", doctor.getId());
        return doctor.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDoctor(Long doctorId) {
        log.info("管理员删除医生，ID：{}", doctorId);

        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }

        // 删除医生的收藏（target_type=2 表示医生）
        collectionMapper.deleteByDoctorId(doctorId);

        // 删除医生
        doctorMapper.deleteById(doctorId);

        log.info("医生删除成功，ID：{}", doctorId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateDoctor(Long doctorId, com.chen.HospitalSelection.dto.DoctorUpdateDTO dto) {
        log.info("管理员更新医生信息，ID：{}", doctorId);

        // 1. 检查医生是否存在
        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            throw new BusinessException("医生不存在");
        }

        // 2. 检查医院是否存在
        Hospital hospital = hospitalMapper.selectById(dto.getHospitalId());
        if (hospital == null || hospital.getIsDeleted() == 1) {
            throw new BusinessException("指定的医院不存在或已删除");
        }

        // 3. 检查科室是否存在
        Department department = departmentMapper.selectById(dto.getDeptId());
        if (department == null || department.getIsDeleted() == 1) {
            throw new BusinessException("指定的科室不存在或已删除");
        }

        // 4. 检查科室是否属于该医院
        if (!department.getHospitalId().equals(dto.getHospitalId())) {
            throw new BusinessException("科室不属于该医院");
        }

        // 5. 更新医生信息
        doctor.setDoctorName(dto.getDoctorName());
        doctor.setHospitalId(dto.getHospitalId());
        doctor.setDeptId(dto.getDeptId());
        doctor.setTitle(dto.getTitle());
        doctor.setSpecialty(dto.getSpecialty());
        doctor.setAcademicBackground(dto.getAcademicBackground());
        doctor.setScheduleTime(dto.getScheduleTime());
        doctor.setConsultationFee(dto.getConsultationFee());
        if (dto.getRating() != null) {
            doctor.setRating(dto.getRating());
        }
        doctor.setUpdateTime(LocalDateTime.now());

        doctorMapper.updateById(doctor);

        log.info("医生信息更新成功，ID：{}", doctorId);
    }

    /**
     * 调用BeanUtils.getNullPropertyNames获取null属性名
     */
    private String[] getNullPropertyNames(Object source) {
        final BeanWrapper sourceWrapper = new BeanWrapperImpl(source);
        java.beans.PropertyDescriptor[] pds = sourceWrapper.getPropertyDescriptors();

        List<String> nullPropertyNames = new ArrayList<>();
        for (java.beans.PropertyDescriptor pd : pds) {
            Object value = sourceWrapper.getPropertyValue(pd.getName());
            if (value == null) {
                nullPropertyNames.add(pd.getName());
            }
        }

        return nullPropertyNames.toArray(new String[0]);
    }

    /**
     * 将Hospital实体转换为HospitalVO
     */
    private HospitalVO convertToVO(Hospital hospital) {
        HospitalVO vo = new HospitalVO();
        BeanUtils.copyProperties(hospital, vo);
        return vo;
    }
}