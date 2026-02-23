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

    @Override
    public AreaVO getAreaByCode(String code) {
        log.info("根据编码查询地区，编码：{}", code);

        Area area = areaMapper.selectByCode(code);
        if (area == null) {
            throw new BusinessException("地区不存在");
        }

        return convertToVO(area);
    }

    @Override
    public AreaVO getAreaById(Long areaId) {
        log.info("根据ID查询地区，ID：{}", areaId);

        Area area = areaMapper.selectById(areaId);
        if (area == null) {
            throw new BusinessException("地区不存在");
        }

        return convertToVO(area);
    }

    @Override
    public List<AreaVO> searchAreasByName(String name) {
        log.info("搜索地区，地区名称：{}", name);

        List<Area> areaList = areaMapper.searchByName(name);

        return areaList.stream()
                .map(this::convertToVO)
                .collect(Collectors.toList());
    }

    @Override
    public String getAreaPath(String areaCode) {
        log.info("获取地区路径，区县编码：{}", areaCode);

        Area area = areaMapper.selectByCode(areaCode);
        if (area == null) {
            throw new BusinessException("地区不存在");
        }

        StringBuilder path = new StringBuilder(area.getName());

        // 如果是区县级（level=3），需要查询市和省
        if (area.getLevel() == 3) {
            Area city = areaMapper.selectByCode(area.getParentCode());
            if (city != null) {
                path.insert(0, city.getName() + " > ");

                Area province = areaMapper.selectByCode(city.getParentCode());
                if (province != null) {
                    path.insert(0, province.getName() + " > ");
                }
            }
        } else if (area.getLevel() == 2) {
            // 如果是市级（level=2），需要查询省
            Area province = areaMapper.selectByCode(area.getParentCode());
            if (province != null) {
                path.insert(0, province.getName() + " > ");
            }
        }

        return path.toString();
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
