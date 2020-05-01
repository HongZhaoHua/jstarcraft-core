package com.jstarcraft.core.common.selection.xpath;

import org.jaxen.JaxenException;
import org.jdom2.Parent;

import com.jstarcraft.core.common.selection.XpathSelector;
import com.jstarcraft.core.common.selection.xpath.swing.SwingXPath;

public class JdomXpathSelector extends XpathSelector<Parent> {

    public JdomXpathSelector(String query) {
        super(query);
        try {
            this.xpath = new SwingXPath(query);
        } catch (JaxenException exception) {
            throw new RuntimeException(exception);
        }
    }

}
