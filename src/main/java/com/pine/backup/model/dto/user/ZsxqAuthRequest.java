package com.pine.backup.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 知识星球授权请求
 */
@Data
public class ZsxqAuthRequest implements Serializable {

    private static final long serialVersionUID = -6944826664302147781L;

    private String appId;

    private String groupId;

    private Long userId;

    private String userName;

    private Integer userNumber;

    private String userIcon;

    private String userRole;

    /**
     * 自定义参数
     */
    private String extra;

    private Long joinTime;

    private Long expireTime;

    private Long timestamp;

    private String signature;

    private Integer errorCode;

    /**
     * 业务系统的用户 id
     */
    private Long bizUserId;
}
