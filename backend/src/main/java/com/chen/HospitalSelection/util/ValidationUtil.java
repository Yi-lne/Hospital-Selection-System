package com.chen.HospitalSelection.util;

import java.util.regex.Pattern;

/**
 * 数据校验工具类
 * 提供常用的数据验证方法
 */
public class ValidationUtil {

    /**
     * 手机号正则表达式
     */
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$";

    /**
     * 密码正则表达式（至少8位，包含大小写字母、数字和特殊字符）
     */
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$";

    /**
     * 邮箱正则表达式
     */
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";

    /**
     * 身份证号正则表达式
     */
    private static final String ID_CARD_REGEX = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[\\dXx]$";

    /**
     * 校验手机号
     *
     * @param phone 手机号
     * @return true-有效，false-无效
     */
    public static boolean validatePhone(String phone) {
        if (phone == null || phone.isEmpty()) {
            return false;
        }
        return Pattern.matches(PHONE_REGEX, phone);
    }

    /**
     * 校验密码
     * 至少8位，包含大小写字母、数字和特殊字符
     *
     * @param password 密码
     * @return true-有效，false-无效
     */
    public static boolean validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return false;
        }
        return Pattern.matches(PASSWORD_REGEX, password);
    }

    /**
     * 校验邮箱
     *
     * @param email 邮箱
     * @return true-有效，false-无效
     */
    public static boolean validateEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return Pattern.matches(EMAIL_REGEX, email);
    }

    /**
     * 校验身份证号
     *
     * @param idCard 身份证号
     * @return true-有效，false-无效
     */
    public static boolean validateIdCard(String idCard) {
        if (idCard == null || idCard.isEmpty()) {
            return false;
        }
        return Pattern.matches(ID_CARD_REGEX, idCard);
    }

    /**
     * 校验字符串是否为空
     *
     * @param str 字符串
     * @return true-为空，false-不为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    /**
     * 校验字符串是否不为空
     *
     * @param str 字符串
     * @return true-不为空，false-为空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 校验对象是否为空
     *
     * @param obj 对象
     * @return true-为空，false-不为空
     */
    public static boolean isEmpty(Object obj) {
        return obj == null;
    }

    /**
     * 校验数字范围
     *
     * @param value 数字
     * @param min   最小值
     * @param max   最大值
     * @return true-在范围内，false-不在范围内
     */
    public static boolean isInRange(Integer value, int min, int max) {
        if (value == null) {
            return false;
        }
        return value >= min && value <= max;
    }

    /**
     * 校验字符串长度
     *
     * @param str  字符串
     * @param min  最小长度
     * @param max  最大长度
     * @return true-长度符合，false-长度不符合
     */
    public static boolean isValidLength(String str, int min, int max) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        return length >= min && length <= max;
    }
}
