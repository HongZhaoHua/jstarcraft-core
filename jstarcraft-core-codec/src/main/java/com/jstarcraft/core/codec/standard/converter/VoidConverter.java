package com.jstarcraft.core.codec.standard.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.standard.StandardReader;
import com.jstarcraft.core.codec.standard.StandardWriter;

/**
 * Void转换器
 * 
 * @author Birdy
 *
 */
public class VoidConverter extends StandardConverter<Object> {

    @Override
    public Object readValueFrom(StandardReader context, Type type, ClassDefinition definition) throws IOException {
        return null;
    }

    @Override
    public void writeValueTo(StandardWriter context, Type type, ClassDefinition definition, Object instance) throws IOException {
    }

}
