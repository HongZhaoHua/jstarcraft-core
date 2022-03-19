package com.jstarcraft.core.common.selection;

import java.util.List;

/**
 * 查询选择器
 * 
 * @author Birdy
 *
 * @param <T>
 */
public interface QuerySelector<T> {

    /**
     * 选择单个
     * 
     * @param content
     * @return
     */
    default T selectSingle(T content) {
        List<T> instances = selectMultiple(content);
        return instances.isEmpty() ? null : instances.get(0);
    }

    /**
     * 选择多个
     * 
     * @param content
     * @return
     */
    List<T> selectMultiple(T content);

    /**
     * 获取查询
     * 
     * @return
     */
    String getQuery();

}
