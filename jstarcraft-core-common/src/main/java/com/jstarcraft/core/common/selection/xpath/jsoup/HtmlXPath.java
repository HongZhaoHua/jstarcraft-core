package com.jstarcraft.core.common.selection.xpath.jsoup;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

/**
 * HTML-XPath
 * 
 * @author Birdy
 *
 */
public class HtmlXPath extends BaseXPath {

    public HtmlXPath(String xpath) throws JaxenException {
        super(xpath, HtmlNavigator.getInstance());
    }

}
