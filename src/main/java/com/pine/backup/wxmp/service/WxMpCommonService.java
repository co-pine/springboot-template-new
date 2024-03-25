package com.pine.backup.wxmp.service;

import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.message.WxMpXmlMessage;

/**
 * WxMp 服务
 *
* @author pine
 **/
public interface WxMpCommonService {

    /**
     * 写场景码（登录）
     *
     * @param wxMpXmlMessage WX MP XML 消息
     * @param wxMpService    WX MP服务
     * @return {@link String}
     * @throws WxErrorException wx 错误异常
     */
    String writeScene(WxMpXmlMessage wxMpXmlMessage, WxMpService wxMpService)
            throws WxErrorException;

}
