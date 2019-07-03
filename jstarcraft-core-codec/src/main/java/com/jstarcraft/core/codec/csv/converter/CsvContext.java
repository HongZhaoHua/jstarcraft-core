package com.jstarcraft.core.codec.csv.converter;

import java.util.EnumMap;

import org.apache.commons.csv.CSVFormat;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.common.reflection.Specification;

/**
 * CSV上下文
 * 
 * @author Birdy
 *
 */
public class CsvContext {

    protected static final CSVFormat FORMAT = CSVFormat.DEFAULT;

    protected static final EnumMap<Specification, CsvConverter<?>> converters = new EnumMap<>(Specification.class);

    static {
        converters.put(Specification.ARRAY, new ArrayConverter());
        converters.put(Specification.BOOLEAN, new BooleanConverter());
        converters.put(Specification.COLLECTION, new CollectionConverter());
        converters.put(Specification.ENUMERATION, new EnumerationConverter());
        converters.put(Specification.MAP, new MapConverter());
        converters.put(Specification.NUMBER, new NumberConverter());
        converters.put(Specification.OBJECT, new ObjectConverter());
        converters.put(Specification.STRING, new StringConverter());
        converters.put(Specification.INSTANT, new InstantConverter());
        converters.put(Specification.TYPE, new TypeConverter());
    }

    /** 协议定义 */
    private final CodecDefinition definition;

    public CsvContext(CodecDefinition definition) {
        this.definition = definition;
    }

    public CsvConverter getCsvConverter(Specification specification) {
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
