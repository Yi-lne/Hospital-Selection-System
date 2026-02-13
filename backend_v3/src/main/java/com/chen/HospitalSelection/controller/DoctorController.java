package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.dto.DoctorFilterDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.service.DoctorService;
import com.chen.HospitalSelection.vo.DoctorSimpleVO;
import com.chen.HospitalSelection.vo.DoctorVO;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 医生信息接口
 * 基础路径：/api/doctor
 *
 * @author chen
 */
@RestController
@RequestMapping("/doctor")
@Api(tags = "医生管理")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    /**
     * 医生列表（分页）
     * 接口路径：GET /api/doctor/list
     * 是否需要登录：否
     *
     * @param dto 分页查询参数
     * @return 医生列表
     */
    @GetMapping("/list")
    @ApiOperation("医生列表（分页）")
    public Result<PageResult<DoctorSimpleVO>> getDoctorList(@Valid PageQueryDTO dto) {
        PageResult<DoctorSimpleVO> pageResult = doctorService.getDoctorList(dto);
        return Result.success(pageResult);
    }

    /**
     * 医生详情
     * 接口路径：GET /api/doctor/{id}
     * 是否需要登录：否
     *
     * @param id 医生ID
     * @return 医生完整信息
     */
    @GetMapping("/{id}")
    @ApiOperation("医生详情")
    public Result<DoctorVO> getDoctorDetail(@PathVariable Long id) {
        DoctorVO doctorVO = doctorService.getDoctorDetail(id);
        return Result.success(doctorVO);
    }

    /**
     * 医生筛选
     * 接口路径：POST /api/doctor/filter
     * 是否需要登录：否
     *
     * @param dto 筛选条件（医院、科室、职称等）
     * @return 筛选后的医生列表
     */
    @PostMapping("/filter")
    @ApiOperation("医生筛选")
    public Result<PageResult<DoctorSimpleVO>> filterDoctors(@RequestBody @Valid DoctorFilterDTO dto) {
        PageResult<DoctorSimpleVO> pageResult = doctorService.filterDoctors(dto);
        return Result.success(pageResult);
    }

    /**
     * 按科室查询医生
     * 接口路径：GET /api/doctor/department/{deptId}
     * 是否需要登录：否
     *
     * @param deptId 科室ID
     * @return 该科室下的所有医生
     */
    @GetMapping("/department/{deptId}")
    @ApiOperation("按科室查询医生")
    public Result<List<DoctorSimpleVO>> getDoctorsByDepartment(@PathVariable Long deptId) {
        List<DoctorSimpleVO> doctors = doctorService.getDoctorsByDepartment(deptId);
        return Result.success(doctors);
    }

    /**
     * 按医院查询医生
     * 接口路径：GET /api/doctor/hospital/{hospitalId}
     * 是否需要登录：否
     *
     * @param hospitalId 医院ID
     * @return 该医院下的所有医生
     */
    @GetMapping("/hospital/{hospitalId}")
    @ApiOperation("按医院查询医生")
    public Result<List<DoctorSimpleVO>> getDoctorsByHospital(@PathVariable Long hospitalId) {
        List<DoctorSimpleVO> doctors = doctorService.getDoctorsByHospital(hospitalId);
        return Result.success(doctors);
    }
}
