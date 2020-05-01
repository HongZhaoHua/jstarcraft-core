
package com.jstarcraft.core.common.selection.xpath.swing;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

/**
 * Swing-XPath
 * 
 * @author Birdy
 *
 */
public class SwingXPath extends BaseXPath {

    public SwingXPath(String xpath) throws JaxenException {
        super(xpath, SwingNavigator.getInstance());
    }

}
