package com.jstarcraft.core.resource.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.jstarcraft.core.resource.schema.ResourceXmlParser.ElementDefinition;

/**
 * 仓储XML处理器
 * 
 * @author Birdy
 */
public class ResourceXmlHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser(ElementDefinition.CONFIGURATION.getName(), new ResourceXmlParser());
    }

}
