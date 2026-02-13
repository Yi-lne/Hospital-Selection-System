package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.service.DepartmentService;
import com.chen.HospitalSelection.vo.DepartmentVO;
import com.chen.HospitalSelection.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 医院科室接口
 * 基础路径：/api/department
 *
 * @author chen
 */
@RestController
@RequestMapping("/department")
@Api(tags = "科室管理")
public class DepartmentController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * 获取所有科室列表
     * 接口路径：GET /api/department/all
     * 是否需要登录：否
     *
     * @return 所有科室列表
     */
    @GetMapping("/all")
    @ApiOperation("获取所有科室")
    public Result<List<DepartmentVO>> getAllDepartments() {
        List<DepartmentVO> departments = departmentService.getAllDepartments();
        return Result.success(departments);
    }

    /**
     * 医院科室列表
     * 接口路径：GET /api/department/hospital/{hospitalId}
     * 是否需要登录：否
     *
     * @param hospitalId 医院ID
     * @return 该医院的科室列表
     */
    @GetMapping("/hospital/{hospitalId}")
    @ApiOperation("医院科室列表")
    public Result<List<DepartmentVO>> getHospitalDepartments(@PathVariable Long hospitalId) {
        List<DepartmentVO> departments = departmentService.getDepartmentsByHospital(hospitalId);
        return Result.success(departments);
    }

    /**
     * 获取某医院所有医生的所属科室（去重）
     * 接口路径：GET /api/department/hospital/{hospitalId}/doctors
     * 是否需要登录：否
     *
     * @param hospitalId 医院ID
     * @return 该医院医生所属的科室列表（按科室名称去重）
     */
    @GetMapping("/hospital/{hospitalId}/doctors")
    @ApiOperation("医院医生所属科室列表")
    public Result<List<DepartmentVO>> getDepartmentsByHospitalDoctors(@PathVariable Long hospitalId) {
        List<DepartmentVO> departments = departmentService.getDepartmentsByHospitalDoctors(hospitalId);
        return Result.success(departments);
    }

    /**
     * 科室详情
     * 接口路径：GET /api/department/{id}
     * 是否需要登录：否
     *
     * @param id 科室ID
     * @return 科室详细信息
     */
    @GetMapping("/{id}")
    @ApiOperation("科室详情")
    public Result<DepartmentVO> getDepartmentDetail(@PathVariable Long id) {
        DepartmentVO departmentVO = departmentService.getDepartmentDetail(id);
        return Result.success(departmentVO);
    }
}
