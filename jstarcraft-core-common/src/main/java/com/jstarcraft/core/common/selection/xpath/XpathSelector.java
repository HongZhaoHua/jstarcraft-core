package com.jstarcraft.core.common.selection.xpath;

import java.util.Collection;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

import com.jstarcraft.core.common.selection.AbstractSelector;

/**
 * XPath选择器
 * 
 * @author Birdy
 *
 * @param <T>
 */
//TODO 目前仍存在问题,如何兼容节点与属性
public abstract class XpathSelector<T> extends AbstractSelector<T> {

    protected BaseXPath xpath;

    public XpathSelector(String query) {
        super(query);
    }

    @Override
    public Collection<T> selectContent(T content) {
        try {
            return (Collection<T>) xpath.selectNodes(content);
        } catch (JaxenException exception) {
            throw new RuntimeException(exception);
        }
    }

}
