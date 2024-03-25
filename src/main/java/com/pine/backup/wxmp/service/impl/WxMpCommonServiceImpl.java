package com.pine.backup.wxmp.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.pine.backup.constant.UserConstant;
import com.pine.backup.model.entity.User;
import com.pine.backup.service.UserService;
import com.pine.backup.wxmp.service.WxMpCommonService;
import jakarta.annotation.Resource;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.result.WxMpUser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


/**
 * WxMp 服务实现类
 *
* @author pine
 **/
@Service
public class WxMpCommonServiceImpl implements WxMpCommonService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WxMpCommonServiceImpl.class);

    @Resource
    private UserService userService;

    @Override
    public String writeScene(WxMpXmlMessage wxMpXmlMessage,
                              WxMpService wxMpService)
            throws WxErrorException {
        // 封装请求参数
        WxMpUser wxMpUser = wxMpService.getUserService().userInfo(wxMpXmlMessage.getFromUser());
        String unionId = wxMpUser.getUnionId();
        String mpOpenId = wxMpUser.getOpenId();
        if (StringUtils.isAnyBlank(unionId, mpOpenId)) {
            return null;
        }
        // 校验是否要登录
        String eventKey = wxMpXmlMessage.getEventKey();
        String scene;
        // 扫场景码登录
        if (eventKey.contains("login")) {
            String[] strings = eventKey.split("_");
            scene = strings[strings.length - 1];
        } else {
            // 点击公众号菜单登录
            scene = RandomUtil.randomString(32);
        }
        // 需要登录
        synchronized (unionId.intern()) {
            // 查询用户是否已存在
            QueryWrapper<User> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("unionId", unionId);
            User oldUser = userService.getOne(queryWrapper);
            // 被封号，禁止获取动态码
            if (oldUser != null && UserConstant.BAN_ROLE.equals(oldUser.getUserRole())) {
                LOGGER.info("user unionId = {} try writeScene, but ban", unionId);
                return null;
            }
            boolean result;
            // 用户不存在则创建
            if (oldUser == null) {
                User newUser = getUser(wxMpUser);
                newUser.setScene(scene);
                try {
                    userService.addUser(newUser);
                    result = true;
                } catch (Exception e) {
                    result = false;
                }
            } else {
                // 存在则更新
                User updateUser = new User();
                updateUser.setId(oldUser.getId());
                updateUser.setScene(scene);
                result = userService.updateById(updateUser);
            }
            return result ? scene : null;
        }
    }

    private User getUser(WxMpUser wxMpUser) {
        String unionId = wxMpUser.getUnionId();
        String mpOpenId = wxMpUser.getOpenId();
        User user = new User();
        user.setUnionId(unionId);
        user.setMpOpenId(mpOpenId);
        user.setUserName("项目名" + RandomUtil.randomNumbers(4));
        return user;
    }
}
