package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.service.DiseaseService;
import com.chen.HospitalSelection.vo.DiseaseVO;
import com.chen.HospitalSelection.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 疾病分类接口
 * 基础路径：/api/disease
 *
 * @author chen
 */
@RestController
@RequestMapping("/disease")
@Api(tags = "疾病分类管理")
public class DiseaseController {

    @Autowired
    private DiseaseService diseaseService;

    /**
     * 疾病分类树
     * 接口路径：GET /api/disease/tree
     * 是否需要登录：否
     *
     * @return 完整的疾病分类树（包含一级和二级分类）
     */
    @GetMapping("/tree")
    @ApiOperation("疾病分类树")
    public Result<List<DiseaseVO>> getDiseaseTree() {
        List<DiseaseVO> tree = diseaseService.getDiseaseTree();
        return Result.success(tree);
    }

    /**
     * 一级分类列表
     * 接口路径：GET /api/disease/level1
     * 是否需要登录：否
     *
     * @return 所有一级分类
     */
    @GetMapping("/level1")
    @ApiOperation("一级分类列表")
    public Result<List<DiseaseVO>> getLevel1Diseases() {
        List<DiseaseVO> level1List = diseaseService.getLevel1Diseases();
        return Result.success(level1List);
    }

    /**
     * 二级分类列表
     * 接口路径：GET /api/disease/level2/{parentId}
     * 是否需要登录：否
     *
     * @param parentId 父分类ID
     * @return 该父分类下的所有二级分类
     */
    @GetMapping("/level2/{parentId}")
    @ApiOperation("二级分类列表")
    public Result<List<DiseaseVO>> getLevel2Diseases(@PathVariable Long parentId) {
        List<DiseaseVO> level2List = diseaseService.getLevel2Diseases(parentId);
        return Result.success(level2List);
    }
}
