package com.chen.HospitalSelection.exception;

import lombok.Getter;

/**
 * 业务异常基类
 * 所有业务异常的父类
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误码
     */
    private final String code;

    /**
     * 错误信息
     */
    private final String message;

    /**
     * 构造函数
     *
     * @param code    错误码
     * @param message 错误信息
     */
    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数（使用默认错误码）
     *
     * @param message 错误信息
     */
    public BusinessException(String message) {
        this("500", message);
    }

    /**
     * 构造函数（包含异常原因）
     *
     * @param code    错误码
     * @param message 错误信息
     * @param cause   异常原因
     */
    public BusinessException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
    }

    /**
     * 构造函数（包含异常原因，使用默认错误码）
     *
     * @param message 错误信息
     * @param cause   异常原因
     */
    public BusinessException(String message, Throwable cause) {
        this("500", message, cause);
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
