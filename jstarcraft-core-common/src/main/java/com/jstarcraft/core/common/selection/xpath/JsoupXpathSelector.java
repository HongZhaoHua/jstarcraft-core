package com.jstarcraft.core.common.selection.xpath;

import org.jaxen.JaxenException;

import com.jstarcraft.core.common.selection.xpath.jsoup.HtmlNode;
import com.jstarcraft.core.common.selection.xpath.jsoup.HtmlXPath;

/**
 * jsoup-XPath选择器
 * 
 * @author Birdy
 *
 */
public class JsoupXpathSelector extends JaxenXpathSelector<HtmlNode> {

    public JsoupXpathSelector(String query) {
        super(query);
        try {
            this.xpath = new HtmlXPath(query);
        } catch (JaxenException exception) {
            throw new RuntimeException(exception);
        }
    }

}
