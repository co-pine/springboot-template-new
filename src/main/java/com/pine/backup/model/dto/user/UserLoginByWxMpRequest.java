package com.pine.backup.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 *
* @author pine
 */
@Data
public class UserLoginByWxMpRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    /**
     * 登录场景码
     */
    private String scene;
}
