package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.mapper.AreaMapper;
import com.chen.HospitalSelection.model.Area;
import com.chen.HospitalSelection.service.AreaService;
import com.chen.HospitalSelection.vo.AreaVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 地区服务实现类
 *
 * @author chen
 * @since 2025-01-30
 */
@Slf4j
@Service
public class AreaServiceImpl implements AreaService {

    @Autowired
    private AreaMapper areaMapper;

    @Override
    public List<AreaVO> getAreaTree() {
        log.info("获取省市区树");

        // 查询所有省份
        List<Area> provinceList = areaMapper.selectProvinces();

        // 构建树形结构
        List<AreaVO> tree = new ArrayList<>();
        for (Area province : provinceList) {
            AreaVO provinceVO = convertToVO(province);

            // 查询该省份下的所有城市
            List<Area> cityList = areaMapper.selectCitiesByProvince(province.getCode());
            List<AreaVO> cityVOList = new ArrayList<>();

            for (Area city : cityList) {
                AreaVO cityVO = convertToVO(city);

                // 查询该城市下的所有区县
                List<Area> areaList = areaMapper.selectAreasByCity(city.getCode());
                List<AreaVO> areaVOList = areaList.stream()
                        .map(this::convertToVO)
                        .collect(Collectors.toList());
                cityVO.setChildren(areaVOList);
                cityVOList.add(cityVO);
            }

            provinceVO.setChildren(cityVOList);
            tree.add(provinceVO);
        }

        return tree;
    }

    @Override
    public List<AreaVO> getProvinces() {
        log.info("获取所有省份");

        List<Area> provinceList = areaMapper.selectProvinces();

        return provinceList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AreaVO> getCitiesByProvince(String provinceCode) {
        log.info("获取城市列表，省份编码：{}", provinceCode);

        List<Area> cityList = areaMapper.selectCitiesByProvince(provinceCode);

        return cityList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<AreaVO> getAreasByCity(String cityCode) {
        log.info("获取区县列表，城市编码：{}", cityCode);

        List<Area> areaList = areaMapper.selectAreasByCity(cityCode);

        return areaList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    /**
     * 转换为地区VO
     */
    private AreaVO convertToVO(Area area) {
        AreaVO vo = new AreaVO();
        vo.setId(area.getId());
        vo.setCode(area.getCode());
        vo.setName(area.getName());
        vo.setParentCode(area.getParentCode());
        vo.setLevel(area.getLevel());
        return vo;
    }
}
