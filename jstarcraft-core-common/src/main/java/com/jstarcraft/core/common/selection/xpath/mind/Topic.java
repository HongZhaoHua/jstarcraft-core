package com.jstarcraft.core.common.selection.xpath.mind;

import java.util.List;

/**
 * 主题
 * 
 * @author Birdy
 *
 * @param <T>
 */
public interface Topic<T extends Topic<T>> {

    public String getTitle();

    public List<T> getChildren();

}
