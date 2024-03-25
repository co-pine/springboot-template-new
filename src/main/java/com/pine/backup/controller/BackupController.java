package com.pine.backup.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.pine.backup.common.BaseResponse;
import com.pine.backup.common.DeleteRequest;
import com.pine.backup.common.ErrorCode;
import com.pine.backup.common.ResultUtils;
import com.pine.backup.exception.BusinessException;
import com.pine.backup.exception.ThrowUtils;
import com.pine.backup.model.dto.backup.BackupAddRequest;
import com.pine.backup.model.dto.backup.BackupEditRequest;
import com.pine.backup.model.dto.backup.BackupQueryRequest;
import com.pine.backup.model.dto.backup.BackupUpdateRequest;
import com.pine.backup.model.entity.Backup;
import com.pine.backup.model.entity.User;
import com.pine.backup.model.vo.BackupVO;
import com.pine.backup.service.BackupService;
import com.pine.backup.util.ThreadLocalUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 帖子接口
 *
* @author pine
 */
@RestController
@RequestMapping("/backup")
@Slf4j
public class BackupController {

    @Resource
    private BackupService backupService;

    // region 增删改查

    /**
     * 创建
     *
     * @param backupAddRequest backup 添加请求
     * @return {@link BaseResponse}<{@link Long}>
     */
    @PostMapping("/add")
    public BaseResponse<Long> addBackup(@RequestBody BackupAddRequest backupAddRequest) {
        if (backupAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Backup backup = new Backup();
        BeanUtils.copyProperties(backupAddRequest, backup);
        List<String> tags = backupAddRequest.getTags();
        if (tags != null) {
            backup.setTags(JSONUtil.toJsonStr(tags));
        }
        backupService.validBackup(backup, true);
        User loginUser = ThreadLocalUtil.getLoginUser();
        backup.setUserId(loginUser.getId());
        backup.setFavourNum(0);
        backup.setThumbNum(0);
        boolean result = backupService.save(backup);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newBackupId = backup.getId();
        return ResultUtils.success(newBackupId);
    }

    /**
     * 删除
     *
     * @param deleteRequest 删除请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteBackup(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Long id = deleteRequest.getId();
        backupService.checkOperationAuth(id);
        boolean b = backupService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param backupUpdateRequest backup 更新请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateBackup(@RequestBody BackupUpdateRequest backupUpdateRequest) {
        if (backupUpdateRequest == null || backupUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Backup backup = new Backup();
        BeanUtils.copyProperties(backupUpdateRequest, backup);
        List<String> tags = backupUpdateRequest.getTags();
        if (tags != null) {
            backup.setTags(JSONUtil.toJsonStr(tags));
        }
        // 参数校验
        backupService.validBackup(backup, false);
        long id = backupUpdateRequest.getId();
        // 判断是否存在
        Backup oldBackup = backupService.getById(id);
        ThrowUtils.throwIf(oldBackup == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = backupService.updateById(backup);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取帖子（仅管理员）
     *
     * @param id 编号
     * @return {@link BaseResponse}<{@link Backup}>
     */
    @GetMapping("/get")
    public BaseResponse<Backup> getBackupById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Backup backup = backupService.getById(id);
        if (backup == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(backup);
    }

    /**
     * 根据 id 获取
     *
     * @param id id
     * @return {@link BaseResponse}<{@link BackupVO}>
     */
    @GetMapping("/get/vo")
    public BaseResponse<BackupVO> getBackupVOById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Backup backup = backupService.getById(id);
        if (backup == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(backupService.getBackupVO(backup));
    }

    /**
     * 分页获取帖子列表（仅管理员）
     *
     * @param backupQueryRequest backup 查询请求
     * @return {@link BaseResponse}<{@link Page}<{@link Backup}>>
     */
    @PostMapping("/list/page")
    public BaseResponse<Page<Backup>> listBackupByPage(@RequestBody BackupQueryRequest backupQueryRequest) {
        long current = backupQueryRequest.getCurrent();
        long size = backupQueryRequest.getPageSize();
        Page<Backup> backupPage = backupService.page(new Page<>(current, size), backupService.getQueryWrapper(backupQueryRequest));
        return ResultUtils.success(backupPage);
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param backupQueryRequest backup 查询请求
     * @return {@link BaseResponse}<{@link Page}<{@link BackupVO}>>
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<BackupVO>> listBackupVOByPage(@RequestBody BackupQueryRequest backupQueryRequest) {
        long current = backupQueryRequest.getCurrent();
        long size = backupQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Backup> backupPage = backupService.page(new Page<>(current, size),
                backupService.getQueryWrapper(backupQueryRequest));
        return ResultUtils.success(backupService.getBackupVOPage(backupPage));
    }

    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param backupQueryRequest backup 查询请求
     * @return {@link BaseResponse}<{@link Page}<{@link BackupVO}>>
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<BackupVO>> listMyBackupVOByPage(@RequestBody BackupQueryRequest backupQueryRequest) {
        if (backupQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = ThreadLocalUtil.getLoginUser();
        backupQueryRequest.setUserId(loginUser.getId());
        long current = backupQueryRequest.getCurrent();
        long size = backupQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Backup> backupPage = backupService.page(new Page<>(current, size),
                backupService.getQueryWrapper(backupQueryRequest));
        return ResultUtils.success(backupService.getBackupVOPage(backupPage));
    }

    // endregion
    /**
     * 编辑（用户）
     *
     * @param backupEditRequest backup 编辑请求
     * @return {@link BaseResponse}<{@link Boolean}>
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editBackup(@RequestBody BackupEditRequest backupEditRequest) {
        if (backupEditRequest == null || backupEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Backup backup = new Backup();
        BeanUtils.copyProperties(backupEditRequest, backup);
        List<String> tags = backupEditRequest.getTags();
        if (tags != null) {
            backup.setTags(JSONUtil.toJsonStr(tags));
        }
        // 参数校验
        backupService.validBackup(backup, false);
        backupService.checkOperationAuth(backupEditRequest.getId());
        backup.setEditTime(new Date());
        boolean result = backupService.updateById(backup);
        return ResultUtils.success(result);
    }

}
