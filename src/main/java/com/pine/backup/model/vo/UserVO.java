package com.pine.backup.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author pine
 */
@Data
@Builder
public class UserVO {

    private String userName;

    private String phone;

    private String userAvatar;

}
