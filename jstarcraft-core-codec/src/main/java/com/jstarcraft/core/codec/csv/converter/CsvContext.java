package com.jstarcraft.core.codec.csv.converter;

import java.util.EnumMap;

import org.apache.commons.csv.CSVFormat;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.codec.specification.CodecSpecification;

/**
 * CSV上下文
 * 
 * @author Birdy
 *
 */
public class CsvContext {

	protected static final CSVFormat FORMAT = CSVFormat.DEFAULT;

	protected static final EnumMap<CodecSpecification, CsvConverter<?>> converters = new EnumMap<>(CodecSpecification.class);
	static {
		converters.put(CodecSpecification.ARRAY, new ArrayConverter());
		converters.put(CodecSpecification.BOOLEAN, new BooleanConverter());
		converters.put(CodecSpecification.COLLECTION, new CollectionConverter());
		converters.put(CodecSpecification.ENUMERATION, new EnumerationConverter());
		converters.put(CodecSpecification.MAP, new MapConverter());
		converters.put(CodecSpecification.NUMBER, new NumberConverter());
		converters.put(CodecSpecification.OBJECT, new ObjectConverter());
		converters.put(CodecSpecification.STRING, new StringConverter());
		converters.put(CodecSpecification.INSTANT, new InstantConverter());
		converters.put(CodecSpecification.TYPE, new TypeConverter());
	}

	/** 协议定义 */
	private final CodecDefinition definition;

	public CsvContext(CodecDefinition definition) {
		this.definition = definition;
	}

	public CsvConverter getCsvConverter(CodecSpecification specification) {
		CsvConverter converter = converters.get(specification);
		return converter;
	}

	protected ClassDefinition getClassDefinition(int index) {
		return definition.getClassDefinition(index);
	}

	protected ClassDefinition getClassDefinition(Class<?> clazz) {
		return definition.getClassDefinition(clazz);
	}

}
