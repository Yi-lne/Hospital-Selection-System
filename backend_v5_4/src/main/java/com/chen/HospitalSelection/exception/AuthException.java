package com.chen.HospitalSelection.exception;

/**
 * 认证授权异常
 * 用于处理登录、注册、权限等相关异常
 */
public class AuthException extends BusinessException {

    /**
     * 默认错误码
     */
    private static final String DEFAULT_CODE = "401";

    /**
     * 构造函数
     *
     * @param message 错误信息
     */
    public AuthException(String message) {
        super(DEFAULT_CODE, message);
    }

    /**
     * 构造函数（自定义错误码）
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public AuthException(String code, String message) {
        super(code, message);
    }

    /**
     * 构造函数（包含异常原因）
     *
     * @param message 错误信息
     * @param cause   异常原因
     */
    public AuthException(String message, Throwable cause) {
        super(DEFAULT_CODE, message, cause);
    }

    /**
     * 用户名或密码错误
     */
    public static AuthException usernameOrPasswordError() {
        return new AuthException("用户名或密码错误");
    }

    /**
     * 用户未登录
     */
    public static AuthException notLoggedIn() {
        return new AuthException("用户未登录");
    }

    /**
     * Token无效
     */
    public static AuthException invalidToken() {
        return new AuthException("Token无效或已过期");
    }

    /**
     * Token已过期
     */
    public static AuthException tokenExpired() {
        return new AuthException("Token已过期");
    }

    /**
     * 无权限访问
     */
    public static AuthException accessDenied() {
        return new AuthException("403", "无权限访问");
    }

    /**
     * 用户已存在
     */
    public static AuthException userAlreadyExists() {
        return new AuthException("用户已存在");
    }

    /**
     * 用户不存在
     */
    public static AuthException userNotFound() {
        return new AuthException("用户不存在");
    }

    /**
     * 账号已被禁用
     */
    public static AuthException accountDisabled() {
        return new AuthException("账号已被禁用");
    }

    /**
     * 验证码错误
     */
    public static AuthException verifyCodeError() {
        return new AuthException("验证码错误");
    }

    /**
     * 验证码已过期
     */
    public static AuthException verifyCodeExpired() {
        return new AuthException("验证码已过期");
    }
}
