package com.jstarcraft.core.storage.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.jstarcraft.core.storage.schema.StorageXmlParser.ElementDefinition;

/**
 * 仓储XML处理器
 * 
 * @author Birdy
 */
public class StorageXmlHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser(ElementDefinition.CONFIGURATION.getName(), new StorageXmlParser());
	}

}
