package com.jstarcraft.core.common.selection.xpath;

import org.jaxen.JaxenException;
import org.jaxen.jdom.JDOMXPath;
import org.jdom2.NamespaceAware;

/**
 * JDOM-XPath选择器
 * 
 * @author Birdy
 *
 */
public class JdomXpathSelector extends XpathSelector<NamespaceAware> {

    public JdomXpathSelector(String query) {
        super(query);
        try {
            this.xpath = new JDOMXPath(query);
        } catch (JaxenException exception) {
            throw new RuntimeException(exception);
        }
    }

}
