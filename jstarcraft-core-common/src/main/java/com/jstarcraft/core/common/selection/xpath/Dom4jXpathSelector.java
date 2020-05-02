package com.jstarcraft.core.common.selection.xpath;

import org.dom4j.Node;
import org.jaxen.JaxenException;
import org.jaxen.dom4j.Dom4jXPath;

import com.jstarcraft.core.common.selection.XpathSelector;

/**
 * dom4j-XPath选择器
 * 
 * @author Birdy
 *
 */
public class Dom4jXpathSelector extends XpathSelector<Node> {

    public Dom4jXpathSelector(String query) {
        super(query);
        try {
            this.xpath = new Dom4jXPath(query);
        } catch (JaxenException exception) {
            throw new RuntimeException(exception);
        }
    }

}
