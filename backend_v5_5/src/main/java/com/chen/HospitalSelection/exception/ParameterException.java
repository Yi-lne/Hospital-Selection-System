package com.chen.HospitalSelection.exception;

/**
 * 参数异常
 * 用于处理参数校验失败等异常
 */
public class ParameterException extends BusinessException {

    /**
     * 默认错误码
     */
    private static final String DEFAULT_CODE = "400";

    /**
     * 构造函数
     *
     * @param message 错误信息
     */
    public ParameterException(String message) {
        super(DEFAULT_CODE, message);
    }

    /**
     * 构造函数（自定义错误码）
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public ParameterException(String code, String message) {
        super(code, message);
    }

    /**
     * 构造函数（包含异常原因）
     *
     * @param message 错误信息
     * @param cause   异常原因
     */
    public ParameterException(String message, Throwable cause) {
        super(DEFAULT_CODE, message, cause);
    }

    /**
     * 参数为空
     */
    public static ParameterException parameterIsNull(String paramName) {
        return new ParameterException(paramName + "不能为空");
    }

    /**
     * 参数格式错误
     */
    public static ParameterException parameterFormatError(String paramName) {
        return new ParameterException(paramName + "格式错误");
    }

    /**
     * 参数无效
     */
    public static ParameterException invalidParameter(String paramName) {
        return new ParameterException("无效的" + paramName);
    }

    /**
     * 参数超出范围
     */
    public static ParameterException parameterOutOfRange(String paramName) {
        return new ParameterException(paramName + "超出范围");
    }

    /**
     * 手机号格式错误
     */
    public static ParameterException phoneFormatError() {
        return new ParameterException("手机号格式错误");
    }

    /**
     * 密码格式错误
     */
    public static ParameterException passwordFormatError() {
        return new ParameterException("密码格式错误，至少8位，包含大小写字母、数字和特殊字符");
    }

    /**
     * 邮箱格式错误
     */
    public static ParameterException emailFormatError() {
        return new ParameterException("邮箱格式错误");
    }

    /**
     * 身份证号格式错误
     */
    public static ParameterException idCardFormatError() {
        return new ParameterException("身份证号格式错误");
    }

    /**
     * 参数长度不符合要求
     */
    public static ParameterException lengthError(String paramName, int min, int max) {
        return new ParameterException(paramName + "长度必须在" + min + "-" + max + "之间");
    }

    /**
     * 缺少必填参数
     */
    public static ParameterException missingRequiredParameter(String paramName) {
        return new ParameterException("缺少必填参数：" + paramName);
    }
}
