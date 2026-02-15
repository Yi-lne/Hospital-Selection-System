package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.service.AreaService;
import com.chen.HospitalSelection.vo.AreaVO;
import com.chen.HospitalSelection.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 地区接口
 * 基础路径：/api/area
 *
 * @author chen
 */
@RestController
@RequestMapping("/area")
@Api(tags = "地区管理")
public class AreaController {

    @Autowired
    private AreaService areaService;

    /**
     * 省市区树
     * 接口路径：GET /api/area/tree
     * 是否需要登录：否
     *
     * @return 完整的省市区三级联动树
     */
    @GetMapping("/tree")
    @ApiOperation("省市区树")
    public Result<List<AreaVO>> getAreaTree() {
        List<AreaVO> tree = areaService.getAreaTree();
        return Result.success(tree);
    }

    /**
     * 省份列表
     * 接口路径：GET /api/area/province
     * 是否需要登录：否
     *
     * @return 所有省份
     */
    @GetMapping("/province")
    @ApiOperation("省份列表")
    public Result<List<AreaVO>> getProvinces() {
        List<AreaVO> provinces = areaService.getProvinces();
        return Result.success(provinces);
    }

    /**
     * 城市列表
     * 接口路径：GET /api/area/city/{provinceCode}
     * 是否需要登录：否
     *
     * @param provinceCode 省份编码
     * @return 该省份下的所有城市
     */
    @GetMapping("/city/{provinceCode}")
    @ApiOperation("城市列表")
    public Result<List<AreaVO>> getCities(@PathVariable String provinceCode) {
        List<AreaVO> cities = areaService.getCitiesByProvince(provinceCode);
        return Result.success(cities);
    }

    /**
     * 区县列表
     * 接口路径：GET /api/area/area/{cityCode}
     * 是否需要登录：否
     *
     * @param cityCode 城市编码
     * @return 该城市下的所有区县
     */
    @GetMapping("/area/{cityCode}")
    @ApiOperation("区县列表")
    public Result<List<AreaVO>> getAreas(@PathVariable String cityCode) {
        List<AreaVO> areas = areaService.getAreasByCity(cityCode);
        return Result.success(areas);
    }
}
