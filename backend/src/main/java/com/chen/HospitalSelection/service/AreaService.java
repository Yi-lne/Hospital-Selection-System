package com.chen.HospitalSelection.service;

import com.chen.HospitalSelection.vo.AreaVO;

import java.util.List;

/**
 * 地区服务接口
 * 提供省市区三级联动查询功能
 *
 * @author chen
 * @since 2025-01-30
 */
public interface AreaService {

    /**
     * 获取完整的省市区树
     *
     * @return 省市区树（三级）
     */
    List<AreaVO> getAreaTree();

    /**
     * 获取所有省份
     *
     * @return 省份列表
     */
    List<AreaVO> getProvinces();

    /**
     * 根据省份编码获取城市列表
     *
     * @param provinceCode 省份编码
     * @return 城市列表
     */
    List<AreaVO> getCitiesByProvince(String provinceCode);

    /**
     * 根据城市编码获取区县列表
     *
     * @param cityCode 城市编码
     * @return 区县列表
     */
    List<AreaVO> getAreasByCity(String cityCode);
}
