package com.pine.backup.util;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;

/**
 * @author pine
 */
public class WrapperUtil {

    /**
     * 处理 queryWrapper 的排序规则
     *
     * @param queryWrapper  查询包装器
     * @param ascSortField  asc 排序字段
     * @param descSortField desc 排序字段
     */
    public static void handleOrder(QueryWrapper<?> queryWrapper, List<String> ascSortField, List<String> descSortField) {
        boolean ascValid = ascSortField != null && ascSortField.size() > 0;
        boolean descValid = descSortField != null && descSortField.size() > 0;
        queryWrapper.orderByAsc(ascValid, ascSortField);
        queryWrapper.orderByDesc(descValid, descSortField);
    }
}
