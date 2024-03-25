package com.pine.backup.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pine.backup.common.ErrorCode;
import com.pine.backup.exception.BusinessException;
import com.pine.backup.exception.ThrowUtils;
import com.pine.backup.mapper.BackupMapper;
import com.pine.backup.model.dto.backup.BackupQueryRequest;
import com.pine.backup.model.entity.Backup;
import com.pine.backup.model.entity.User;
import com.pine.backup.model.vo.BackupVO;
import com.pine.backup.model.vo.UserVO;
import com.pine.backup.service.BackupService;
import com.pine.backup.service.UserService;
import com.pine.backup.util.ThreadLocalUtil;
import com.pine.backup.util.WrapperUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 帖子服务实现
 *
* @author pine
 */
@Service
@Slf4j
public class BackupServiceImpl extends ServiceImpl<BackupMapper, Backup> implements BackupService {

    @Resource
    private UserService userService;

    @Override
    public void validBackup(Backup backup, boolean add) {
        if (backup == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        String title = backup.getTitle();
        String content = backup.getContent();
        String tags = backup.getTags();
        Integer contentType = backup.getContentType();
        // if (contentType != null) {
        //     ContentTypeEnum contentTypeEnum = ContentTypeEnum.getEnumByValue(contentType);
        //     if (contentTypeEnum == null) {
        //         throw new BusinessException(ErrorCode.PARAMS_ERROR, "帖子内容类型异常");
        //     }
        // }
        // 创建时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StringUtils.isAnyBlank(title, content), ErrorCode.PARAMS_ERROR);
        }
        // 有参数则校验
        if (StringUtils.isNotBlank(title) && title.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "标题过长");
        }
        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
        }
    }

    @Override
    public BackupVO getBackupVO(Backup backup) {
        BackupVO backupVO = BackupVO.objToVo(backup);
        long backupId = backup.getId();
        // 1. 关联查询用户信息
        Long userId = backup.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        backupVO.setUser(userVO);
        // 2. 已登录，获取用户点赞、收藏状态
        // User loginUser = userService.getLoginUserPermitNull();
        // if (loginUser != null) {
        //     // 获取点赞
        //     QueryWrapper<BackupThumb> backupThumbQueryWrapper = new QueryWrapper<>();
        //     backupThumbQueryWrapper.in("backupId", backupId);
        //     backupThumbQueryWrapper.eq("userId", loginUser.getId());
        //     BackupThumb backupThumb = backupThumbMapper.selectOne(backupThumbQueryWrapper);
        //     backupVO.setHasThumb(backupThumb != null);
        //     // 获取收藏
        //     QueryWrapper<BackupFavour> backupFavourQueryWrapper = new QueryWrapper<>();
        //     backupFavourQueryWrapper.in("backupId", backupId);
        //     backupFavourQueryWrapper.eq("userId", loginUser.getId());
        //     BackupFavour backupFavour = backupFavourMapper.selectOne(backupFavourQueryWrapper);
        //     backupVO.setHasFavour(backupFavour != null);
        // }
        return backupVO;
    }

    @Override
    public Page<BackupVO> getBackupVOPage(Page<Backup> backupPage) {
        List<Backup> backupList = backupPage.getRecords();
        Page<BackupVO> backupVOPage = new Page<>(backupPage.getCurrent(), backupPage.getSize(), backupPage.getTotal());
        if (CollUtil.isEmpty(backupList)) {
            return backupVOPage;
        }
        // 1. 关联查询用户信息
        Set<Long> userIdSet = backupList.stream().map(Backup::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        List<BackupVO> backupVOList = backupList.stream().map(backup -> {
            BackupVO backupVO = BackupVO.objToVo(backup);
            Long userId = backup.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            backupVO.setUser(userService.getUserVO(user));
            return backupVO;
        }).collect(Collectors.toList());
        backupVOPage.setRecords(backupVOList);
        return backupVOPage;
    }

    @Override
    public void checkOperationAuth(Long backupId) {
        User loginUser = ThreadLocalUtil.getLoginUser();
        // 判断是否存在
        Backup oldBackup = this.getById(backupId);
        ThrowUtils.throwIf(oldBackup == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldBackup.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
    }

    /**
     * 获取查询包装类
     *
     * @param backupQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Backup> getQueryWrapper(BackupQueryRequest backupQueryRequest) {
        QueryWrapper<Backup> queryWrapper = new QueryWrapper<>();
        if (backupQueryRequest == null) {
            return queryWrapper;
        }
        String searchText = backupQueryRequest.getSearchText();
        List<String> ascSortField = backupQueryRequest.getAscSortField();
        List<String> descSortField = backupQueryRequest.getDescSortField();
        Long id = backupQueryRequest.getId();
        String title = backupQueryRequest.getTitle();
        String content = backupQueryRequest.getContent();
        List<String> tagList = backupQueryRequest.getTags();
        List<String> orTagList = backupQueryRequest.getOrTags();
        Integer contentType = backupQueryRequest.getContentType();
        Long userId = backupQueryRequest.getUserId();
        Long notId = backupQueryRequest.getNotId();
        Integer priority = backupQueryRequest.getPriority();
        Date startTime = backupQueryRequest.getStartTime();
        Date endTime = backupQueryRequest.getEndTime();

        // 拼接查询条件
        if (StringUtils.isNotBlank(searchText)) {
            queryWrapper.and(w -> w.like("title", searchText).or().like("content", searchText));
        }
        queryWrapper.like(StringUtils.isNotBlank(title), "title", title);
        queryWrapper.like(StringUtils.isNotBlank(content), "content", content);
        if (CollUtil.isNotEmpty(tagList)) {
            for (String tag : tagList) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        if (CollUtil.isNotEmpty(orTagList)) {
            queryWrapper.and(w -> {
                for (String orTag : orTagList) {
                    w.like("tags", orTag).or();
                }
            });
        }
        queryWrapper.eq(ObjectUtils.isNotEmpty(contentType), "contentType", contentType);
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(priority), "priority", priority);

        queryWrapper.ge(ObjectUtils.isNotEmpty(startTime), "createTime", startTime);
        queryWrapper.le(ObjectUtils.isNotEmpty(endTime), "createTime", endTime);

        WrapperUtil.handleOrder(queryWrapper, ascSortField, descSortField);

        return queryWrapper;
    }

}




