package com.jstarcraft.core.orm.lucene.schema;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

import com.jstarcraft.core.orm.lucene.schema.LuceneXmlParser.ElementDefinition;

/**
 * LuceneXML处理器
 * 
 * @author Birdy
 */
public class LuceneXmlHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser(ElementDefinition.CONFIGURATION.getName(), new LuceneXmlParser());
    }

}
