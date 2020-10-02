package com.jstarcraft.core.codec.standard.converter;

import java.lang.reflect.Type;

import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.standard.StandardReader;
import com.jstarcraft.core.codec.standard.StandardWriter;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * 类型转换器
 * 
 * @author Birdy
 *
 */
public class TypeConverter extends StandardConverter<Type> {

    @Override
    public Type readValueFrom(StandardReader context, Type type, ClassDefinition definition) throws Exception {
        StandardConverter converter = context.getStandardConverter(Specification.STRING);
        String instance = (String) converter.readValueFrom(context, String.class, context.getClassDefinition(String.class));
        return instance == null ? null : TypeUtility.string2Type(instance);
    }

    @Override
    public void writeValueTo(StandardWriter context, Type type, ClassDefinition definition, Type instance) throws Exception {
        StandardConverter converter = context.getStandardConverter(Specification.STRING);
        String string = type == null ? null : TypeUtility.type2String(instance);
        converter.writeValueTo(context, String.class, context.getClassDefinition(String.class), string);
    }

}
