package com.jstarcraft.core.common.selection.xpath;

import org.jaxen.JaxenException;

import com.jstarcraft.core.common.selection.XpathSelector;
import com.jstarcraft.core.common.selection.xpath.jsoup.HtmlXPath;
import com.jstarcraft.core.common.selection.xpath.swing.SwingNode;

public class JsoupXpathSelector extends XpathSelector<SwingNode> {

    public JsoupXpathSelector(String query) {
        super(query);
        try {
            this.xpath = new HtmlXPath(query);
        } catch (JaxenException exception) {
            throw new RuntimeException(exception);
        }
    }

}
