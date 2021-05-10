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
     * 选择
     * 
     * @param content
     * @return
     */
    List<T> selectContent(T content);

    /**
     * 获取查询
     * 
     * @return
     */
    String getQuery();

}
