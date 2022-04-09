
package com.jstarcraft.core.common.selection.xpath.mind;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

/**
 * 主题-XPath
 * 
 * @author Birdy
 *
 */
public class TopicXPath extends BaseXPath {

    public TopicXPath(String xpath) throws JaxenException {
        super(xpath, TopicNavigator.getInstance());
    }

}
