package com.jstarcraft.core.codec.standard.converter;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.standard.StandardReader;
import com.jstarcraft.core.codec.standard.StandardWriter;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 映射转换器
 * 
 * @author Birdy
 *
 */
public class MapConverter extends StandardConverter<Map<Object, Object>> {

    /** 0000 0000(Null标记) */
    private static final byte NULL_MARK = (byte) 0x00;

    /** 0000 0001(显式标记) */
    private static final byte EXPLICIT_MARK = (byte) 0x01;

    /** 0000 0002(隐式标记) */
    private static final byte IMPLICIT_MARK = (byte) 0x02;

    /** 0000 0003(引用标记) */
    private static final byte REFERENCE_MARK = (byte) 0x03;

    @Override
    public Map<Object, Object> readValueFrom(StandardReader context, Type type, ClassDefinition definition) throws Exception {
        InputStream in = context.getInputStream();
        byte information = (byte) in.read();
        byte mark = getMark(information);
        if (mark == NULL_MARK) {
            return null;
        }
        if (mark == EXPLICIT_MARK) {
            int size = NumberConverter.readNumber(in).intValue();
            Map instance = (Map) definition.getInstance();
            context.putMapValue(instance);
            StandardConverter converter = context.getStandardConverter(Specification.TYPE);
            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            Type keyType = types[0];
            Type valueType = types[1];
            StandardConverter keyConverter = context.getStandardConverter(Specification.getSpecification(keyType));
            StandardConverter valueConverter = context.getStandardConverter(Specification.getSpecification(valueType));
            ClassDefinition keyDefinition = context.getClassDefinition(TypeUtility.getRawType(keyType, null));
            ClassDefinition valueDefinition = context.getClassDefinition(TypeUtility.getRawType(valueType, null));
            for (int index = 0; index < size; index++) {
                Object key = keyConverter.readValueFrom(context, keyType, keyDefinition);
                Object value = valueConverter.readValueFrom(context, valueType, valueDefinition);
                instance.put(key, value);
            }
            return instance;
        } else if (mark == IMPLICIT_MARK) {
            int size = NumberConverter.readNumber(in).intValue();
            Map instance = (Map) definition.getInstance();
            context.putMapValue(instance);
            for (int index = 0; index < size; index++) {
                int code = NumberConverter.readNumber(in).intValue();
                definition = context.getClassDefinition(code);
                Type keyType = definition.getType();
                StandardConverter keyConverter = context.getStandardConverter(definition.getSpecification());
                code = NumberConverter.readNumber(in).intValue();
                definition = context.getClassDefinition(code);
                Type valueType = definition.getType();
                StandardConverter valueConverter = context.getStandardConverter(definition.getSpecification());
                ClassDefinition keyDefinition = context.getClassDefinition(TypeUtility.getRawType(keyType, null));
                ClassDefinition valueDefinition = context.getClassDefinition(TypeUtility.getRawType(valueType, null));
                Object key = keyConverter.readValueFrom(context, keyType, keyDefinition);
                Object value = valueConverter.readValueFrom(context, valueType, valueDefinition);
                instance.put(key, value);
            }
            return instance;
        } else if (mark == REFERENCE_MARK) {
            int reference = NumberConverter.readNumber(in).intValue();
            Map instance = (Map) context.getMapValue(reference);
            return instance;
        }
        String message = StringUtility.format("类型码[{}]没有对应标记码[{}]", type, mark);
        throw new CodecConvertionException(message);
    }

    @Override
    public void writeValueTo(StandardWriter context, Type type, ClassDefinition definition, Map<Object, Object> instance) throws Exception {
        OutputStream out = context.getOutputStream();
        byte information = ClassDefinition.getMark(Specification.MAP);
        if (instance == null) {
            out.write(information);
            return;
        }
        int reference = context.getMapIndex(instance);
        if (reference != -1) {
            information |= REFERENCE_MARK;
            out.write(information);
            NumberConverter.writeNumber(out, reference);
        } else {
            if (type instanceof Class) {
                information |= IMPLICIT_MARK;
                context.putMapValue(instance);
                out.write(information);
                int size = instance.size();
                NumberConverter.writeNumber(out, size);
                for (Entry<Object, Object> keyValue : instance.entrySet()) {
                    ClassDefinition keyDefinition = context.getClassDefinition(keyValue.getKey().getClass());
                    NumberConverter.writeNumber(out, keyDefinition.getCode());
                    StandardConverter keyConverter = context.getStandardConverter(keyDefinition.getSpecification());
                    ClassDefinition valueDefinition = context.getClassDefinition(keyValue.getValue() == null ? void.class : keyValue.getValue().getClass());
                    NumberConverter.writeNumber(out, valueDefinition.getCode());
                    StandardConverter valueConverter = context.getStandardConverter(valueDefinition.getSpecification());
                    keyConverter.writeValueTo(context, keyValue.getKey().getClass(), keyDefinition, keyValue.getKey());
                    valueConverter.writeValueTo(context, keyValue.getValue() == null ? void.class : keyValue.getValue().getClass(), valueDefinition, keyValue.getValue());
                }
            } else {
                information |= EXPLICIT_MARK;
                context.putMapValue(instance);
                out.write(information);
                int size = instance.size();
                NumberConverter.writeNumber(out, size);
                definition = context.getClassDefinition(instance.getClass());
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] types = parameterizedType.getActualTypeArguments();
                Type keyType = types[0];
                Type valueType = types[1];
                StandardConverter keyConverter = context.getStandardConverter(Specification.getSpecification(keyType));
                StandardConverter valueConverter = context.getStandardConverter(Specification.getSpecification(valueType));
                ClassDefinition keyDefinition = context.getClassDefinition(TypeUtility.getRawType(keyType, null));
                ClassDefinition valueDefinition = context.getClassDefinition(TypeUtility.getRawType(valueType, null));
                for (Entry<Object, Object> keyValue : instance.entrySet()) {
                    keyConverter.writeValueTo(context, keyType, keyDefinition, keyValue.getKey());
                    valueConverter.writeValueTo(context, valueType, valueDefinition, keyValue.getValue());
                }
            }
        }
    }

}
