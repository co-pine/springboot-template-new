package com.pine.backup.constant;

/**
 * @author pine
 */
public interface RedisKeyConstant {

    // IP 和 sessionId 的 key 前缀
    String USER_EXTRA_INFO = "user:extra:";
    String DEFAULT_NAMESPACE = "spring:session";
    // Spring Session 中 session 信息的后缀（sessionId 前面）
    String SESSION_KEY_POSTFIX = "sessions";
    // session 中保存的属性的前缀
    String SESSION_ATTRIBUTE_PREFIX = "sessionAttr";


    String IP = "ip";

    String SESSION_ID = "sessionId";
}
