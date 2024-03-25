package com.pine.backup.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.pine.backup.model.dto.backup.BackupQueryRequest;
import com.pine.backup.model.entity.Backup;
import com.pine.backup.model.vo.BackupVO;

/**
 * 帖子服务
 *
* @author pine
 */
public interface BackupService extends IService<Backup> {

    /**
     * 校验
     *
     * @param backup
     * @param add
     */
    void validBackup(Backup backup, boolean add);

    /**
     * 获取查询条件
     *
     * @param backupQueryRequest
     * @return
     */
    QueryWrapper<Backup> getQueryWrapper(BackupQueryRequest backupQueryRequest);

    /**
     * 获取帖子封装
     *
     * @param backup
     * @return
     */
    BackupVO getBackupVO(Backup backup);

    /**
     * 分页获取帖子封装
     *
     * @param backupPage
     * @return
     */
    Page<BackupVO> getBackupVOPage(Page<Backup> backupPage);

    void checkOperationAuth(Long backupId);
}
