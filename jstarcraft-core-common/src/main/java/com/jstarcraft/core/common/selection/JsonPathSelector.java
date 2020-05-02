package com.jstarcraft.core.common.selection;

/**
 * JSON选择器
 * 
 * @author Birdy
 *
 */
public abstract class JsonPathSelector<T> extends AbstractSelector<T> {

    public JsonPathSelector(String query) {
        super(query);
    }

}
