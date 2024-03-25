package com.pine.backup.model.dto.backup;

import com.pine.backup.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 查询请求
 *
* @author pine
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class BackupQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * id
     */
    private Long notId;

    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 内容格式
     *  0 普通文本
     *  1 md
     *  2 富文本
     */
    private Integer contentType;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 至少有一个标签
     */
    private List<String> orTags;

    /**
     * 优先级
     *  999 精选
     */
    private Integer priority;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 收藏用户 id
     */
    private Long favourUserId;

    /**
     * 帖子发布最小时间
     */
    private Date startTime;

    /**
     * 帖子发布最大时间
     */
    private Date endTime;

    private static final long serialVersionUID = 1L;
}