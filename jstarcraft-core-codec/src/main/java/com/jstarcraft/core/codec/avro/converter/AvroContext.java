package com.jstarcraft.core.codec.avro.converter;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.common.reflection.Specification;

import java.util.EnumMap;

public class AvroContext {

    protected static final EnumMap<Specification, AvroConverter<?>> converters = new EnumMap<>(Specification.class);

    static {
        converters.put(Specification.ARRAY, new ArrayConverter());
        converters.put(Specification.COLLECTION, new CollectionConverter());
        converters.put(Specification.ENUMERATION, new EnumerationConverter());
        converters.put(Specification.MAP, new MapConverter());
        converters.put(Specification.STRING, new StringConverter());
        converters.put(Specification.NUMBER, new NumberConverter());
        converters.put(Specification.BOOLEAN, new BooleanConverter());
        converters.put(Specification.INSTANT, new InstantConverter());
        converters.put(Specification.OBJECT, new ObjectConverter());
        converters.put(Specification.TYPE, new TypeConverter());

    }
    private CodecDefinition definition;
    public AvroContext(CodecDefinition definition) {
        this.definition = definition;
    }

    public CodecDefinition getDefinition() {
        return definition;
    }

    protected ClassDefinition getClassDefinition(Class<?> clazz) {
        return definition.getClassDefinition(clazz);
    }

    protected ClassDefinition getClassDefinition(int index) {
        return definition.getClassDefinition(index);
    }

    public AvroConverter getAvroConverter(Specification specification) {
        return converters.get(specification);
    }


}
