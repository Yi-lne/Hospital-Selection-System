package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.RoleDTO;
import com.chen.HospitalSelection.service.RoleService;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.Result;
import com.chen.HospitalSelection.vo.RoleVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 角色管理接口
 * 基础路径：/api/role
 *
 * @author chen
 */
@RestController
@RequestMapping("/role")
@Api(tags = "角色管理")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 获取用户角色列表
     * 接口路径：GET /api/role/user/{userId}
     * 是否需要登录：是（管理员权限）
     *
     * @param userId 用户ID
     * @return 角色编码列表
     */
    @GetMapping("/user/{userId}")
    @ApiOperation("获取用户角色")
    public Result<List<String>> getUserRoles(@PathVariable Long userId) {
        List<String> roles = roleService.getUserRoles(userId);
        return Result.success(roles);
    }

    /**
     * 为用户分配角色
     * 接口路径：POST /api/role/assign
     * 是否需要登录：是（管理员权限）
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 分配结果
     */
    @PostMapping("/assign")
    @ApiOperation("分配角色")
    public Result<Void> assignRole(@RequestParam Long userId, @RequestParam Long roleId) {
        roleService.assignRole(userId, roleId);
        return Result.success(null, "分配成功");
    }

    /**
     * 取消用户角色
     * 接口路径：DELETE /api/role/remove
     * 是否需要登录：是（管理员权限）
     *
     * @param userId 用户ID
     * @param roleId 角色ID
     * @return 取消结果
     */
    @DeleteMapping("/remove")
    @ApiOperation("取消角色")
    public Result<Void> removeRole(@RequestParam Long userId, @RequestParam Long roleId) {
        roleService.removeRole(userId, roleId);
        return Result.success(null, "取消成功");
    }

    /**
     * 检查用户是否为管理员
     * 接口路径：GET /api/role/admin/check/{userId}
     * 是否需要登录：是
     *
     * @param userId 用户ID
     * @return 是否为管理员
     */
    @GetMapping("/admin/check/{userId}")
    @ApiOperation("检查是否为管理员")
    public Result<Boolean> isAdmin(@PathVariable Long userId) {
        boolean admin = roleService.isAdmin(userId);
        return Result.success(admin);
    }

    // ==================== 角色管理功能 ====================

    /**
     * 角色列表（分页）
     * 接口路径：GET /api/role/list
     * 是否需要登录：是（管理员权限）
     *
     * @param dto 分页查询参数
     * @return 角色分页列表
     */
    @GetMapping("/list")
    @ApiOperation("角色列表")
    public Result<PageResult<RoleVO>> getRoleList(@Valid PageQueryDTO dto) {
        PageResult<RoleVO> pageResult = roleService.getRoleList(dto);
        return Result.success(pageResult);
    }

    /**
     * 角色详情
     * 接口路径：GET /api/role/{id}
     * 是否需要登录：是（管理员权限）
     *
     * @param id 角色ID
     * @return 角色详细信息
     */
    @GetMapping("/{id}")
    @ApiOperation("角色详情")
    public Result<RoleVO> getRoleDetail(@PathVariable Long id) {
        RoleVO roleVO = roleService.getRoleDetail(id);
        return Result.success(roleVO);
    }

    /**
     * 添加角色
     * 接口路径：POST /api/role/add
     * 是否需要登录：是（管理员权限）
     *
     * @param dto 角色信息
     * @return 添加后的角色信息
     */
    @PostMapping("/add")
    @ApiOperation("添加角色")
    public Result<RoleVO> addRole(@RequestBody @Valid RoleDTO dto) {
        RoleVO roleVO = roleService.addRole(dto);
        return Result.success(roleVO, "添加成功");
    }

    /**
     * 修改角色
     * 接口路径：PUT /api/role/update/{id}
     * 是否需要登录：是（管理员权限）
     *
     * @param id  角色ID
     * @param dto 角色信息
     * @return 修改后的角色信息
     */
    @PutMapping("/update/{id}")
    @ApiOperation("修改角色")
    public Result<RoleVO> updateRole(@PathVariable Long id, @RequestBody @Valid RoleDTO dto) {
        RoleVO roleVO = roleService.updateRole(id, dto);
        return Result.success(roleVO, "修改成功");
    }

    /**
     * 删除角色
     * 接口路径：DELETE /api/role/{id}
     * 是否需要登录：是（管理员权限）
     *
     * @param id 角色ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    @ApiOperation("删除角色")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success(null, "删除成功");
    }

    /**
     * 所有角色列表
     * 接口路径：GET /api/role/all
     * 是否需要登录：是
     *
     * @return 所有角色列表
     */
    @GetMapping("/all")
    @ApiOperation("所有角色列表")
    public Result<List<RoleVO>> getAllRoles() {
        List<RoleVO> roles = roleService.getAllRoles();
        return Result.success(roles);
    }

    // ==================== 以下功能暂未实现，需要扩展RoleService接口 ====================

    /*
    TODO: 需要在RoleService中添加以下方法：
    - PageResult<RoleVO> getRoleList(PageQueryDTO dto)
    - RoleVO getRoleDetail(Long roleId)
    - RoleVO addRole(RoleDTO dto)
    - RoleVO updateRole(Long roleId, RoleDTO dto)
    - void deleteRole(Long roleId)
    - List<RoleVO> getAllRoles()

    @GetMapping("/list")
    @ApiOperation("角色列表")
    public Result<PageResult<RoleVO>> getRoleList(@Valid PageQueryDTO dto) {
        PageResult<RoleVO> pageResult = roleService.getRoleList(dto);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    @ApiOperation("角色详情")
    public Result<RoleVO> getRoleDetail(@PathVariable Long id) {
        RoleVO roleVO = roleService.getRoleDetail(id);
        return Result.success(roleVO);
    }

    @PostMapping("/add")
    @ApiOperation("添加角色")
    public Result<RoleVO> addRole(@RequestBody Map<String, Object> roleData) {
        RoleVO roleVO = roleService.addRole(roleData);
        return Result.success(roleVO, "添加成功");
    }

    @PutMapping("/update/{id}")
    @ApiOperation("修改角色")
    public Result<RoleVO> updateRole(@PathVariable Long id,
                                      @RequestBody Map<String, Object> roleData) {
        RoleVO roleVO = roleService.updateRole(id, roleData);
        return Result.success(roleVO, "修改成功");
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除角色")
    public Result<Void> deleteRole(@PathVariable Long id) {
        roleService.deleteRole(id);
        return Result.success(null, "删除成功");
    }

    @GetMapping("/all")
    @ApiOperation("所有角色列表")
    public Result<List<RoleVO>> getAllRoles() {
        List<RoleVO> roles = roleService.getAllRoles();
        return Result.success(roles);
    }
    */
}
