package com.pine.template.common;

/**
 * 返回工具类
 *
 * @author pine
 * @date 2024/02/04
 */
public class ResultUtils {

    /**
     * 成功
     *
     * @param data 数据
     * @return {@link BaseResponse}<{@link T}>
     */
    public static <T> BaseResponse<T> success(T data) {
        return new BaseResponse<>(0, data, "ok");
    }

    /**
     * 错误
     *
     * @param errorCode 错误代码
     * @return {@link BaseResponse}
     */
    public static BaseResponse<Void> error(ErrorCode errorCode) {
        return new BaseResponse<>(errorCode);
    }

    /**
     * 错误
     *
     * @param code    密码
     * @param message 消息
     * @return {@link BaseResponse}<{@link Void}>
     */
    public static BaseResponse<Void> error(int code, String message) {
        return new BaseResponse<>(code, null, message);
    }

    /**
     * 错误
     *
     * @param errorCode 错误代码
     * @param message   消息
     * @return {@link BaseResponse}<{@link Void}>
     */
    public static BaseResponse<Void> error(ErrorCode errorCode, String message) {
        return new BaseResponse<>(errorCode.getCode(), null, message);
    }
}
