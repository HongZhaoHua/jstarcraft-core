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
 * @author: MnZzV
 **/
public class ObjectConverter extends AvroConverter<Object> {

    @Override
    protected Object readValue(AvroReader avroReader, Object input, Type type) throws Exception {
        if (input == null) {
            return null;
        }
        GenericRecord genericData = (GenericRecord) input;
        Class<?> rawType = TypeUtility.getRawType(type, null);
        ClassDefinition classDefinition = avroReader.getClassDefinition(rawType);
        Object instance = classDefinition.getInstance();
        for (PropertyDefinition property : classDefinition.getProperties()) {
            Object inputItem = genericData.get(property.getName());
            AvroConverter avroConverter = avroReader.getAvroConverter(Specification.getSpecification(property.getType()));
            Object item = avroConverter.readValue(avroReader, inputItem, property.getType());
            property.setValue(instance, item);
        }
        return instance;
    }

    @Override
    protected Object writeValue(AvroWriter writer, Object input, Type type) throws Exception {
        Schema schema = super.getSchema(type);
        GenericRecord parquet = new GenericData.Record(schema);
        if (input == null) {
            return null;
        }
        Class<?> clazz = TypeUtility.getRawType(type, null);
        for (Field declaredField : clazz.getDeclaredFields()) {
            AvroConverter avroConverter = writer.getAvroConverter(Specification.getSpecification(declaredField.getGenericType()));
            declaredField.setAccessible(true);
            parquet.put(declaredField.getName(), avroConverter.writeValue(writer, declaredField.get(input), declaredField.getGenericType()));
        }

        return parquet;
    }
}
