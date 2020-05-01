package com.jstarcraft.core.common.selection.xpath;

import org.jaxen.JaxenException;
import org.w3c.dom.Node;

import com.jstarcraft.core.common.selection.XpathSelector;
import com.jstarcraft.core.common.selection.xpath.swing.SwingXPath;

public class DomXpathSelector extends XpathSelector<Node> {

    public DomXpathSelector(String query) {
        super(query);
        try {
            this.xpath = new SwingXPath(query);
        } catch (JaxenException exception) {
            throw new RuntimeException(exception);
        }
    }

}
