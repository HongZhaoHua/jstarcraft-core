package com.jstarcraft.core.common.selection.css;

import com.jstarcraft.core.common.selection.AbstractSelector;

/**
 * CSS选择器
 * 
 * @author Birdy
 *
 * @param <T>
 */
public abstract class CssSelector<T> extends AbstractSelector<T> {

    public CssSelector(String query) {
        super(query);
    }

}
