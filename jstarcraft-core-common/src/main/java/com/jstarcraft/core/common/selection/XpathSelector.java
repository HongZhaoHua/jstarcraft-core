package com.jstarcraft.core.common.selection;

import java.util.Collection;

import org.jaxen.JaxenException;

import com.jstarcraft.core.common.selection.xpath.swing.SwingXPath;

public abstract class XpathSelector<T> extends AbstractSelector<T> {

    protected SwingXPath xpath;

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
