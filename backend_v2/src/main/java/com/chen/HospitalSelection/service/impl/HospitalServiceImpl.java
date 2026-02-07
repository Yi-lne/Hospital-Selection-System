package com.chen.HospitalSelection.service.impl;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        log.info("筛选详情 - 省份编码: {}, 城市编码: {}, 区县编码: {}, 科室: {}",
            dto.getProvinceCode(), dto.getCityCode(), dto.getAreaCode(), dto.getDepartment());

        // 使用PageHelper进行物理分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());

        // 获取科室别名列表（用于匹配科室简称和全称）
        List<String> departmentNames = getDepartmentAliases(dto.getDepartment());

        // 使用动态条件查询（在数据库层面过滤）
        List<Hospital> hospitalList = hospitalMapper.selectByCondition(
                dto.getHospitalLevel(),
                dto.getProvinceCode(),
                dto.getCityCode(),
                dto.getAreaCode(),
                dto.getIsMedicalInsurance(),
                dto.getDepartment(),
                departmentNames  // 传递科室别名列表
        );

        // 输出查询结果的详细信息用于调试
        if (dto.getAreaCode() != null && !dto.getAreaCode().isEmpty()) {
            log.info("区县筛选查询结果 - 应该筛选区县编码: {}, 实际查询到 {} 家医院",
                dto.getAreaCode(), hospitalList.size());
            hospitalList.forEach(hospital -> {
                log.debug("医院: {}, 省份编码: {}, 城市编码: {}, 区县编码: {}",
                    hospital.getHospitalName(),
                    hospital.getProvinceCode(),
                    hospital.getCityCode(),
                    hospital.getAreaCode());
            });
        }

        PageInfo<Hospital> pageInfo = new PageInfo<>(hospitalList);

        // 转换为VO
        List<HospitalSimpleVO> voList = hospitalList.stream()
                .map(this::convertToSimpleVO)
                .collect(Collectors.toList());

        log.info("筛选完成，共找到{}家医院", pageInfo.getTotal());
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
     * 获取科室别名列表（用于匹配科室简称和全称）
     * @param department 科室名称
     * @return 科室别名列表（包括全称和常见简称）
     */
    private List<String> getDepartmentAliases(String department) {
        if (department == null || department.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> aliases = new ArrayList<>();
        aliases.add(department);  // 首先添加原始科室名

        // 科室别名映射表
        Map<String, List<String>> aliasMap = new HashMap<>();
        aliasMap.put("心血管内科", Arrays.asList("心血管内科", "心内科", "心脏内科"));
        aliasMap.put("呼吸内科", Arrays.asList("呼吸内科", "呼吸科"));
        aliasMap.put("消化内科", Arrays.asList("消化内科", "消化科", "胃肠内科"));
        aliasMap.put("神经内科", Arrays.asList("神经内科", "神经科"));
        aliasMap.put("骨科", Arrays.asList("骨科", "骨外科", "创伤骨科"));
        aliasMap.put("泌尿外科", Arrays.asList("泌尿外科", "泌尿科"));
        aliasMap.put("妇产科", Arrays.asList("妇产科", "妇科", "产科", "妇产科"));
        aliasMap.put("儿科", Arrays.asList("儿科", "小儿科", "儿童科"));
        aliasMap.put("眼科", Arrays.asList("眼科", "眼耳鼻喉科"));
        aliasMap.put("耳鼻喉科", Arrays.asList("耳鼻喉科", "五官科", "ENT"));
        aliasMap.put("肿瘤科", Arrays.asList("肿瘤科", "肿瘤内科", "肿瘤外科", " oncology"));
        aliasMap.put("内分泌科", Arrays.asList("内分泌科", "内分泌代谢科"));
        aliasMap.put("肾内科", Arrays.asList("肾内科", "肾脏内科", "肾病学"));
        aliasMap.put("血液科", Arrays.asList("血液科", "血液内科"));
        aliasMap.put("风湿免疫科", Arrays.asList("风湿免疫科", "风湿科", "免疫科"));
        aliasMap.put("皮肤科", Arrays.asList("皮肤科", "皮肤病科", "皮肤性病科"));
        aliasMap.put("感染科", Arrays.asList("感染科", "感染性疾病科", "传染科"));
        aliasMap.put("急诊科", Arrays.asList("急诊科", "急诊内科", "急诊外科"));
        aliasMap.put("麻醉科", Arrays.asList("麻醉科", "麻醉手术科"));
        aliasMap.put("影像科", Arrays.asList("影像科", "放射科", "医学影像科"));
        aliasMap.put("检验科", Arrays.asList("检验科", "医学检验科", "化验室"));
        aliasMap.put("超声科", Arrays.asList("超声科", "B超室", "超声诊断科"));
        aliasMap.put("病理科", Arrays.asList("病理科", "病理学"));
        aliasMap.put("全科医疗科", Arrays.asList("全科医疗科", "全科", "全科医学科", "全科医疗"));
        aliasMap.put("重症医学科", Arrays.asList("重症医学科", "ICU", "重症监护室", "重症监护病房"));
        aliasMap.put("康复医学科", Arrays.asList("康复医学科", "康复科", "康复医学"));
        aliasMap.put("精神科", Arrays.asList("精神科", "精神卫生科", "心理科"));
        aliasMap.put("口腔科", Arrays.asList("口腔科", "牙科", "口腔颌面外科"));

        // 查找科室的别名
        for (Map.Entry<String, List<String>> entry : aliasMap.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            // 如果输入的科室名匹配映射表中的任何一个值，返回所有别名
            if (values.contains(department) || key.equals(department)) {
                return values;
            }
        }

        return aliases;
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
