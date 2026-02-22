package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.UserBanDTO;
import com.chen.HospitalSelection.service.AdminUserService;
import com.chen.HospitalSelection.service.RoleService;
import com.chen.HospitalSelection.util.JwtUtil;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.Result;
import com.chen.HospitalSelection.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

/**
 * 管理员用户管理接口
 * 基础路径：/api/admin
 *
 * @author chen
 * @since 2025-02-16
 */
@Slf4j
@RestController
@RequestMapping("/admin")
@Api(tags = "管理员-用户管理")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RoleService roleService;

    // ==================== 用户管理 ====================

    /**
     * 获取所有用户（分页）
     * 接口路径：GET /api/admin/users
     * 是否需要登录：是（管理员）
     *
     * @param dto 分页查询参数
     * @return 用户分页列表
     */
    @GetMapping("/users")
    @ApiOperation("获取所有用户")
    public Result<PageResult<UserVO>> getAllUsers(@Valid PageQueryDTO dto,
                                                   HttpServletRequest request) {
        checkAdminPermission(request);
        PageResult<UserVO> result = adminUserService.getAllUsers(dto);
        return Result.success(result);
    }

    /**
     * 搜索用户
     * 接口路径：GET /api/admin/users/search
     * 是否需要登录：是（管理员）
     *
     * @param keyword 搜索关键词（手机号或昵称）
     * @return 搜索结果
     */
    @GetMapping("/users/search")
    @ApiOperation("搜索用户")
    public Result<List<UserVO>> searchUsers(@RequestParam String keyword,
                                             HttpServletRequest request) {
        checkAdminPermission(request);
        List<UserVO> result = adminUserService.searchUsers(keyword);
        return Result.success(result);
    }

    /**
     * 封禁用户
     * 接口路径：PUT /api/admin/user/{id}/ban
     * 是否需要登录：是（管理员）
     *
     * @param id 用户ID
     * @param dto 封禁参数（时长、原因）
     * @return 封禁结果
     */
    @PutMapping("/user/{id}/ban")
    @ApiOperation("封禁用户")
    public Result<Void> banUser(@PathVariable Long id,
                                 @RequestBody @Valid UserBanDTO dto,
                                 HttpServletRequest request) {
        checkAdminPermission(request);
        adminUserService.banUser(id, dto);
        return Result.success(null, "用户已封禁");
    }

    /**
     * 解封用户
     * 接口路径：PUT /api/admin/user/{id}/unban
     * 是否需要登录：是（管理员）
     *
     * @param id 用户ID
     * @return 解封结果
     */
    @PutMapping("/user/{id}/unban")
    @ApiOperation("解封用户")
    public Result<Void> unbanUser(@PathVariable Long id,
                                   HttpServletRequest request) {
        checkAdminPermission(request);
        adminUserService.unbanUser(id);
        return Result.success(null, "用户已解封");
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
}
