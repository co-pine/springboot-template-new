package com.pine.backup.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 基础响应
 * 通用返回类
 *
 * @param <T>
 * @author pine
 * @date 2024/02/04
 */
@Data
public class BaseResponse<T> implements Serializable {

    private int code;

    private T data;

    private String message;

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(int code, T data) {
        this(code, data, "");
    }

    public BaseResponse(ErrorCode errorCode) {
        this(errorCode.getCode(), null, errorCode.getMessage());
    }
}
