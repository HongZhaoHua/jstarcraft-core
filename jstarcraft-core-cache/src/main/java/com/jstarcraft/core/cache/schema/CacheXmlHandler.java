package com.jstarcraft.core.cache.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.jstarcraft.core.cache.schema.CacheXmlParser.ElementDefinition;

/**
 * 缓存XML处理器
 * 
 * @author Birdy
 */
public class CacheXmlHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser(ElementDefinition.CONFIGURATION.getName(), new CacheXmlParser());
    }

}
