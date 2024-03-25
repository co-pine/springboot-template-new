package com.pine.backup.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpDefaultConfigImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 微信开放平台配置
 *
 * @author pine
 */
@Slf4j
@Configuration
@ConfigurationProperties(prefix = "wx.open")
@Data
public class WxOpenConfig {

    private String appId;

    private String appSecret;

    private WxMpService wxMpService;

    /**
     * 获取 wxmp 服务
     *
     * @return {@link WxMpService}
     */
    @Bean
    public WxMpService getWxMpService() {
        WxMpDefaultConfigImpl config = new WxMpDefaultConfigImpl();
        config.setAppId(appId);
        config.setSecret(appSecret);
        WxMpService service = new WxMpServiceImpl();
        service.setWxMpConfigStorage(config);
        wxMpService = service;
        return wxMpService;
    }
}