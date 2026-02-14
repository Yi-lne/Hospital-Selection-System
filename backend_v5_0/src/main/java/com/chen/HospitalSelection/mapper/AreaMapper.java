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
     * 根据地区名称查询
     * @param name 地区名称
     * @return 地区对象
     */
    Area selectByName(@Param("name") String name);

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
     * 根据层级查询地区
     * @param level 层级（1=省，2=市，3=区/县）
     * @return 地区列表
     */
    List<Area> selectByLevel(@Param("level") Integer level);

    /**
     * 查询所有地区
     * @return 地区列表
     */
    List<Area> selectAll();

    /**
     * 模糊搜索地区（按名称）
     * @param keyword 关键词
     * @return 地区列表
     */
    List<Area> searchByName(@Param("keyword") String keyword);

    /**
     * 插入地区
     * @param area 地区对象
     * @return 影响行数
     */
    int insert(Area area);

    /**
     * 批量插入地区
     * @param areas 地区列表
     * @return 影响行数
     */
    int batchInsert(@Param("areas") List<Area> areas);

    /**
     * 更新地区信息
     * @param area 地区对象
     * @return 影响行数
     */
    int updateById(Area area);

    /**
     * 删除地区
     * @param id 主键ID
     * @return 影响行数
     */
    int deleteById(@Param("id") Long id);

    /**
     * 根据编码删除地区
     * @param code 地区编码
     * @return 影响行数
     */
    int deleteByCode(@Param("code") String code);

    /**
     * 批量删除地区
     * @param ids ID列表
     * @return 影响行数
     */
    int batchDelete(@Param("ids") List<Long> ids);

    /**
     * 根据父级编码删除所有子地区
     * @param parentCode 父级编码
     * @return 影响行数
     */
    int deleteByParentCode(@Param("parentCode") String parentCode);

    /**
     * 检查地区编码是否存在
     * @param code 地区编码
     * @return 存在数量
     */
    int countByCode(@Param("code") String code);

    /**
     * 统计省份数量
     * @return 省份数量
     */
    int countProvinces();

    /**
     * 统计城市的数量（根据省份）
     * @param provinceCode 省份编码
     * @return 城市数量
     */
    int countCitiesByProvince(@Param("provinceCode") String provinceCode);

    /**
     * 统计区县的数量（根据城市）
     * @param cityCode 城市编码
     * @return 区县数量
     */
    int countAreasByCity(@Param("cityCode") String cityCode);
}
