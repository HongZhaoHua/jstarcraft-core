package com.jstarcraft.core.utility.conversion;

import org.springframework.core.convert.converter.Converter;

import com.jstarcraft.core.utility.Configuration;

public class String2ClassConverter implements Converter<String, Class<?>> {

	private static ClassLoader container;

	static {
		container = Thread.currentThread().getContextClassLoader();
		if (container == null) {
			container = Configuration.class.getClassLoader();
		}
	}

	@Override
	public Class<?> convert(String instance) {
		try {
			return Class.forName(instance, true, container);
		} catch (ClassNotFoundException exception) {
			throw new IllegalArgumentException(exception);
		}
	}

}
