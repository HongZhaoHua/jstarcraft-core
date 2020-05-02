package com.jstarcraft.core.common.selection.xpath;

import org.jaxen.JaxenException;
import org.jaxen.jdom.JDOMXPath;
import org.jdom2.Parent;

import com.jstarcraft.core.common.selection.XpathSelector;

public class JdomXpathSelector extends XpathSelector<Parent> {

    public JdomXpathSelector(String query) {
        super(query);
        try {
            this.xpath = new JDOMXPath(query);
        } catch (JaxenException exception) {
            throw new RuntimeException(exception);
        }
    }

}
