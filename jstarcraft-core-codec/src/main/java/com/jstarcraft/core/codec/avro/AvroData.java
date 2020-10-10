package com.jstarcraft.core.codec.avro;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.UnresolvedUnionException;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.specific.SpecificData;

import com.jstarcraft.core.common.reflection.TypeUtility;

public class AvroData extends ReflectData {

    private static final String KEY_VALUE_RECORD = "com.jstarcraft.core.utility.KeyValue";
    
    private static final String KEY_FIELD = "key";
    
    private static final String VALUE_FIELD = "value";

    @Override
    protected Schema createSchema(Type type, Map<String, Schema> names) {
        if (type instanceof Class) {
            Class<?> clazz = (Class<?>) type;
            if (clazz.isArray()) {
                Class component = clazz.getComponentType();
                if (component == Byte.TYPE) {
                    Schema schema = Schema.create(Schema.Type.BYTES);
                    schema.addProp(SpecificData.CLASS_PROP, clazz.getName());
                    return schema;
                }
                Schema schema = createSchema(component, names);
                if (component.isPrimitive()) {
                    schema = Schema.createArray(schema);
                } else {
                    schema = Schema.createArray(makeNullable(schema));
                }
                schema.addProp(SpecificData.CLASS_PROP, clazz.getName());
                return schema;
            }
        }
        if (type instanceof GenericArrayType) {
            Type component = ((GenericArrayType) type).getGenericComponentType();
            if (component == Byte.TYPE) {
                return Schema.create(Schema.Type.BYTES);
            }
            Schema schema = createSchema(component, names);
            schema = Schema.createArray(makeNullable(schema));
            return schema;
        }
        Class clazz = TypeUtility.getRawType(type, null);
        if (Collection.class.isAssignableFrom(clazz)) {
            ParameterizedType parameterizedType = (ParameterizedType) TypeUtility.refineType(type, Collection.class);
            Type[] types = parameterizedType.getActualTypeArguments();
            Schema schema = createSchema(types[0], names);
            schema = Schema.createArray(makeNullable(schema));
            schema.addProp(SpecificData.CLASS_PROP, clazz.getName());
            return schema;
        }
        if (Map.class.isAssignableFrom(clazz)) {
            ParameterizedType parameterizedType = (ParameterizedType) TypeUtility.refineType(type, Map.class);
            Type[] types = parameterizedType.getActualTypeArguments();
            Class key = TypeUtility.getRawType(types[0], null);
            Class value = TypeUtility.getRawType(types[1], null);
            if (key == String.class || isStringable(key)) {
                Schema schema = Schema.createMap(makeNullable(createSchema(types[1], names)));
                schema.addProp(SpecificData.KEY_CLASS_PROP, key.getName());
                return schema;
            } else if (key != String.class) {
                Schema keySchema = createSchema(types[0], names);
                Schema valueSchema = createSchema(types[1], names);
                String name = KEY_VALUE_RECORD + keySchema.getName() + valueSchema.getName();
                valueSchema = makeNullable(valueSchema);
                Schema.Field keyField = new Schema.Field(KEY_FIELD, keySchema, null, null);
                Schema.Field valueField = new Schema.Field(VALUE_FIELD, valueSchema, null, null);
                Schema elementSchema = Schema.createRecord(name, null, null, false);
                elementSchema.setFields(Arrays.asList(keyField, valueField));
                Schema schema = Schema.createArray(elementSchema);
                schema.addProp(SpecificData.CLASS_PROP, clazz.getName());
                return schema;
            }
        }
        return super.createSchema(type, names);
    }

    @Override
    protected Schema createFieldSchema(Field field, Map<String, Schema> names) {
        Schema schema = super.createFieldSchema(field, names);
        if (field.getType().isPrimitive()) {
            return schema;
        }
        return makeNullable(schema);
    }

    @Override
    public <T> Conversion<T> getConversionByClass(Class<T> datumClass, LogicalType logicalType) {
        // 由于确保datumClass与logicalType一对一,所以可以根据logicalType获取Conversion.
        return (Conversion<T>) super.getConversionFor(logicalType);
    }

    @Override
    public int resolveUnion(Schema union, Object datum) {
        if (datum != null) {
            List<Schema> candidateSchemas = union.getTypes();
            for (int index = 0, size = candidateSchemas.size(); index < size; index++) {
                LogicalType candidateType = candidateSchemas.get(index).getLogicalType();
                if (candidateType != null) {
                    // 尝试匹配datumClass与logicalType
                    Conversion<?> conversion = getConversionByClass(datum.getClass(), candidateType);
                    if (conversion != null) {
                        return index;
                    }
                }
            }
        }
        Integer index = union.getIndexNamed(getSchemaName(datum));
        if (index != null) {
            return index;
        }
        throw new UnresolvedUnionException(union, datum);
    }

}
