# 数据库初始化

-- 创建库
create database if not exists pine_backup;

-- 切换库
use pine_backup;

-- 用户表
create table if not exists user
(
    id            bigint auto_increment comment 'id' primary key,
    userAccount   varchar(256)                           null comment '账号',
    userPassword  varchar(512)                           null comment '密码',
    unionId       varchar(256)                           null comment '微信开放平台id',
    mpOpenId      varchar(256)                           null comment '公众号openId',
    wxAppOpenId   varchar(256)                           null comment '微信小程序openId',
    userName      varchar(256)                           null comment '用户昵称',
    userAvatar    varchar(1024)                          null comment '用户头像',
    userProfile   varchar(512)                           null comment '用户简介',
    userRole      varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    scene         varchar(128)                           null comment '场景码',
    vipExpireTime datetime                               null comment '会员过期时间',
    createTime    datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime    datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete      tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
    ) comment '用户' collate = utf8mb4_unicode_ci;

-- 帖子表
create table if not exists post
(
    id          bigint auto_increment comment 'id' primary key,
    title       varchar(512)                       null comment '标题',
    content     text                               null comment '内容',
    contentType int      default 0                 null comment '内容格式（0 普通文本  1 md  2 富文本）',
    tags        varchar(1024)                      null comment '标签列表（json 数组）',
    viewNum     int      default 0                 not null comment '浏览量',
    thumbNum    int      default 0                 not null comment '点赞数',
    favourNum   int      default 0                 not null comment '收藏数',
    priority    int                                null comment '优先级 999 精选',
    userId      bigint                             not null comment '创建用户 id',
    editTime    datetime default CURRENT_TIMESTAMP not null comment '编辑时间',
    createTime  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
    ) comment '帖子' collate = utf8mb4_unicode_ci;
