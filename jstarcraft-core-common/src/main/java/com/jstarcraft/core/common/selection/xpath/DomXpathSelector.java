package com.jstarcraft.core.common.selection.xpath;

import org.jaxen.JaxenException;
import org.jaxen.dom.DOMXPath;
import org.w3c.dom.Node;

import com.jstarcraft.core.common.selection.XpathSelector;

public class DomXpathSelector extends XpathSelector<Node> {

    public DomXpathSelector(String query) {
        super(query);
        try {
            this.xpath = new DOMXPath(query);
        } catch (JaxenException exception) {
            throw new RuntimeException(exception);
        }
    }

}
