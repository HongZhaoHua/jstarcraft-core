package com.jstarcraft.core.common.selection.xpath;

import org.jaxen.JaxenException;

import com.jstarcraft.core.common.selection.XpathSelector;
import com.jstarcraft.core.common.selection.xpath.swing.SwingXPath;

import nu.xom.Node;

public class XomXpathSelector extends XpathSelector<Node> {

    public XomXpathSelector(String query) {
        super(query);
        try {
            this.xpath = new SwingXPath(query);
        } catch (JaxenException exception) {
            throw new RuntimeException(exception);
        }
    }

}
