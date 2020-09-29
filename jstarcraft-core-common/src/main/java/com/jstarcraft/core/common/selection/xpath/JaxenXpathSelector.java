package com.jstarcraft.core.common.selection.xpath;

import java.util.Collection;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

/**
 * XPath选择器
 * 
 * <pre>
 * 基于Jaxen
 * </pre>
 * 
 * @author Birdy
 *
 * @param <T>
 */
public abstract class JaxenXpathSelector<T> extends XpathSelector<T> {

    protected BaseXPath xpath;

    public JaxenXpathSelector(String query) {
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
