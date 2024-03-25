package com.pine.backup.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pine.backup.model.entity.Backup;

import java.util.Date;
import java.util.List;

/**
 * 帖子数据库操作
 *
* @author pine
 */
public interface BackupMapper extends BaseMapper<Backup> {

    /**
     * 查询帖子列表（包括已被删除的数据）
     */
    List<Backup> listPostWithDelete(Date minUpdateTime);

}




