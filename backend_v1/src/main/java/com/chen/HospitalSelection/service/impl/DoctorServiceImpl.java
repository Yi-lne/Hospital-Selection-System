package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.DoctorFilterDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.DoctorMapper;
import com.chen.HospitalSelection.model.Doctor;
import com.chen.HospitalSelection.service.DoctorService;
import com.chen.HospitalSelection.vo.DoctorSimpleVO;
import com.chen.HospitalSelection.vo.DoctorVO;
import com.chen.HospitalSelection.vo.PageResult;
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

        // 获取所有数据
        List<Doctor> allList = doctorMapper.selectAll();
        long total = allList.size();

        // 内存分页
        int offset = (dto.getPage() - 1) * dto.getPageSize();
        int end = Math.min(offset + dto.getPageSize(), allList.size());
        List<Doctor> doctorList = offset < allList.size()
            ? allList.subList(offset, end)
            : new java.util.ArrayList<>();

        List<DoctorSimpleVO> voList = doctorList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public PageResult<DoctorSimpleVO> filterDoctors(DoctorFilterDTO dto) {
        log.info("多条件筛选医生，条件：{}", dto);

        // 获取所有数据
        List<Doctor> allList = doctorMapper.selectAll();

        // 应用过滤条件
        List<Doctor> filteredList = allList.stream()
                .filter(doctor -> {
                    if (dto.getHospitalId() != null && !doctor.getHospitalId().equals(dto.getHospitalId())) {
                        return false;
                    }
                    if (dto.getDeptId() != null && !doctor.getDeptId().equals(dto.getDeptId())) {
                        return false;
                    }
                    if (dto.getTitle() != null && !dto.getTitle().equals(doctor.getTitle())) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        long total = filteredList.size();

        // 内存分页
        int offset = (dto.getPage() - 1) * dto.getPageSize();
        int end = Math.min(offset + dto.getPageSize(), filteredList.size());
        List<Doctor> doctorList = offset < filteredList.size()
            ? filteredList.subList(offset, end)
            : new java.util.ArrayList<>();

        List<DoctorSimpleVO> voList = doctorList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public DoctorVO getDoctorDetail(Long doctorId) {
        log.info("查询医生详情，医生ID：{}", doctorId);

        Doctor doctor = doctorMapper.selectById(doctorId);
        if (doctor == null) {
            throw new BusinessException("医生不存在");
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
    public List<DoctorSimpleVO> getDoctorsByHospital(Long hospitalId) {
        log.info("查询医院医生列表，医院ID：{}", hospitalId);

        List<Doctor> doctorList = doctorMapper.selectByHospitalId(hospitalId);

        return doctorList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());
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
    public PageResult<DoctorSimpleVO> searchDoctors(String keyword, PageQueryDTO dto) {
        log.info("搜索医生，关键词：{}", keyword);

        // 使用模糊搜索
        List<Doctor> allList = doctorMapper.searchByKeyword(keyword);

        long total = allList.size();

        // 内存分页
        int offset = (dto.getPage() - 1) * dto.getPageSize();
        int end = Math.min(offset + dto.getPageSize(), allList.size());
        List<Doctor> doctorList = offset < allList.size()
            ? allList.subList(offset, end)
            : new java.util.ArrayList<>();

        List<DoctorSimpleVO> voList = doctorList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
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
