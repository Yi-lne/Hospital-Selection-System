package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.dto.AIQueryRequestDTO;
import com.chen.HospitalSelection.dto.HospitalFilterDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.service.HospitalService;
import com.chen.HospitalSelection.vo.DepartmentVO;
import com.chen.HospitalSelection.vo.DoctorSimpleVO;
import com.chen.HospitalSelection.vo.HospitalSimpleVO;
import com.chen.HospitalSelection.vo.HospitalVO;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 医院信息接口
 * 基础路径：/api/hospital
 *
 * @author chen
 */
@Slf4j
@RestController
@RequestMapping("/hospital")
@Api(tags = "医院管理")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private com.chen.HospitalSelection.service.DoctorService doctorService;

    /**
     * 医院列表（分页）
     * 接口路径：GET /api/hospital/list
     * 是否需要登录：否
     *
     * @param dto 分页查询参数
     * @return 医院列表
     */
    @GetMapping("/list")
    @ApiOperation("医院列表（分页）")
    public Result<PageResult<HospitalSimpleVO>> getHospitalList(@Valid PageQueryDTO dto) {
        PageResult<HospitalSimpleVO> pageResult = hospitalService.getHospitalList(dto);
        return Result.success(pageResult);
    }

    /**
     * 医院搜索
     * 接口路径：GET /api/hospital/search
     * 是否需要登录：否
     *
     * @param keyword 搜索关键词
     * @param dto 分页查询参数
     * @return 搜索结果
     */
    @GetMapping("/search")
    @ApiOperation("医院搜索")
    public Result<PageResult<HospitalSimpleVO>> searchHospitals(
            @RequestParam String keyword,
            @Valid PageQueryDTO dto) {
        PageResult<HospitalSimpleVO> pageResult = hospitalService.searchHospitals(keyword, dto);
        return Result.success(pageResult);
    }

    /**
     * 医院多条件筛选（核心功能）
     * 接口路径：POST /api/hospital/filter
     * 是否需要登录：否
     *
     * @param dto 筛选条件（疾病类型、医院等级、地区、医保定点等）
     * @return 筛选后的医院列表
     */
    @PostMapping("/filter")
    @ApiOperation("医院多条件筛选")
    public Result<PageResult<HospitalSimpleVO>> filterHospitals(@RequestBody @Valid HospitalFilterDTO dto) {
        PageResult<HospitalSimpleVO> pageResult = hospitalService.filterHospitals(dto);
        return Result.success(pageResult);
    }

    /**
     * 医院详情
     * 接口路径：GET /api/hospital/{id}
     * 是否需要登录：否
     *
     * @param id 医院ID
     * @return 医院完整信息
     */
    @GetMapping("/{id}")
    @ApiOperation("医院详情")
    public Result<HospitalVO> getHospitalDetail(@PathVariable Long id) {
        HospitalVO hospitalVO = hospitalService.getHospitalDetail(id);
        return Result.success(hospitalVO);
    }

    /**
     * 医院科室列表
     * 接口路径：GET /api/hospital/{id}/departments
     * 是否需要登录：否
     *
     * @param id 医院ID
     * @return 医院所有科室
     */
    @GetMapping("/{id}/departments")
    @ApiOperation("医院科室列表")
    public Result<List<DepartmentVO>> getHospitalDepartments(@PathVariable Long id) {
        List<DepartmentVO> departments = hospitalService.getHospitalDepartments(id);
        return Result.success(departments);
    }

    /**
     * 医院医生列表
     * 接口路径：GET /api/hospital/{id}/doctors
     * 是否需要登录：否
     *
     * @param id 医院ID
     * @return 医院所有医生
     */
    @GetMapping("/{id}/doctors")
    @ApiOperation("医院医生列表")
    public Result<List<DoctorSimpleVO>> getHospitalDoctors(@PathVariable Long id) {
        List<DoctorSimpleVO> doctors = hospitalService.getHospitalDoctors(id);
        return Result.success(doctors);
    }

    /**
     * 根据科室名称筛选医院医生列表
     * 接口路径：GET /api/hospital/{id}/doctors/dept/{deptName}
     * 是否需要登录：否
     *
     * @param id 医院ID
     * @param deptName 科室名称
     * @return 筛选后的医生列表
     */
    @GetMapping("/{id}/doctors/dept/{deptName}")
    @ApiOperation("根据科室名称筛选医院医生列表")
    public Result<List<DoctorSimpleVO>> getHospitalDoctorsByDeptName(
            @PathVariable Long id,
            @PathVariable String deptName) {
        List<DoctorSimpleVO> doctors = doctorService.getDoctorsByHospitalAndDeptName(id, deptName);
        return Result.success(doctors);
    }

    /**
     * 医院搜索建议
     * 接口路径：GET /api/hospital/search/suggest
     * 是否需要登录：否
     *
     * @param keyword 搜索关键词
     * @return 搜索建议列表（医院名称）
     */
    @GetMapping("/search/suggest")
    @ApiOperation("医院搜索建议")
    public Result<List<String>> searchSuggest(@RequestParam String keyword) {
        List<String> suggestions = hospitalService.getSearchSuggestions(keyword);
        return Result.success(suggestions);
    }

    /**
     * AI智能推荐医院
     * 接口路径：POST /api/hospital/ai-recommend
     * 是否需要登录：否
     *
     * @param request AI查询请求（包含用户的自然语言查询和分页参数）
     * @return 医院列表
     */
    @PostMapping("/ai-recommend")
    @ApiOperation("AI智能推荐医院")
    public Result<PageResult<HospitalSimpleVO>> aiRecommend(@RequestBody @Valid AIQueryRequestDTO request) {
        log.info("收到AI推荐请求，查询内容：{}", request.getQuery());

        try {
            PageResult<HospitalSimpleVO> result = hospitalService.aiRecommendHospitals(request);
            return Result.success(result, "AI推荐成功");
        } catch (Exception e) {
            log.error("AI推荐失败", e);
            return Result.error(500, "AI推荐失败：" + e.getMessage());
        }
    }
}
