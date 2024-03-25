package com.pine.backup.util;


import static com.pine.backup.constant.RedisKeyConstant.*;

/**
 * redis键 工具类
 *
 * @author pine
 * @date 2024/01/03
 */
public class RedisKeyUtil {

    /**
     * 获取已登录用户的 IP 和 sessionId 对应的 key
     *
     * @param userId 用户 id
     * @return {@link String}
     */
    public static String getUserExtraInfoKey(Long userId) {
        return USER_EXTRA_INFO + String.valueOf(userId);
    }


    /**
     * 获取 session 信息对应的 key
     *
     * @param sessionId sessionId
     * @return {@link String}
     */
    public static String getSessionKey(String sessionId) {
        return DEFAULT_NAMESPACE + ":" + SESSION_KEY_POSTFIX + ":" + sessionId;
    }

    /**
     * 获取 session 中某一属性的 key
     *
     * @param attrName 属性名称
     * @return {@link String}
     */
    public static String getSessionAttrKey(String attrName) {
        return SESSION_ATTRIBUTE_PREFIX + ":" + attrName;
    }
}
