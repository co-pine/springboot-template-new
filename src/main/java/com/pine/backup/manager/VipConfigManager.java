package com.pine.backup.manager;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.pine.backup.model.VipConfig;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * VIP 配置管理
 *
 * @author pine
 */
@Service
public class VipConfigManager {

    private static List<VipConfig> vipConfigList;

    static {
        String json = ResourceUtil.readUtf8Str("biz/vipConfig.json");
        vipConfigList = JSONUtil.toList(json, VipConfig.class);
    }

    /**
     * 根据角色获取 Vip 配置
     *
     * @param role 角色
     * @return {@link VipConfig}
     */
    public VipConfig getVipConfigByRole(String role) {
        if (StringUtils.isBlank(role)) {
            return null;
        }
        for (VipConfig vipConfig : vipConfigList) {
            if (vipConfig.getRole().equals(role)) {
                return vipConfig;
            }
        }
        return null;
    }

}
