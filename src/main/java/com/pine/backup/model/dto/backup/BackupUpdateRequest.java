package com.pine.backup.model.dto.backup;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求
 *
* @author pine
 */
@Data
public class BackupUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

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
     * 浏览数
     */
    private Integer viewNum;

    private static final long serialVersionUID = 1L;
}