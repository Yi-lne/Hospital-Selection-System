package com.chen.HospitalSelection.controller;

import com.chen.HospitalSelection.dto.PasswordResetDTO;
import com.chen.HospitalSelection.dto.PasswordUpdateDTO;
import com.chen.HospitalSelection.dto.UserLoginDTO;
import com.chen.HospitalSelection.dto.UserRegisterDTO;
import com.chen.HospitalSelection.dto.UserUpdateDTO;
import com.chen.HospitalSelection.service.UserService;
import com.chen.HospitalSelection.util.FileUploadUtil;
import com.chen.HospitalSelection.util.JwtUtil;
import com.chen.HospitalSelection.vo.Result;
import com.chen.HospitalSelection.vo.UserProfileVO;
import com.chen.HospitalSelection.vo.UserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户相关接口
 * 基础路径：/api/user
 *
 * @author chen
 */
@RestController
@RequestMapping("/user")
@Api(tags = "用户管理")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private com.chen.HospitalSelection.util.JwtUtil jwtUtil;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    /**
     * 用户注册
     * 接口路径：POST /api/user/register
     * 是否需要登录：否
     *
     * @param dto 注册信息（手机号+密码）
     * @return 注册成功的用户信息和JWT Token（自动登录）
     */
    @PostMapping("/register")
    @ApiOperation("用户注册")
    public Result<Map<String, Object>> register(@RequestBody @Valid UserRegisterDTO dto) {
        // 1. 调用service注册
        UserVO userVO = userService.register(dto);

        // 2. 生成token（注册成功自动登录）
        String token = jwtUtil.generateToken(userVO.getId(), userVO.getPhone());

        // 3. 构造返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", userVO);

        return Result.success(data, "注册成功");
    }

    /**
     * 用户登录
     * 接口路径：POST /api/user/login
     * 是否需要登录：否
     *
     * @param dto 登录信息（手机号+密码）
     * @return 登录成功的用户信息和JWT Token
     */
    @PostMapping("/login")
    @ApiOperation("用户登录")
    public Result<Map<String, Object>> login(@RequestBody @Valid UserLoginDTO dto,
                                              HttpServletRequest request) {
        // 1. 调用service登录
        UserVO userVO = userService.login(dto);

        // 2. 重新生成token（service中已生成，这里重新生成用于返回）
        String token = jwtUtil.generateToken(userVO.getId(), userVO.getPhone());

        // 3. 构造返回数据
        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("userInfo", userVO);

        return Result.success(data, "登录成功");
    }

    /**
     * 用户登出
     * 接口路径：POST /api/user/logout
     * 是否需要登录：是
     *
     * @return 登出结果
     */
    @PostMapping("/logout")
    @ApiOperation("用户登出")
    public Result<Void> logout() {
        // JWT是无状态认证，登出由客户端删除token即可
        // 服务端可以维护token黑名单（如果需要的话），这里暂不实现
        return Result.success(null, "登出成功");
    }

    /**
     * 获取当前登录用户信息
     * 接口路径：GET /api/user/info
     * 是否需要登录：是
     *
     * @return 用户详细信息
     */
    @GetMapping("/info")
    @ApiOperation("获取用户信息")
    public Result<UserProfileVO> getUserInfo(HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        UserProfileVO userProfileVO = userService.getUserProfile(userId);
        return Result.success(userProfileVO);
    }

    /**
     * 更新用户信息
     * 接口路径：PUT /api/user/info
     * 是否需要登录：是
     *
     * @param dto 更新的用户信息（昵称、头像、性别等）
     * @return 更新后的用户信息
     */
    @PutMapping("/info")
    @ApiOperation("更新用户信息")
    public Result<UserVO> updateUserInfo(@RequestBody @Valid UserUpdateDTO dto,
                                        HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        UserVO userVO = userService.updateUserInfo(userId, dto);
        return Result.success(userVO, "更新成功");
    }

    /**
     * 修改密码
     * 接口路径：PUT /api/user/password
     * 是否需要登录：是
     *
     * @param dto 密码信息（原密码+新密码）
     * @return 修改结果
     */
    @PutMapping("/password")
    @ApiOperation("修改密码")
    public Result<Void> updatePassword(@RequestBody @Valid PasswordUpdateDTO dto,
                                      HttpServletRequest request) {
        Long userId = getCurrentUserId(request);
        userService.updatePassword(userId, dto);
        return Result.success(null, "密码修改成功");
    }

    /**
     * 找回密码
     * 接口路径：POST /api/user/password/reset
     * 是否需要登录：否
     *
     * @param dto 找回密码信息（手机号+验证码+新密码）
     * @return 重置结果
     */
    @PostMapping("/password/reset")
    @ApiOperation("找回密码")
    public Result<Void> resetPassword(@RequestBody @Valid PasswordResetDTO dto) {
        userService.resetPassword(dto);
        return Result.success(null, "密码重置成功");
    }

    /**
     * 上传头像
     * 接口路径：POST /api/user/avatar
     * 是否需要登录：是
     *
     * @param file 头像图片文件
     * @return 头像URL
     */
    @PostMapping("/avatar")
    @ApiOperation("上传头像")
    public Result<UserVO> uploadAvatar(@RequestParam("file") MultipartFile file,
                                      HttpServletRequest request) {
        Long userId = getCurrentUserId(request);

        try {
            // 上传文件并获取访问URL
            String avatarUrl = fileUploadUtil.uploadImage(file, "avatar");

            // 更新用户头像
            UserVO userVO = userService.uploadAvatar(userId, avatarUrl);

            return Result.success(userVO, "头像上传成功");
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            return Result.error("头像上传失败：" + e.getMessage());
        }
    }

    /**
     * 获取当前登录用户ID
     *
     * @param request HTTP请求对象
     * @return 用户ID
     */
    private Long getCurrentUserId(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7); // 去掉 "Bearer "
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
