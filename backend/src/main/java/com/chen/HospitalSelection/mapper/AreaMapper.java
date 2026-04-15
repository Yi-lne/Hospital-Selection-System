package com.chen.HospitalSelection.mapper;

import com.chen.HospitalSelection.model.Area;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 省市区Mapper接口
 * 对应表：area_info
 */
@Mapper
public interface AreaMapper {

    /**
     * 根据ID查询地区
     * @param id 主键ID
     * @return 地区对象
     */
    Area selectById(@Param("id") Long id);

    /**
     * 根据地区编码查询
     * @param code 地区编码
     * @return 地区对象
     */
    Area selectByCode(@Param("code") String code);

    /**
     * 根据父级编码查询子地区列表
     * @param parentCode 父级编码（空=查询省级）
     * @return 地区列表
     */
    List<Area> selectByParentCode(@Param("parentCode") String parentCode);

    /**
     * 查询所有省份（层级=1）
     * @return 省份列表
     */
    List<Area> selectProvinces();

    /**
     * 根据省份编码查询所有城市
     * @param provinceCode 省份编码
     * @return 城市列表
     */
    List<Area> selectCitiesByProvince(@Param("provinceCode") String provinceCode);

    /**
     * 根据城市编码查询所有区县
     * @param cityCode 城市编码
     * @return 区县列表
     */
    List<Area> selectAreasByCity(@Param("cityCode") String cityCode);

    /**
     * 插入地区
     * @param area 地区对象
     * @return 影响行数
     */
    int insert(Area area);

    /**
     * 删除地区
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);
}
