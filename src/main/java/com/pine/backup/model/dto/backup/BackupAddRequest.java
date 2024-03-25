package com.pine.backup.model.dto.backup;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建请求
 *
* @author pine
 */
@Data
public class BackupAddRequest implements Serializable {

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

    private static final long serialVersionUID = 1L;
}