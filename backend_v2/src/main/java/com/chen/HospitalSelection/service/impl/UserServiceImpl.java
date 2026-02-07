package com.chen.HospitalSelection.service.impl;

import com.chen.HospitalSelection.dto.PageQueryDTO;
import com.chen.HospitalSelection.dto.PasswordResetDTO;
import com.chen.HospitalSelection.dto.PasswordUpdateDTO;
import com.chen.HospitalSelection.dto.UserLoginDTO;
import com.chen.HospitalSelection.dto.UserRegisterDTO;
import com.chen.HospitalSelection.dto.UserUpdateDTO;
import com.chen.HospitalSelection.exception.BusinessException;
import com.chen.HospitalSelection.exception.ParameterException;
import com.chen.HospitalSelection.mapper.RoleMapper;
import com.chen.HospitalSelection.mapper.UserMapper;
import com.chen.HospitalSelection.mapper.UserRoleMapper;
import com.chen.HospitalSelection.model.Role;
import com.chen.HospitalSelection.model.User;
import com.chen.HospitalSelection.model.UserRole;
import com.chen.HospitalSelection.service.SmsService;
import com.chen.HospitalSelection.service.UserService;
import com.chen.HospitalSelection.util.JwtUtil;
import com.chen.HospitalSelection.util.PasswordUtil;
import com.chen.HospitalSelection.vo.PageResult;
import com.chen.HospitalSelection.vo.UserProfileVO;
import com.chen.HospitalSelection.vo.UserVO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 用户服务实现类
 *
 * @author chen
 * @since 2025-01-30
 */
@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SmsService smsService;

    @Autowired
    private UserRoleMapper userRoleMapper;

    @Autowired
    private RoleMapper roleMapper;

    /**
     * 默认头像URL
     */
    private static final String DEFAULT_AVATAR = "http://example.com/default-avatar.png";

    @Override
    @Transactional
    public UserVO register(UserRegisterDTO dto) {
        log.info("用户注册，手机号：{}", dto.getPhone());

        // 1. 检查手机号是否已存在
        User existUser = userMapper.selectByPhone(dto.getPhone());
        if (existUser != null) {
            throw new ParameterException("手机号已被注册");
        }

        // 2. 创建用户对象
        User user = new User();
        user.setPhone(dto.getPhone());
        // 密码加密
        user.setPassword(PasswordUtil.encode(dto.getPassword()));
        // 如果没有昵称，生成随机昵称
        user.setNickname(dto.getNickname() != null ? dto.getNickname() : "用户" + UUID.randomUUID().toString().substring(0, 8));
        user.setAvatar(DEFAULT_AVATAR);
        user.setGender(0); // 默认未知
        user.setStatus(1); // 默认正常
        user.setIsDeleted(0);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());

        // 3. 保存到数据库
        userMapper.insert(user);

        // 3.5. 为新用户分配默认角色（普通用户）
        try {
            Role userRole = roleMapper.selectByCode("user");
            if (userRole != null) {
                UserRole userRoleRelation = new UserRole();
                userRoleRelation.setUserId(user.getId());
                userRoleRelation.setRoleId(userRole.getId());
                userRoleRelation.setCreateTime(LocalDateTime.now());
                userRoleMapper.insert(userRoleRelation);
                log.info("为新用户{}分配默认角色：{}", user.getId(), userRole.getRoleCode());
            } else {
                log.warn("未找到普通用户角色，新用户{}将无角色", user.getId());
            }
        } catch (Exception e) {
            log.error("为新用户分配角色失败：{}", e.getMessage(), e);
            // 不影响注册流程，继续执行
        }

        // 4. 返回用户信息
        return convertToVO(user);
    }

    @Override
    public UserVO login(UserLoginDTO dto) {
        log.info("用户登录，手机号：{}", dto.getPhone());

        // 1. 查询用户
        User user = userMapper.selectByPhone(dto.getPhone());
        if (user == null) {
            throw new ParameterException("手机号或密码错误");
        }

        // 2. 验证密码
        boolean passwordMatch = PasswordUtil.matches(dto.getPassword(), user.getPassword());
        if (!passwordMatch) {
            throw new ParameterException("手机号或密码错误");
        }

        // 3. 检查用户状态
        if (user.getStatus() == 0) {
            throw new BusinessException("账号已被禁用");
        }

        // 4. 生成JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getPhone());

        // 5. 返回用户信息
        UserVO userVO = convertToVO(user);
        // 注意：UserVO没有token字段，token通过响应头或其他方式返回
        // 如果需要在VO中返回token，需要扩展UserVO类

        log.info("用户登录成功，用户ID：{}", user.getId());
        return userVO;
    }

    @Override
    public UserVO getUserInfo(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        return convertToVO(user);
    }

    @Override
    public UserProfileVO getUserProfile(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        UserProfileVO profileVO = new UserProfileVO();
        BeanUtils.copyProperties(user, profileVO);

        // 脱敏处理手机号
        profileVO.setPhone(maskPhone(user.getPhone()));

        // 查询用户角色列表
        List<UserRole> userRoles = userRoleMapper.selectByUserId(userId);
        List<String> roleCodes = new ArrayList<>();
        for (UserRole userRole : userRoles) {
            Role role = roleMapper.selectById(userRole.getRoleId());
            if (role != null && role.getIsDeleted() == 0) {
                roleCodes.add(role.getRoleCode());
            }
        }
        profileVO.setRoles(roleCodes);

        return profileVO;
    }

    @Override
    @Transactional
    public UserVO updateUserInfo(Long userId, UserUpdateDTO dto) {
        log.info("更新用户信息，用户ID：{}", userId);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 更新字段
        if (dto.getNickname() != null) {
            user.setNickname(dto.getNickname());
        }
        if (dto.getAvatar() != null) {
            user.setAvatar(dto.getAvatar());
        }
        if (dto.getGender() != null) {
            user.setGender(dto.getGender());
        }

        user.setUpdateTime(LocalDateTime.now());
        userMapper.updateById(user);

        return convertToVO(user);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, PasswordUpdateDTO dto) {
        log.info("修改密码，用户ID：{}", userId);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        // 验证原密码
        boolean passwordMatch = PasswordUtil.matches(dto.getOldPassword(), user.getPassword());
        if (!passwordMatch) {
            throw new ParameterException("原密码错误");
        }

        // 更新密码
        String newPassword = PasswordUtil.encode(dto.getNewPassword());
        userMapper.updatePassword(userId, newPassword);

        log.info("密码修改成功，用户ID：{}", userId);
    }

    @Override
    public String sendVerificationCode(String phone) {
        log.info("发送验证码，手机号：{}", phone);

        // 调用短信服务发送验证码
        String code = smsService.sendVerificationCode(phone);

        return code;
    }

    @Override
    @Transactional
    public void resetPassword(PasswordResetDTO dto) {
        log.info("找回密码，手机号：{}", dto.getPhone());

        // 根据手机号查询用户
        User user = userMapper.selectByPhone(dto.getPhone());
        if (user == null) {
            throw new BusinessException("该手机号未注册");
        }

        // 验证验证码
        boolean isValid = smsService.verifyCode(dto.getPhone(), dto.getVerificationCode());
        if (!isValid) {
            throw new BusinessException("验证码错误或已过期");
        }

        // 更新密码
        String newPassword = PasswordUtil.encode(dto.getNewPassword());
        userMapper.updatePassword(user.getId(), newPassword);

        log.info("密码重置成功，手机号：{}", dto.getPhone());
    }

    @Override
    public PageResult<UserVO> getUserList(PageQueryDTO dto) {
        log.info("分页查询用户列表，页码：{}，每页大小：{}", dto.getPage(), dto.getPageSize());

        // 使用PageHelper进行物理分页
        PageHelper.startPage(dto.getPage(), dto.getPageSize());
        List<User> userList = userMapper.selectAll();
        PageInfo<User> pageInfo = new PageInfo<>(userList);

        // 转换为VO
        List<UserVO> userVOList = userList.stream()
                .map(this::convertToVO)
                .collect(java.util.stream.Collectors.toList());

        return new PageResult<>(pageInfo.getTotal(), dto.getPage(), dto.getPageSize(), userVOList);
    }

    @Override
    @Transactional
    public void updateUserStatus(Long userId, Integer status) {
        log.info("更新用户状态，用户ID：{}，状态：{}", userId, status);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        userMapper.updateStatus(userId, status);
    }

    @Override
    @Transactional
    public UserVO uploadAvatar(Long userId, String avatarUrl) {
        log.info("上传用户头像，用户ID：{}", userId);

        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }

        userMapper.updateAvatar(userId, avatarUrl);
        user.setAvatar(avatarUrl);

        return convertToVO(user);
    }

    /**
     * 将User实体转换为UserVO
     */
    private UserVO convertToVO(User user) {
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);

        // 脱敏处理手机号
        userVO.setPhone(maskPhone(user.getPhone()));

        // 查询并填充用户角色列表
        if (user.getId() != null) {
            List<UserRole> userRoles = userRoleMapper.selectByUserId(user.getId());
            List<String> roleCodes = new ArrayList<>();
            for (UserRole userRole : userRoles) {
                Role role = roleMapper.selectById(userRole.getRoleId());
                if (role != null && role.getIsDeleted() == 0) {
                    roleCodes.add(role.getRoleCode());
                    log.debug("用户{}拥有角色：{}", user.getId(), role.getRoleCode());
                }
            }
            userVO.setRoles(roleCodes);
            log.info("用户{}的角色列表：{}", user.getId(), roleCodes);
        } else {
            userVO.setRoles(new ArrayList<>());
        }

        return userVO;
    }

    /**
     * 手机号脱敏
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }
}
