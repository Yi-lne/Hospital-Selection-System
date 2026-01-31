package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.HospitalFilterDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.DepartmentMapper;
import com.chen.HospitalSelection.mapper.DoctorMapper;
import com.chen.HospitalSelection.mapper.HospitalMapper;
import com.chen.HospitalSelection.model.Department;
import com.chen.HospitalSelection.model.Doctor;
import com.chen.HospitalSelection.model.Hospital;
import com.chen.HospitalSelection.service.HospitalService;
import com.chen.HospitalSelection.vo.DepartmentVO;
import com.chen.HospitalSelection.vo.DoctorSimpleVO;
import com.chen.HospitalSelection.vo.HospitalSimpleVO;
import com.chen.HospitalSelection.vo.HospitalVO;
import com.chen.HospitalSelection.vo.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 医院服务实现类
 *
 * @author chen
 * @since 2025-01-30
 */
@Slf4j
@Service
public class HospitalServiceImpl implements HospitalService {

    @Autowired
    private HospitalMapper hospitalMapper;

    @Autowired
    private DepartmentMapper departmentMapper;

    @Autowired
    private DoctorMapper doctorMapper;

    @Override
    public PageResult<HospitalSimpleVO> getHospitalList(PageQueryDTO dto) {
        log.info("分页查询医院列表，页码：{}，每页大小：{}", dto.getPage(), dto.getPageSize());

        // 获取所有数据
        List<Hospital> allList = hospitalMapper.selectAll();
        long total = allList.size();

        // 内存分页
        int offset = (dto.getPage() - 1) * dto.getPageSize();
        int end = Math.min(offset + dto.getPageSize(), allList.size());
        List<Hospital> hospitalList = offset < allList.size()
            ? allList.subList(offset, end)
            : new java.util.ArrayList<>();

        // 转换为VO
        List<HospitalSimpleVO> voList = hospitalList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public PageResult<HospitalSimpleVO> filterHospitals(HospitalFilterDTO dto) {
        log.info("多条件筛选医院，条件：{}", dto);

        // 获取所有数据
        List<Hospital> allList = hospitalMapper.selectAll();

        // 应用过滤条件
        List<Hospital> filteredList = allList.stream()
                .filter(hospital -> {
                    // 注意：疾病编码筛选应通过关联表实现，这里暂不实现
                    // 如果需要疾病筛选，需要查询医院-疾病关联表
                    if (dto.getHospitalLevel() != null && !dto.getHospitalLevel().equals(hospital.getHospitalLevel())) {
                        return false;
                    }
                    if (dto.getProvinceCode() != null && !dto.getProvinceCode().equals(hospital.getProvinceCode())) {
                        return false;
                    }
                    if (dto.getCityCode() != null && !dto.getCityCode().equals(hospital.getCityCode())) {
                        return false;
                    }
                    if (dto.getAreaCode() != null && !dto.getAreaCode().equals(hospital.getAreaCode())) {
                        return false;
                    }
                    if (dto.getIsMedicalInsurance() != null && !dto.getIsMedicalInsurance().equals(hospital.getIsMedicalInsurance())) {
                        return false;
                    }
                    if (StringUtils.hasText(dto.getKeyDepartments()) &&
                        !StringUtils.hasText(hospital.getKeyDepartments())) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());

        long total = filteredList.size();

        // 内存分页
        int offset = (dto.getPage() - 1) * dto.getPageSize();
        int end = Math.min(offset + dto.getPageSize(), filteredList.size());
        List<Hospital> hospitalList = offset < filteredList.size()
            ? filteredList.subList(offset, end)
            : new java.util.ArrayList<>();

        // 转换为VO
        List<HospitalSimpleVO> voList = hospitalList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public HospitalVO getHospitalDetail(Long hospitalId) {
        log.info("查询医院详情，医院ID：{}", hospitalId);

        Hospital hospital = hospitalMapper.selectById(hospitalId);
        if (hospital == null) {
            throw new BusinessException("医院不存在");
        }

        return convertToVO(hospital);
    }

    @Override
    public List<DepartmentVO> getHospitalDepartments(Long hospitalId) {
        log.info("查询医院科室列表，医院ID：{}", hospitalId);

        List<Department> departmentList = departmentMapper.selectByHospitalId(hospitalId);

        return departmentList.stream()
                .map(this::convertToDepartmentVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<DoctorSimpleVO> getHospitalDoctors(Long hospitalId) {
        log.info("查询医院医生列表，医院ID：{}", hospitalId);

        List<Doctor> doctorList = doctorMapper.selectByHospitalId(hospitalId);

        return doctorList.stream()
                .map(this::convertToDoctorSimpleVO)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<HospitalSimpleVO> searchHospitals(String keyword, PageQueryDTO dto) {
        log.info("搜索医院，关键词：{}", keyword);

        // 使用模糊搜索
        List<Hospital> allList = hospitalMapper.searchByKeyword(keyword);

        long total = allList.size();

        // 内存分页
        int offset = (dto.getPage() - 1) * dto.getPageSize();
        int end = Math.min(offset + dto.getPageSize(), allList.size());
        List<Hospital> hospitalList = offset < allList.size()
            ? allList.subList(offset, end)
            : new java.util.ArrayList<>();

        List<HospitalSimpleVO> voList = hospitalList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());

        return new PageResult<>(total, dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public List<String> getSearchSuggestions(String keyword) {
        log.info("获取搜索建议，关键词：{}", keyword);

        // 使用模糊搜索查询医院名称包含关键词的医院
        List<Hospital> hospitalList = hospitalMapper.searchByKeyword(keyword);

        // 提取医院名称
        return hospitalList.stream()
                .map(Hospital::getHospitalName)
                .limit(10)
                .collect(Collectors.toList());
    }

    @Override
    public PageResult<HospitalSimpleVO> getHospitalsByCity(String cityCode, PageQueryDTO dto) {
        log.info("查询城市医院列表，城市编码：{}", cityCode);

        HospitalFilterDTO filterDTO = new HospitalFilterDTO();
        filterDTO.setCityCode(cityCode);
        filterDTO.setPage(dto.getPage());
        filterDTO.setPageSize(dto.getPageSize());

        return filterHospitals(filterDTO);
    }

    @Override
    public PageResult<HospitalSimpleVO> getHospitalsByLevel(String level, PageQueryDTO dto) {
        log.info("查询等级医院列表，等级：{}", level);

        HospitalFilterDTO filterDTO = new HospitalFilterDTO();
        filterDTO.setHospitalLevel(level);
        filterDTO.setPage(dto.getPage());
        filterDTO.setPageSize(dto.getPageSize());

        return filterHospitals(filterDTO);
    }

    /**
     * 转换为医院简要VO
     */
    private HospitalSimpleVO convertToSimpleVO(Hospital hospital) {
        HospitalSimpleVO vo = new HospitalSimpleVO();
        BeanUtils.copyProperties(hospital, vo);
        return vo;
    }

    /**
     * 转换为医院详细VO
     */
    private HospitalVO convertToVO(Hospital hospital) {
        HospitalVO vo = new HospitalVO();
        BeanUtils.copyProperties(hospital, vo);
        return vo;
    }

    /**
     * 转换为科室VO
     */
    private DepartmentVO convertToDepartmentVO(Department department) {
        DepartmentVO vo = new DepartmentVO();
        BeanUtils.copyProperties(department, vo);
        return vo;
    }

    /**
     * 转换为医生简要VO
     */
    private DoctorSimpleVO convertToDoctorSimpleVO(Doctor doctor) {
        DoctorSimpleVO vo = new DoctorSimpleVO();
        BeanUtils.copyProperties(doctor, vo);
        return vo;
    }
}
