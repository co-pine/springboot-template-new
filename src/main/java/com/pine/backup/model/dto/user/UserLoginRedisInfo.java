package com.pine.backup.model.dto.user;

import com.pine.backup.model.entity.User;
import lombok.Builder;
import lombok.Data;

/**
 * 用户登录 存在 Redis 中的信息（user 单独存储）
 *
 * @author pine
 */
@Data
@Builder
public class UserLoginRedisInfo {

    private User user;

    private String ip;

}
