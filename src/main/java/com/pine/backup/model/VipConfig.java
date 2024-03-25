package com.pine.backup.model;

import lombok.Data;

/**
 * Vip 配置
 *
 * @author pine
 */
@Data
public class VipConfig {

    /**
     * 角色
     */
    private String role;

    /**
     * 添加次数
     */
    private int addLeftNum;

    /**
     * 最大累积次数
     */
    private int maxLeftNum;

    /**
     * 私有对话邀请人数
     */
    private int maxChatUserNum;

    /**
     * 对话消息保存天数
     */
    private int messageRetainDay;

    /**
     * 下载原图消耗
     */
    private int downloadDrawPictureCost;

    /**
     * 单次回复最大词数
     */
    private int modelMaxTokens = 2048;

    /**
     * 绘画模式（relaxed、fast）
     */
    private String drawMode = "relaxed";

    /**
     * 能否写书
     */
    private boolean canWriteBook = false;
}
