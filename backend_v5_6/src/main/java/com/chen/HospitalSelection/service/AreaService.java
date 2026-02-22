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

    /**
     * 根据地区编码获取地区信息
     *
     * @param code 地区编码
     * @return 地区信息
     */
    AreaVO getAreaByCode(String code);

    /**
     * 根据地区ID获取地区信息
     *
     * @param areaId 地区ID
     * @return 地区信息
     */
    AreaVO getAreaById(Long areaId);

    /**
     * 根据地区名称搜索地区
     *
     * @param name 地区名称（支持模糊搜索）
     * @return 地区列表
     */
    List<AreaVO> searchAreasByName(String name);

    /**
     * 获取地区完整路径（省市区）
     *
     * @param areaCode 区县编码
     * @return 地区路径（如：广东省 > 广州市 > 天河区）
     */
    String getAreaPath(String areaCode);
}
