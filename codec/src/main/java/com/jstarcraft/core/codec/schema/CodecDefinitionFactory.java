package com.jstarcraft.core.codec.schema;

import java.lang.reflect.Type;
import java.util.Collection;

import org.springframework.beans.factory.FactoryBean;

import com.jstarcraft.core.codec.specification.CodecDefinition;

/**
 * 编解码定义工厂
 * 
 * @author Birdy
 *
 */
public class CodecDefinitionFactory implements FactoryBean<CodecDefinition> {

	/** 协议列表 */
	private Collection<Type> protocolClasses;

	/** 协议定义 */
	private CodecDefinition protocolDefinition;

	public void setProtocolClasses(Collection<Type> protocolClasses) {
		this.protocolClasses = protocolClasses;
	}

	@Override
	public CodecDefinition getObject() throws Exception {
		if (protocolDefinition == null) {
			protocolDefinition = CodecDefinition.instanceOf(protocolClasses);
		}
		return protocolDefinition;
	}

	@Override
	public Class<CodecDefinition> getObjectType() {
		return CodecDefinition.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
