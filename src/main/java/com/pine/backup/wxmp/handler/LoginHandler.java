package com.pine.backup.wxmp.handler;

import com.pine.backup.wxmp.service.WxMpCommonService;
import jakarta.annotation.Resource;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.common.session.WxSessionManager;
import me.chanjar.weixin.mp.api.WxMpMessageHandler;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;
import me.chanjar.weixin.mp.bean.message.WxMpXmlOutMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 登录处理器
 *
* @author pine
 **/
@Component
public class LoginHandler implements WxMpMessageHandler {

    @Resource
    private WxMpCommonService wxMpCommonService;

    @Override
    public WxMpXmlOutMessage handle(WxMpXmlMessage wxMpXmlMessage, Map<String, Object> map, WxMpService wxMpService,
                                    WxSessionManager wxSessionManager) throws WxErrorException {
        // 更新场景码
        String scene = wxMpCommonService.writeScene(wxMpXmlMessage, wxMpService);
        String content;
        if (StringUtils.isBlank(scene)) {
            content = "登录失败，请稍后重试";
        } else {
            // TODO: 2024/1/5 修改
            content = "招呼语";
        }
        // 调用接口，返回验证码
        return WxMpXmlOutMessage.TEXT().content(content)
                .fromUser(wxMpXmlMessage.getToUser())
                .toUser(wxMpXmlMessage.getFromUser())
                .build();
    }
}
