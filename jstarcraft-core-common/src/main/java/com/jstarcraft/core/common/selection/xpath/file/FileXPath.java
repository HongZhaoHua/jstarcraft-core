
package com.jstarcraft.core.common.selection.xpath.file;

import org.jaxen.BaseXPath;
import org.jaxen.JaxenException;

/**
 * 文件-XPath
 * 
 * @author Birdy
 *
 */
public class FileXPath extends BaseXPath {

    public FileXPath(String xpath) throws JaxenException {
        super(xpath, FileNavigator.getInstance());
    }

}
