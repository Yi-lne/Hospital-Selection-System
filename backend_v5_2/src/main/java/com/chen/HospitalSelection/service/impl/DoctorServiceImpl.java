package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.DoctorFilterDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.exception.ResourceNotFoundException;
import com.chen.HospitalSelection.mapper.DoctorMapper;
import com.chen.HospitalSelection.model.Doctor;
import com.chen.HospitalSelection.service.DoctorService;
import com.chen.HospitalSelection.vo.DoctorSimpleVO;
import com.chen.HospitalSelection.vo.DoctorVO;
import com.chen.HospitalSelection.vo.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 医生服务实现类
 *
 * @author chen
 * @since 2025-01-30
 */
@Slf4j
@Service
public class DoctorServiceImpl implements DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;

    @Override
    public PageResult<DoctorSimpleVO> getDoctorList(PageQueryDTO dto) {
        log.info("分页查询医生列表，页码：{}，每页大小：{}", dto.getPage(), dto.getPageSize());

        // 使用PageHelper进行物理分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        List<Doctor> doctorList = doctorMapper.selectAll();
        PageInfo<Doctor> pageInfo = new PageInfo<>(doctorList);

        List<DoctorSimpleVO> voList = doctorList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageInfo.getTotal(), dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public PageResult<DoctorSimpleVO> filterDoctors(DoctorFilterDTO dto) {
        log.info("多条件筛选医生，条件：{}", dto);

        // 使用PageHelper进行物理分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());

        // 使用动态条件查询（在数据库层面过滤）
        List<Doctor> doctorList = doctorMapper.selectBySimpleCondition(
                dto.getHospitalId(),
                dto.getDeptId(),
                dto.getTitle()
        );

        PageInfo<Doctor> pageInfo = new PageInfo<>(doctorList);

        List<DoctorSimpleVO> voList = doctorList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageInfo.getTotal(), dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public DoctorVO getDoctorDetail(Long doctorId) {
        log.info("查询医生详情，医生ID：{}", doctorId);

        // 使用关联查询获取医生详情（包含医院和科室名称）
        Doctor doctor = doctorMapper.selectWithDetailsById(doctorId);
        if (doctor == null) {
            throw ResourceNotFoundException.doctorNotFound();  // 使用 ResourceNotFoundException，返回 404
        }

        return convertToVO(doctor);
    }

    @Override
    public List<DoctorSimpleVO> getDoctorsByDepartment(Long deptId) {
        log.info("查询科室医生列表，科室ID：{}", deptId);

        List<Doctor> doctorList = doctorMapper.selectByDeptId(deptId);

        return doctorList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<DoctorSimpleVO> getDoctorsByHospital(Long hospitalId, PageQueryDTO dto) {
        log.info("查询医院医生列表，医院ID：{}，页码：{}，每页大小：{}", hospitalId, dto.getPage(), dto.getPageSize());

        // 使用PageHelper进行物理分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        List<Doctor> doctorList = doctorMapper.selectByHospitalId(hospitalId);
        PageInfo<Doctor> pageInfo = new PageInfo<>(doctorList);

        List<DoctorSimpleVO> voList = doctorList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageInfo.getTotal(), dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public List<DoctorSimpleVO> getDoctorsByHospitalAndDept(Long hospitalId, Long deptId) {
        log.info("查询医院科室医生列表，医院ID：{}，科室ID：{}", hospitalId, deptId);

        List<Doctor> doctorList = doctorMapper.selectByHospitalAndDept(hospitalId, deptId);

        return doctorList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorSimpleVO> getDoctorsByHospitalAndDeptName(Long hospitalId, String deptName) {
        log.info("查询医院科室医生列表（按科室名称），医院ID：{}，科室名称：{}", hospitalId, deptName);

        List<Doctor> doctorList = doctorMapper.selectByHospitalAndDeptName(hospitalId, deptName);

        return doctorList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<DoctorSimpleVO> searchDoctors(String keyword, PageQueryDTO dto) {
        log.info("搜索医生，关键词：{}", keyword);

        // 使用PageHelper进行物理分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        List<Doctor> doctorList = doctorMapper.searchByKeyword(keyword);
        PageInfo<Doctor> pageInfo = new PageInfo<>(doctorList);

        List<DoctorSimpleVO> voList = doctorList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageInfo.getTotal(), dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public PageResult<DoctorSimpleVO> getDoctorsByTitle(String title, PageQueryDTO dto) {
        log.info("查询职称医生列表，职称：{}", title);

        DoctorFilterDTO filterDTO = new DoctorFilterDTO();
        filterDTO.setTitle(title);
        filterDTO.setPage(dto.getPage());
        filterDTO.setPageSize(dto.getPageSize());

        return filterDoctors(filterDTO);
    }

    /**
     * 转换为医生简要VO
     */
    private DoctorSimpleVO convertToSimpleVO(Doctor doctor) {
        DoctorSimpleVO vo = new DoctorSimpleVO();
        BeanUtils.copyProperties(doctor, vo);
        return vo;
    }

    /**
     * 转换为医生详细VO
     */
    private DoctorVO convertToVO(Doctor doctor) {
        DoctorVO vo = new DoctorVO();
        BeanUtils.copyProperties(doctor, vo);
        return vo;
    }
}
