-- 创建数据库
create database if not exists template_new;
-- 用户表
create table if not exists template_new.user
(
    id                bigint auto_increment comment 'id' primary key,
    unionId           varchar(256) comment '开放平台id',
    mpOpenId          varchar(256) comment '公众号openId',
    mnOpenId          varchar(256) comment '小程序openId',
    userName          varchar(256) comment '用户昵称',
    userAvatar        varchar(1024) comment '用户头像',
    gender            tinyint comment '性别：0-女，1-男',
    userProfile       varchar(512) comment '简介',
    email             varchar(256) comment '邮箱',
    phone             varchar(128) comment '手机号',
    userRole          varchar(256) default 'user'            not null comment '用户角色：user/vip/admin/ban',
    interests         text comment '兴趣',
    place             varchar(256) comment '地区',
    birthday          varchar(256) comment '生日',
    jobStatus         varchar(256) comment '工作状态（在校、找实习、实习中、找工作、已工作）',
    direction         varchar(256) comment '主攻方向',
    goal              varchar(512) comment '目标',
    github            varchar(512) comment 'github',
    blog              varchar(512) comment '博客',
    school            varchar(256) comment '学校',
    major             varchar(256) comment '专业',
    education         varchar(256) comment '学历',
    graduationYear    int(11) comment '毕业年份',
    company           varchar(256) comment '公司',
    job               varchar(256) comment '岗位',
    workYear          int(11) comment '工作年限',
    vipExpireTime     datetime comment '会员过期时间',
    lastLoginTime     datetime comment '上次登录时间',
    createTime        datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime        datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete          tinyint      default 0                 not null comment '是否删除',
    index idx_unionId (unionId)
    ) comment '用户' collate = utf8mb4_unicode_ci;