package com.chen.HospitalSelection.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理器
 * 统一处理所有异常，返回标准格式的错误响应
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     * 统一返回HTTP 200，通过响应体中的code区分业务状态
     *
     * @param e       业务异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException e, HttpServletRequest request) {
        logger.warn("业务异常: {} - {}", e.getCode(), e.getMessage());

        Map<String, Object> result = new HashMap<>();
        result.put("code", Integer.valueOf(e.getCode()));  // 转换为Integer
        result.put("message", e.getMessage());
        result.put("data", null);

        // 统一返回HTTP 200，通过响应体中的code区分业务状态
        return ResponseEntity.ok().body(result);
    }

    /**
     * 处理认证授权异常
     *
     * @param e       认证授权异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<Map<String, Object>> handleAuthException(AuthException e, HttpServletRequest request) {
        logger.warn("认证授权异常: {} - {}", e.getCode(), e.getMessage());

        Map<String, Object> result = new HashMap<>();
        result.put("code", Integer.valueOf(e.getCode()));
        result.put("message", e.getMessage());
        result.put("data", null);

        // 统一返回HTTP 200
        return ResponseEntity.ok().body(result);
    }

    /**
     * 处理参数异常
     *
     * @param e       参数异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(ParameterException.class)
    public ResponseEntity<Map<String, Object>> handleParameterException(ParameterException e, HttpServletRequest request) {
        logger.warn("参数异常: {} - {}", e.getCode(), e.getMessage());

        Map<String, Object> result = new HashMap<>();
        result.put("code", Integer.valueOf(e.getCode()));
        result.put("message", e.getMessage());
        result.put("data", null);

        // 统一返回HTTP 200
        return ResponseEntity.ok().body(result);
    }

    /**
     * 处理资源未找到异常
     *
     * @param e       资源未找到异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFoundException(ResourceNotFoundException e, HttpServletRequest request) {
        logger.warn("资源未找到异常: {} - {}", e.getCode(), e.getMessage());

        Map<String, Object> result = new HashMap<>();
        result.put("code", Integer.valueOf(e.getCode()));
        result.put("message", e.getMessage());
        result.put("data", null);

        // 统一返回HTTP 200，通过响应体中的code区分业务状态
        return ResponseEntity.ok().body(result);
    }

    /**
     * 处理参数校验异常（@Valid注解）
     *
     * @param e       参数校验异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException e, HttpServletRequest request) {
        logger.warn("参数校验异常: {}", e.getMessage());

        Map<String, Object> result = new HashMap<>();
        result.put("code", 400);

        // 获取第一个错误信息
        FieldError fieldError = e.getBindingResult().getFieldError();
        if (fieldError != null) {
            result.put("message", fieldError.getDefaultMessage());
        } else {
            result.put("message", "参数校验失败");
        }

        result.put("data", null);

        // 统一返回HTTP 200
        return ResponseEntity.ok().body(result);
    }

    /**
     * 处理绑定异常
     *
     * @param e       绑定异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, Object>> handleBindException(BindException e, HttpServletRequest request) {
        logger.warn("绑定异常: {}", e.getMessage());

        Map<String, Object> result = new HashMap<>();
        result.put("code", 400);

        // 获取第一个错误信息
        FieldError fieldError = e.getFieldError();
        if (fieldError != null) {
            result.put("message", fieldError.getDefaultMessage());
        } else {
            result.put("message", "参数绑定失败");
        }

        result.put("data", null);

        // 统一返回HTTP 200
        return ResponseEntity.ok().body(result);
    }

    /**
     * 处理空指针异常
     *
     * @param e       空指针异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<Map<String, Object>> handleNullPointerException(NullPointerException e, HttpServletRequest request) {
        logger.error("空指针异常: ", e);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "系统内部错误");
        result.put("data", null);

        // 统一返回HTTP 200
        return ResponseEntity.ok().body(result);
    }

    /**
     * 处理其他未知异常
     *
     * @param e       异常
     * @param request HTTP请求
     * @return 错误响应
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleException(Exception e, HttpServletRequest request) {
        logger.error("系统异常: ", e);

        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("message", "系统内部错误: " + e.getMessage());
        result.put("data", null);

        // 统一返回HTTP 200
        return ResponseEntity.ok().body(result);
    }
}
