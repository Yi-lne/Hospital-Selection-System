package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.dto.DepartmentCreateDTO;
import com.chen.HospitalSelection.dto.DepartmentUpdateDTO;
import com.chen.HospitalSelection.dto.DoctorCreateDTO;
import com.chen.HospitalSelection.dto.DoctorUpdateDTO;
import com.chen.HospitalSelection.dto.HospitalCreateDTO;
import com.chen.HospitalSelection.dto.HospitalUpdateDTO;
import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.service.AdminHospitalService;
import com.chen.HospitalSelection.service.RoleService;
import com.chen.HospitalSelection.util.JwtUtil;
import com.chen.HospitalSelection.vo.HospitalVO;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员医院管理接口
 * 基础路径：/api/admin
 *
 * @author chen
 * @since 2025-02-07
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@Api(tags = "管理员-医院管理")
public class AdminHospitalController {

    @Autowired
    private AdminHospitalService adminHospitalService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RoleService roleService;

    // ==================== 医院管理 ====================

    /**
     * 添加医院
     * 接口路径：POST /api/admin/hospital
     * 是否需要登录：是（管理员）
     *
     * @param dto 医院信息
     * @return 创建的医院信息
     */
    @PostMapping("/hospital")
    @ApiOperation("添加医院")
    public Result<HospitalVO> createHospital(@RequestBody @Valid HospitalCreateDTO dto,
                                            HttpServletRequest request) {
        checkAdminPermission(request);
        HospitalVO hospital = adminHospitalService.createHospital(dto);
        return Result.success(hospital, "医院添加成功");
    }

    /**
     * 修改医院
     * 接口路径：PUT /api/admin/hospital/{id}
     * 是否需要登录：是（管理员）
     *
     * @param id  医院ID
     * @param dto 更新的医院信息
     * @return 更新结果
     */
    @PutMapping("/hospital/{id}")
    @ApiOperation("修改医院")
    public Result<Void> updateHospital(@PathVariable Long id,
                                      @RequestBody @Valid HospitalUpdateDTO dto,
                                      HttpServletRequest request) {
        checkAdminPermission(request);
        adminHospitalService.updateHospital(id, dto);
        return Result.success(null, "医院信息更新成功");
    }

    /**
     * 删除医院（逻辑删除，级联删除科室和医生）
     * 接口路径：DELETE /api/admin/hospital/{id}
     * 是否需要登录：是（管理员）
     *
     * @param id 医院ID
     * @return 删除结果
     */
    @DeleteMapping("/hospital/{id}")
    @ApiOperation("删除医院")
    public Result<Void> deleteHospital(@PathVariable Long id,
                                      HttpServletRequest request) {
        checkAdminPermission(request);
        adminHospitalService.deleteHospital(id);
        return Result.success(null, "医院删除成功");
    }

    /**
     * 恢复已删除的医院
     * 接口路径：POST /api/admin/hospital/{id}/restore
     * 是否需要登录：是（管理员）
     *
     * @param id 医院ID
     * @return 恢复结果
     */
    @PostMapping("/hospital/{id}/restore")
    @ApiOperation("恢复医院")
    public Result<Void> restoreHospital(@PathVariable Long id,
                                        HttpServletRequest request) {
        checkAdminPermission(request);
        adminHospitalService.restoreHospital(id);
        return Result.success(null, "医院恢复成功");
    }

    /**
     * 获取所有医院（包括已删除）
     * 接口路径：GET /api/admin/hospitals
     * 是否需要登录：是（管理员）
     *
     * @param includeDeleted 是否包含已删除的医院
     * @param dto 分页查询参数
     * @return 医院分页列表
     */
    @GetMapping("/hospitals")
    @ApiOperation("获取所有医院")
    public Result<PageResult<HospitalVO>> getAllHospitals(
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @Valid PageQueryDTO dto,
            HttpServletRequest request) {
        checkAdminPermission(request);
        PageResult<HospitalVO> result = adminHospitalService.getAllHospitals(includeDeleted, dto);
        return Result.success(result);
    }

    /**
     * 搜索医院
     * 接口路径：GET /api/admin/hospitals/search
     * 是否需要登录：是（管理员）
     *
     * @param keyword 医院名称关键词
     * @return 搜索结果
     */
    @GetMapping("/hospitals/search")
    @ApiOperation("搜索医院")
    public Result<List<HospitalVO>> searchHospitals(@RequestParam String keyword,
                                                     HttpServletRequest request) {
        checkAdminPermission(request);
        List<HospitalVO> result = adminHospitalService.searchHospitals(keyword);
        return Result.success(result);
    }

    // ==================== 科室管理 ====================

    /**
     * 添加科室
     * 接口路径：POST /api/admin/department
     * 是否需要登录：是（管理员）
     *
     * @param dto 科室信息
     * @return 科室ID
     */
    @PostMapping("/department")
    @ApiOperation("添加科室")
    public Result<Long> createDepartment(@RequestBody @Valid DepartmentCreateDTO dto,
                                         HttpServletRequest request) {
        checkAdminPermission(request);
        Long deptId = adminHospitalService.createDepartment(dto);
        return Result.success(deptId, "科室添加成功");
    }

    /**
     * 批量添加科室
     * 接口路径：POST /api/admin/department/batch
     * 是否需要登录：是（管理员）
     *
     * @param hospitalId 医院ID
     * @param deptNames 科室名称列表
     * @return 添加成功的科室数量
     */
    @PostMapping("/department/batch")
    @ApiOperation("批量添加科室")
    public Result<Map<String, Object>> batchCreateDepartments(
            @RequestParam Long hospitalId,
            @RequestBody List<String> deptNames,
            HttpServletRequest request) {
        checkAdminPermission(request);
        int count = adminHospitalService.batchCreateDepartments(hospitalId, deptNames);

        Map<String, Object> result = new HashMap<>();
        result.put("hospitalId", hospitalId);
        result.put("count", count);

        return Result.success(result, "批量添加科室完成，成功添加" + count + "个科室");
    }

    /**
     * 更新科室
     * 接口路径：PUT /api/admin/department/{id}
     * 是否需要登录：是（管理员）
     *
     * @param id  科室ID
     * @param dto 更新的科室信息
     * @return 更新结果
     */
    @PutMapping("/department/{id}")
    @ApiOperation("更新科室")
    public Result<Void> updateDepartment(@PathVariable Long id,
                                         @RequestBody @Valid DepartmentUpdateDTO dto,
                                         HttpServletRequest request) {
        checkAdminPermission(request);
        adminHospitalService.updateDepartment(id, dto);
        return Result.success(null, "科室信息更新成功");
    }

    /**
     * 删除科室
     * 接口路径：DELETE /api/admin/department/{id}
     * 是否需要登录：是（管理员）
     *
     * @param id 科室ID
     * @return 删除结果
     */
    @DeleteMapping("/department/{id}")
    @ApiOperation("删除科室")
    public Result<Void> deleteDepartment(@PathVariable Long id,
                                        HttpServletRequest request) {
        checkAdminPermission(request);
        adminHospitalService.deleteDepartment(id);
        return Result.success(null, "科室删除成功");
    }

    // ==================== 医生管理 ====================

    /**
     * 添加医生
     * 接口路径：POST /api/admin/doctor
     * 是否需要登录：是（管理员）
     *
     * @param dto 医生信息
     * @return 医生ID
     */
    @PostMapping("/doctor")
    @ApiOperation("添加医生")
    public Result<Long> createDoctor(@RequestBody @Valid DoctorCreateDTO dto,
                                     HttpServletRequest request) {
        checkAdminPermission(request);
        Long doctorId = adminHospitalService.createDoctor(dto);
        return Result.success(doctorId, "医生添加成功");
    }

    /**
     * 批量导入医生
     * 接口路径：POST /api/admin/doctor/import
     * 是否需要登录：是（管理员）
     *
     * @param file Excel文件
     * @return 导入结果统计
     */
    @PostMapping("/doctor/import")
    @ApiOperation("批量导入医生")
    public Result<Map<String, Object>> importDoctors(@RequestParam("file") MultipartFile file,
                                                      HttpServletRequest request) {
        checkAdminPermission(request);
        Map<String, Object> result = adminHospitalService.importDoctors(file);
        return Result.success(result);
    }

    /**
     * 删除医生
     * 接口路径：DELETE /api/admin/doctor/{id}
     * 是否需要登录：是（管理员）
     *
     * @param doctorId 医生ID
     * @return 删除结果
     */
    @DeleteMapping("/doctor/{id}")
    @ApiOperation("删除医生")
    public Result<Void> deleteDoctor(@PathVariable Long id,
                                     HttpServletRequest request) {
        checkAdminPermission(request);
        adminHospitalService.deleteDoctor(id);
        return Result.success(null, "医生删除成功");
    }

    /**
     * 更新医生
     * 接口路径：PUT /api/admin/doctor/{id}
     * 是否需要登录：是（管理员）
     *
     * @param id 医生ID
     * @param dto 更新的医生信息
     * @return 更新结果
     */
    @PutMapping("/doctor/{id}")
    @ApiOperation("更新医生")
    public Result<Void> updateDoctor(@PathVariable Long id,
                                     @RequestBody @Valid DoctorUpdateDTO dto,
                                     HttpServletRequest request) {
        checkAdminPermission(request);
        adminHospitalService.updateDoctor(id, dto);
        return Result.success(null, "医生信息更新成功");
    }

    // ==================== 权限检查 ====================

    /**
     * 检查管理员权限
     */
    private void checkAdminPermission(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }

            // 检查用户是否登录
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                throw new RuntimeException("用户未登录");
            }

            // 检查用户是否为管理员角色
            boolean isAdmin = roleService.isAdmin(userId);
            if (!isAdmin) {
                log.warn("用户{}尝试访问管理员接口，但无管理员权限", userId);
                throw new RuntimeException("无管理员权限");
            }

            log.debug("管理员操作，用户ID：{}", userId);
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("权限验证失败，请重新登录");
        }
    }

    /**
     * 获取当前登录用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
            }
            Long userId = jwtUtil.getUserIdFromToken(token);
            if (userId == null) {
                throw new RuntimeException("用户未登录");
            }
            return userId;
        } catch (Exception e) {
            throw new RuntimeException("获取用户信息失败，请重新登录");
        }
    }
}