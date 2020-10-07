package com.jstarcraft.core.common.selection.xpath;

import com.jstarcraft.core.common.selection.AbstractSelector;

/**
 * XPath选择器
 * 
 * @author Birdy
 *
 */
public abstract class XpathSelector<T> extends AbstractSelector<T> {

    public XpathSelector(String query) {
        super(query);
    }

}
