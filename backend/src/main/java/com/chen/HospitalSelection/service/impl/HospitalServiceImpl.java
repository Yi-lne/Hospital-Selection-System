package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.AIQueryRequestDTO;
import com.chen.HospitalSelection.dto.HospitalFilterDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.AreaMapper;
import com.chen.HospitalSelection.mapper.DepartmentMapper;
import com.chen.HospitalSelection.mapper.DoctorMapper;
import com.chen.HospitalSelection.mapper.HospitalMapper;
import com.chen.HospitalSelection.model.Area;
import com.chen.HospitalSelection.model.Department;
import com.chen.HospitalSelection.model.Doctor;
import com.chen.HospitalSelection.model.Hospital;
import com.chen.HospitalSelection.service.HospitalService;
import com.chen.HospitalSelection.vo.DepartmentVO;
import com.chen.HospitalSelection.vo.DoctorSimpleVO;
import com.chen.HospitalSelection.vo.HospitalSimpleVO;
import com.chen.HospitalSelection.vo.HospitalVO;
import com.chen.HospitalSelection.vo.PageResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
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

    @Autowired
    private AreaMapper areaMapper;

    @Autowired
    private ZhipuAIService zhipuAIService;

    @Override
    public PageResult<HospitalSimpleVO> getHospitalList(PageQueryDTO dto) {
        log.info("分页查询医院列表，页码：{}，每页大小：{}", dto.getPage(), dto.getPageSize());

        // 使用PageHelper进行物理分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        List<Hospital> hospitalList = hospitalMapper.selectAll();
        PageInfo<Hospital> pageInfo = new PageInfo<>(hospitalList);

        // 转换为VO
        List<HospitalSimpleVO> voList = hospitalList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageInfo.getTotal(), dto.getPage(), dto.getPageSize(), voList);
    }

    @Override
    public PageResult<HospitalSimpleVO> filterHospitals(HospitalFilterDTO dto) {
        log.info("多条件筛选医院，条件：{}", dto);

        // 使用PageHelper进行物理分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());

        // 使用动态条件查询（在数据库层面过滤）
        List<Hospital> hospitalList = hospitalMapper.selectByCondition(
                dto.getHospitalLevel(),
                dto.getProvinceCode(),
                dto.getCityCode(),
                dto.getAreaCode(),
                dto.getIsMedicalInsurance(),
                dto.getKeyDepartments(),
                dto.getDeptName(),
                dto.getSortBy()
        );

        PageInfo<Hospital> pageInfo = new PageInfo<>(hospitalList);

        // 转换为VO
        List<HospitalSimpleVO> voList = hospitalList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageInfo.getTotal(), dto.getPage(), dto.getPageSize(), voList);
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

        // 使用PageHelper进行物理分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        List<Hospital> hospitalList = hospitalMapper.searchByKeyword(keyword);
        PageInfo<Hospital> pageInfo = new PageInfo<>(hospitalList);

        List<HospitalSimpleVO> voList = hospitalList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());

        return new PageResult<>(pageInfo.getTotal(), dto.getPage(), dto.getPageSize(), voList);
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

    @Override
    public PageResult<HospitalSimpleVO> aiRecommendHospitals(AIQueryRequestDTO request) {
        log.info("AI推荐医院，用户查询：{}", request.getQuery());

        // 1. AI解析用户查询，提取筛选条件
        HospitalFilterDTO filter = zhipuAIService.analyzeQuery(request.getQuery());

        // 2. 设置分页参数
        filter.setPage(request.getPageNum());
        filter.setPageSize(request.getPageSize());

        // 3. 调用现有的筛选方法
        return filterHospitals(filter);
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

        // 查询省份名称
        if (StringUtils.hasText(hospital.getProvinceCode())) {
            Area province = areaMapper.selectByCode(hospital.getProvinceCode());
            if (province != null) {
                vo.setProvinceName(province.getName());
            }
        }

        // 查询城市名称
        if (StringUtils.hasText(hospital.getCityCode())) {
            Area city = areaMapper.selectByCode(hospital.getCityCode());
            if (city != null) {
                vo.setCityName(city.getName());
            }
        }

        // 查询区县名称
        if (StringUtils.hasText(hospital.getAreaCode())) {
            Area area = areaMapper.selectByCode(hospital.getAreaCode());
            if (area != null) {
                vo.setAreaName(area.getName());
            }
        }

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
