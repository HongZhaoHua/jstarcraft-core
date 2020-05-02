package com.jstarcraft.core.common.selection.xpath;

import org.jaxen.JaxenException;
import org.jaxen.xom.XOMXPath;

import com.jstarcraft.core.common.selection.XpathSelector;

import nu.xom.Node;

public class XomXpathSelector extends XpathSelector<Node> {

    public XomXpathSelector(String query) {
        super(query);
        try {
            this.xpath = new XOMXPath(query);
        } catch (JaxenException exception) {
            throw new RuntimeException(exception);
        }
    }

}
