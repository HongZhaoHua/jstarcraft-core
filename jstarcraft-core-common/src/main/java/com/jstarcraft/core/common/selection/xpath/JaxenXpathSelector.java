package com.jstarcraft.core.common.selection.xpath;

import java.util.List;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;
import org.jaxen.Navigator;

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
public class JaxenXpathSelector<T> extends XpathSelector<T> {

    protected BaseXPath xpath;

    public JaxenXpathSelector(String query, Navigator navigator) {
        super(query);
        try {
            this.xpath = new BaseXPath(query, navigator);
        } catch (JaxenException exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    @Override
    public List<T> selectMultiple(T content) {
        try {
            return (List<T>) xpath.selectNodes(content);
        } catch (JaxenException exception) {
            throw new RuntimeException(exception);
        }
    }

}
