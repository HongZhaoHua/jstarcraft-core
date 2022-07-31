package com.jstarcraft.core.storage.berkeley.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.jstarcraft.core.storage.berkeley.schema.BerkeleyXmlParser.ElementDefinition;

/**
 * BerkeleyXML处理器
 * 
 * @author Birdy
 */
public class BerkeleyXmlHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser(ElementDefinition.CONFIGURATION.getName(), new BerkeleyXmlParser());
    }

}
