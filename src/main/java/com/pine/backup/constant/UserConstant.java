package com.pine.backup.constant;

import com.pine.backup.model.enums.UserRoleEnum;

import java.util.Arrays;
import java.util.List;

/**
 * 用户常量
 *
* @author pine
 */
public interface UserConstant {

    /**
     * 用户登录态键
     */
    String USER_LOGIN_STATE = "user_login";

    /**
     * 系统用户 id
     *  todo 根据实际修改
     */
    long SYSTEM_USER_ID = 1L;

    //  region 权限

    /**
     * 默认权限
     */
    String DEFAULT_ROLE = "user";

    /**
     * VIP
     */
    String VIP_ROLE = "vip";

    /**
     * SVIP
     */
    String SVIP_ROLE = "svip";

    /**
     * 管理员权限
     */
    String ADMIN_ROLE = "admin";

    /**
     * 被封号
     */
    String BAN_ROLE = "ban";

    // endregion

    /**
     * VIP 角色枚举列表
     */
    List<UserRoleEnum> VIP_ROLE_ENUM_LIST = Arrays.asList(
            UserRoleEnum.VIP,
            UserRoleEnum.SVIP
    );
}
