package com.jstarcraft.core.codec.standard.converter;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.standard.StandardReader;
import com.jstarcraft.core.codec.standard.StandardWriter;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 集合转换器
 * 
 * @author Birdy
 *
 */
public class CollectionConverter extends StandardConverter<Collection<?>> {

    /** 0000 0000(Null标记) */
    private static final byte NULL_MARK = (byte) 0x00;

    /** 0000 0001(显式标记) */
    private static final byte EXPLICIT_MARK = (byte) 0x01;

    /** 0000 0002(隐式标记) */
    private static final byte IMPLICIT_MARK = (byte) 0x02;

    /** 0000 0003(引用标记) */
    private static final byte REFERENCE_MARK = (byte) 0x03;

    @Override
    public Collection<?> readValueFrom(StandardReader context, Type type, ClassDefinition definition) throws Exception {
        InputStream in = context.getInputStream();
        byte information = (byte) in.read();
        byte mark = getMark(information);
        if (mark == NULL_MARK) {
            return null;
        }
        if (mark == EXPLICIT_MARK) {
            int size = NumberConverter.readNumber(in).intValue();
            Collection instance = (Collection) definition.getInstance();
            context.putCollectionValue(instance);
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            Type elementType = types[0];
            StandardConverter converter = context.getStandardConverter(Specification.getSpecification(elementType));
            definition = context.getClassDefinition(TypeUtility.getRawType(elementType, null));
            for (int index = 0; index < size; index++) {
                Object element = converter.readValueFrom(context, elementType, definition);
                instance.add(element);
            }
            return instance;
        } else if (mark == IMPLICIT_MARK) {
            int size = NumberConverter.readNumber(in).intValue();
            Collection instance = (Collection) definition.getInstance();
            context.putCollectionValue(instance);
            for (int index = 0; index < size; index++) {
                int code = NumberConverter.readNumber(in).intValue();
                definition = context.getClassDefinition(code);
                StandardConverter converter = context.getStandardConverter(definition.getSpecification());
                Object element = converter.readValueFrom(context, definition.getType(), definition);
                instance.add(element);
            }
            return instance;
        } else if (mark == REFERENCE_MARK) {
            int reference = NumberConverter.readNumber(in).intValue();
            Collection instance = (Collection) context.getCollectionValue(reference);
            return instance;
        }
        String message = StringUtility.format("类型码[{}]没有对应标记码[{}]", type, mark);
        throw new CodecConvertionException(message);
    }

    @Override
    public void writeValueTo(StandardWriter context, Type type, ClassDefinition definition, Collection<?> instance) throws Exception {
        OutputStream out = context.getOutputStream();
        byte information = ClassDefinition.getMark(Specification.COLLECTION);
        if (instance == null) {
            out.write(information);
            return;
        }
        int reference = context.getCollectionIndex(instance);
        if (reference != -1) {
            information |= REFERENCE_MARK;
            out.write(information);
            NumberConverter.writeNumber(out, reference);
        } else {
            if (type instanceof Class) {
                information |= IMPLICIT_MARK;
                context.putCollectionValue(instance);
                out.write(information);
                int size = instance.size();
                NumberConverter.writeNumber(out, size);
                for (Object element : instance) {
                    definition = context.getClassDefinition(element == null ? void.class : element.getClass());
                    NumberConverter.writeNumber(out, definition.getCode());
                    StandardConverter converter = context.getStandardConverter(definition.getSpecification());
                    converter.writeValueTo(context, definition.getType(), definition, element);
                }
            } else {
                information |= EXPLICIT_MARK;
                context.putCollectionValue(instance);
                out.write(information);
                int size = instance.size();
                NumberConverter.writeNumber(out, size);
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] types = parameterizedType.getActualTypeArguments();
                Type elementType = types[0];
                StandardConverter converter = context.getStandardConverter(Specification.getSpecification(elementType));
                definition = context.getClassDefinition(TypeUtility.getRawType(elementType, null));
                for (Object element : instance) {
                    converter.writeValueTo(context, elementType, definition, element);
                }
            }
        }
    }

}
