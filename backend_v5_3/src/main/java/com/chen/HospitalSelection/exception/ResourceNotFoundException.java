package com.chen.HospitalSelection.exception;

/**
 * 资源未找到异常
 * 用于处理资源不存在的异常
 */
public class ResourceNotFoundException extends BusinessException {

    /**
     * 默认错误码
     */
    private static final String DEFAULT_CODE = "404";

    /**
     * 构造函数
     *
     * @param message 错误信息
     */
    public ResourceNotFoundException(String message) {
        super(DEFAULT_CODE, message);
    }

    /**
     * 构造函数（自定义错误码）
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public ResourceNotFoundException(String code, String message) {
        super(code, message);
    }

    /**
     * 构造函数（包含异常原因）
     *
     * @param message 错误信息
     * @param cause   异常原因
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(DEFAULT_CODE, message, cause);
    }

    /**
     * 资源未找到
     */
    public static ResourceNotFoundException resourceNotFound(String resourceName) {
        return new ResourceNotFoundException(resourceName + "未找到");
    }

    /**
     * 用户未找到
     */
    public static ResourceNotFoundException userNotFound() {
        return new ResourceNotFoundException("用户不存在");
    }

    /**
     * 医院未找到
     */
    public static ResourceNotFoundException hospitalNotFound() {
        return new ResourceNotFoundException("医院不存在");
    }

    /**
     * 科室未找到
     */
    public static ResourceNotFoundException departmentNotFound() {
        return new ResourceNotFoundException("科室不存在");
    }

    /**
     * 医生未找到
     */
    public static ResourceNotFoundException doctorNotFound() {
        return new ResourceNotFoundException("医生不存在");
    }

    /**
     * 预约未找到
     */
    public static ResourceNotFoundException appointmentNotFound() {
        return new ResourceNotFoundException("预约记录不存在");
    }

    /**
     * 评价未找到
     */
    public static ResourceNotFoundException reviewNotFound() {
        return new ResourceNotFoundException("评价记录不存在");
    }

    /**
     * 消息未找到
     */
    public static ResourceNotFoundException messageNotFound() {
        return new ResourceNotFoundException("消息不存在");
    }

    /**
     * 数据不存在
     */
    public static ResourceNotFoundException dataNotFound(Long id) {
        return new ResourceNotFoundException("ID为" + id + "的数据不存在");
    }
}
