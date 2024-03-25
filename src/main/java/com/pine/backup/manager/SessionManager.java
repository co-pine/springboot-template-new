package com.pine.backup.manager;

import cn.dev33.satoken.config.SaTokenConfig;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import com.pine.backup.model.dto.user.UserLoginRedisInfo;
import com.pine.backup.model.entity.User;
import com.pine.backup.service.UserService;
import com.pine.backup.util.RedisKeyUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import static com.pine.backup.constant.UserConstant.USER_LOGIN_STATE;


/**
 * Session 管理器
 *
 * @author pine
 */
@Component
@Slf4j
public class SessionManager {

    // @Value("${spring.session.timeout}")
    // private long sessionTimeout;

    @Lazy
    @Resource
    private UserService userService;

    /**
     * 删除属性
     *
     * @param request 请求信息
     * @param key     键
     */
    public void removeAttribute(HttpServletRequest request, String key) {
        HttpSession session = request.getSession();
        session.removeAttribute(key);
    }

    /**
     * 退出登录
     *
     * @param request 请求信息
     */
    public void logout(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        removeAttribute(request, USER_LOGIN_STATE);
        // stringRedisTemplate.delete(RedisKeyUtil.getUserExtraInfoKey(loginUser.getId()));
    }

    /**
     * 删除其他 session 的登录属性
     *
     * @param sessionId sessionId
     */
    public void removeOtherSessionLoginAttribute(String sessionId, Long userId) {
        String sessionKey = RedisKeyUtil.getSessionKey(sessionId);
        String sessionAttrKey = RedisKeyUtil.getSessionAttrKey(USER_LOGIN_STATE);
        // 删除用户的额外信息
        // Boolean userExtraInfoDelete = stringRedisTemplate.delete(RedisKeyUtil.getUserExtraInfoKey(userId));
        // Long delete = sessionRepository.getSessionRedisOperations().opsForHash().delete(sessionKey, sessionAttrKey);

        // log.info("oldSessionId: {}, user extra info delete result: {}, user login state delete result: {}", sessionId, userExtraInfoDelete, delete);

    }

    /**
     * 登录
     *
     * @param user    用户
     */
    public void login(User user) {
        StpUtil.login(user.getId());
    }
}
