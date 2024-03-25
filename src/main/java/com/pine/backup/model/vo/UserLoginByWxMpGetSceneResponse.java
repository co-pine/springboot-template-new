package com.pine.backup.model.vo;

import lombok.Data;

/**
 * 用户微信公众号场景码响应
 *
* @author pine
 */
@Data
public class UserLoginByWxMpGetSceneResponse {

    private String qrCode;

    private String scene;
}
