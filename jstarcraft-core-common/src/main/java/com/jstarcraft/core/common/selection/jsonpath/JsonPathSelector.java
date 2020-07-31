package com.jstarcraft.core.common.selection.jsonpath;

import com.jstarcraft.core.common.selection.AbstractSelector;

/**
 * JSONPath选择器
 * 
 * @author Birdy
 *
 */
public abstract class JsonPathSelector<T> extends AbstractSelector<T> {

    public JsonPathSelector(String query) {
        super(query);
    }

}
