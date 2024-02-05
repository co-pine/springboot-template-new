package com.pine.template.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户
 * @TableName user
 */
@TableName(value ="user")
@Data
public class User implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 开放平台id
     */
    private String unionId;

    /**
     * 公众号openId
     */
    private String mpOpenId;

    /**
     * 小程序openId
     */
    private String mnOpenId;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 性别：0-女，1-男
     */
    private Integer gender;

    /**
     * 简介
     */
    private String userProfile;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 用户角色：user/vip/admin/ban
     */
    private String userRole;

    /**
     * 兴趣
     */
    private String interests;

    /**
     * 地区
     */
    private String place;

    /**
     * 生日
     */
    private String birthday;

    /**
     * 工作状态（在校、找实习、实习中、找工作、已工作）
     */
    private String jobStatus;

    /**
     * 主攻方向
     */
    private String direction;

    /**
     * 目标
     */
    private String goal;

    /**
     * github
     */
    private String github;

    /**
     * 博客
     */
    private String blog;

    /**
     * 学校
     */
    private String school;

    /**
     * 专业
     */
    private String major;

    /**
     * 学历
     */
    private String education;

    /**
     * 毕业年份
     */
    private Integer graduationYear;

    /**
     * 公司
     */
    private String company;

    /**
     * 岗位
     */
    private String job;

    /**
     * 工作年限
     */
    private Integer workYear;

    /**
     * 会员过期时间
     */
    private Date vipExpireTime;

    /**
     * 上次登录时间
     */
    private Date lastLoginTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

    @Serial
    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}