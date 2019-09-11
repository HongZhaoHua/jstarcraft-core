package com.jstarcraft.core.communication.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.jstarcraft.core.communication.schema.CommunicationXmlParser.ElementDefinition;

/**
 * 通讯XML处理器
 * 
 * @author Birdy
 */
public class CommunicationXmlHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser(ElementDefinition.CONFIGURATION.getName(), new CommunicationXmlParser());
    }

}
