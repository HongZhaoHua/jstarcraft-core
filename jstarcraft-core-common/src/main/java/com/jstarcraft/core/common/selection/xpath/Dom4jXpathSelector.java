package com.jstarcraft.core.common.selection.xpath;

import org.dom4j.Node;
import org.jaxen.JaxenException;

import com.jstarcraft.core.common.selection.XpathSelector;
import com.jstarcraft.core.common.selection.xpath.swing.SwingXPath;

public class Dom4jXpathSelector extends XpathSelector<Node> {

    public Dom4jXpathSelector(String query) {
        super(query);
        try {
            this.xpath = new SwingXPath(query);
        } catch (JaxenException exception) {
            throw new RuntimeException(exception);
        }
    }

}
