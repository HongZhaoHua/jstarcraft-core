package com.jstarcraft.core.codec.avro.converter;

import java.lang.reflect.Field;
import java.lang.reflect.Type;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

import com.jstarcraft.core.codec.avro.AvroReader;
import com.jstarcraft.core.codec.avro.AvroWriter;
import com.jstarcraft.core.codec.specification.ClassDefinition;
import com.jstarcraft.core.codec.specification.PropertyDefinition;
import com.jstarcraft.core.common.reflection.Specification;
import com.jstarcraft.core.common.reflection.TypeUtility;

/**
 * 对象转换器
 * 
 * @author Yue Zhen Wei
 *
 */
public class ObjectConverter extends AvroConverter<Object> {

    @Override
    protected Object readValue(AvroReader context, Object record, Type type) throws Exception {
        if (record == null) {
            return null;
        }
        GenericRecord genericData = (GenericRecord) record;
        Class<?> rawType = TypeUtility.getRawType(type, null);
        ClassDefinition classDefinition = context.getClassDefinition(rawType);
        Object instance = classDefinition.getInstance();
        for (PropertyDefinition property : classDefinition.getProperties()) {
            Object inputItem = genericData.get(property.getName());
            AvroConverter avroConverter = context.getAvroConverter(Specification.getSpecification(property.getType()));
            Object item = avroConverter.readValue(context, inputItem, property.getType());
            property.setValue(instance, item);
        }
        return instance;
    }

    @Override
    protected Object writeValue(AvroWriter context, Object instance, Type type) throws Exception {
        Schema schema = super.getSchema(type);
        GenericRecord record = new GenericData.Record(schema);
        if (instance == null) {
            return null;
        }
        Class<?> clazz = TypeUtility.getRawType(type, null);
        for (Field declaredField : clazz.getDeclaredFields()) {
            AvroConverter avroConverter = context.getAvroConverter(Specification.getSpecification(declaredField.getGenericType()));
            declaredField.setAccessible(true);
            record.put(declaredField.getName(), avroConverter.writeValue(context, declaredField.get(instance), declaredField.getGenericType()));
        }
        return record;
    }
}
