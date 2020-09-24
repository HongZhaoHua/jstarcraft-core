package com.jstarcraft.core.codec.thrift.converter;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;

import com.jstarcraft.core.codec.exception.CodecConvertionException;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.thrift.ThriftReader;
import com.jstarcraft.core.codec.thrift.ThriftWriter;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 映射转换器
 * 
 * @author Birdy
 *
 */
public class MapConverter extends ThriftConverter<Map<Object, Object>> {

    /** 0000 0000(Null标记) */
    private static final byte NULL_MARK = (byte) 0x00;

    /** 0000 0001(显式标记) */
    private static final byte EXPLICIT_MARK = (byte) 0x01;

    /** 0000 0002(隐式标记) */
    private static final byte IMPLICIT_MARK = (byte) 0x02;

    /** 0000 0003(引用标记) */
    private static final byte REFERENCE_MARK = (byte) 0x03;

    @Override
    public Map<Object, Object> readValueFrom(ThriftReader context, Type type, ClassDefinition definition) throws Exception {
        byte information = protocol.readByte();
        byte mark = getMark(information);
        if (mark == NULL_MARK) {
            return null;
        }
        if (mark == EXPLICIT_MARK) {
            int size = protocol.readI32();
            Map map = (Map) definition.getInstance();
            context.putMapValue(map);
            ThriftConverter converter = context.getProtocolConverter(Specification.TYPE);

            ParameterizedType parameterizedType = (ParameterizedType) type;
            Type[] types = parameterizedType.getActualTypeArguments();
            Type keyType = types[0];
            Type valueType = types[1];

            ThriftConverter keyConverter = context.getProtocolConverter(Specification.getSpecification(keyType));
            ThriftConverter valueConverter = context.getProtocolConverter(Specification.getSpecification(valueType));
            ClassDefinition keyDefinition = context.getClassDefinition(TypeUtility.getRawType(keyType, null));
            ClassDefinition valueDefinition = context.getClassDefinition(TypeUtility.getRawType(valueType, null));
            for (int index = 0; index < size; index++) {
                Object key = keyConverter.readValueFrom(context, keyType, keyDefinition);
                Object value = valueConverter.readValueFrom(context, valueType, valueDefinition);
                map.put(key, value);
            }
            return map;
        } else if (mark == IMPLICIT_MARK) {
            int size = protocol.readI32();
            int code = protocol.readI32();
            definition = context.getClassDefinition(code);
            Map map = (Map) definition.getInstance();
            context.putMapValue(map);
            for (int index = 0; index < size; index++) {
                code = protocol.readI32();
                definition = context.getClassDefinition(code);
                Type keyType = definition.getType();
                ThriftConverter keyConverter = context.getProtocolConverter(definition.getSpecification());
                code = protocol.readI32();
                definition = context.getClassDefinition(code);
                Type valueType = definition.getType();
                ThriftConverter valueConverter = context.getProtocolConverter(definition.getSpecification());
                ClassDefinition keyDefinition = context.getClassDefinition(TypeUtility.getRawType(keyType, null));
                ClassDefinition valueDefinition = context.getClassDefinition(TypeUtility.getRawType(valueType, null));
                Object key = keyConverter.readValueFrom(context, keyType, keyDefinition);
                Object value = valueConverter.readValueFrom(context, valueType, valueDefinition);
                map.put(key, value);
            }
            return map;
        } else if (mark == REFERENCE_MARK) {
            int reference = protocol.readI32();
            Map map = (Map) context.getMapValue(reference);
            return map;
        }
        String message = StringUtility.format("类型码[{}]没有对应标记码[{}]", type, mark);
        throw new CodecConvertionException(message);
    }

    @Override
    public void writeValueTo(ThriftWriter context, Type type, ClassDefinition definition, Map<Object, Object> value) throws Exception {
        byte information = ClassDefinition.getMark(Specification.MAP);
        if (value == null) {
            protocol.writeByte(information);
            return;
        }
        int reference = context.getMapIndex(value);
        if (reference != -1) {
            information |= REFERENCE_MARK;
            protocol.writeByte(information);
            protocol.writeI32(reference);
        } else {
            if (type instanceof Class) {
                information |= IMPLICIT_MARK;
                context.putMapValue(value);
                protocol.writeByte(information);
                int size = value.size();
                protocol.writeI32(size);
                int code = definition.getCode();
                protocol.writeI32(code);
                for (Entry<Object, Object> keyValue : value.entrySet()) {
                    ClassDefinition keyDefinition = context.getClassDefinition(keyValue.getKey().getClass());
                    protocol.writeI32(keyDefinition.getCode());
                    ThriftConverter keyConverter = context.getProtocolConverter(keyDefinition.getSpecification());
                    ClassDefinition valueDefinition = context.getClassDefinition(keyValue.getValue() == null ? void.class : keyValue.getValue().getClass());
                    protocol.writeI32(valueDefinition.getCode());
                    ThriftConverter valueConverter = context.getProtocolConverter(valueDefinition.getSpecification());
                    keyConverter.writeValueTo(context, keyValue.getKey().getClass(), keyDefinition, keyValue.getKey());
                    valueConverter.writeValueTo(context, keyValue.getValue() == null ? void.class : keyValue.getValue().getClass(), valueDefinition, keyValue.getValue());
                }
            } else {
                information |= EXPLICIT_MARK;
                context.putMapValue(value);
                protocol.writeByte(information);
                int size = value.size();
                protocol.writeI32(size);
                definition = context.getClassDefinition(value.getClass());
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] types = parameterizedType.getActualTypeArguments();
                Type keyType = types[0];
                Type valueType = types[1];
                ThriftConverter keyConverter = context.getProtocolConverter(Specification.getSpecification(keyType));
                ThriftConverter valueConverter = context.getProtocolConverter(Specification.getSpecification(valueType));
                ClassDefinition keyDefinition = context.getClassDefinition(TypeUtility.getRawType(keyType, null));
                ClassDefinition valueDefinition = context.getClassDefinition(TypeUtility.getRawType(valueType, null));
                for (Entry<Object, Object> keyValue : value.entrySet()) {
                    keyConverter.writeValueTo(context, keyType, keyDefinition, keyValue.getKey());
                    valueConverter.writeValueTo(context, valueType, valueDefinition, keyValue.getValue());
                }
            }
        }
    }

}
