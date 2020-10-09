package com.jstarcraft.core.codec.avro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.UnresolvedUnionException;
import org.apache.avro.data.TimeConversions.TimestampMillisConversion;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData.AllowNull;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.avro.specific.SpecificData;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.codec.MockComplexObject;
import com.jstarcraft.core.codec.MockEnumeration;
import com.jstarcraft.core.common.reflection.TypeUtility;
import com.jstarcraft.core.utility.StringUtility;

public class AvroTestCase {

    public static class TypeConversion extends Conversion {

        private Class clazz;

        private String name;

        public TypeConversion(Class clazz, String name) {
            this.clazz = clazz;
            this.name = name;
        }

        @Override
        public Class getConvertedType() {
            return Type.class;
        }

        @Override
        public Schema getRecommendedSchema() {
            return new LogicalType(getLogicalTypeName()).addToSchema(Schema.create(Schema.Type.STRING));
        }

        @Override
        public String getLogicalTypeName() {
            return name;
        }

        @Override
        public Object fromCharSequence(CharSequence value, Schema schema, LogicalType type) {
            return TypeUtility.string2Type(value.toString());
        }

        @Override
        public CharSequence toCharSequence(Object value, Schema schema, LogicalType type) {
            return TypeUtility.type2String((Type) value);
        }

    }

    private AllowNull avroData = new AllowNull() {

        @Override
        public <T> Conversion<T> getConversionByClass(Class<T> datumClass, LogicalType logicalType) {
            return (Conversion<T>) super.getConversionFor(logicalType);
        }

        @Override
        protected Schema createSchema(Type type, Map<String, Schema> names) {
            if (type instanceof Class) { // Class
                Class<?> clazz = (Class<?>) type;
                if (clazz.isArray()) { // array
                    Class component = clazz.getComponentType();
                    if (component == Byte.TYPE) { // byte array
                        Schema schema = Schema.create(Schema.Type.BYTES);
                        schema.addProp(SpecificData.CLASS_PROP, clazz.getName());
                        return schema;
                    }
                    Schema schema = createSchema(component, names);
                    if (component.isPrimitive()) {
                        schema = SchemaBuilder.array().items().type(schema);
                    } else {
                        schema = SchemaBuilder.array().items().nullable().type(schema);
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
                schema = SchemaBuilder.array().items().nullable().type(schema);
                return schema;
            }
            Class clazz = TypeUtility.getRawType(type, null);
            if (Collection.class.isAssignableFrom(clazz)) {
                type = TypeUtility.refineType(type, Collection.class);
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Type[] types = parameterizedType.getActualTypeArguments();
                Schema schema = createSchema(types[0], names);
                schema = SchemaBuilder.array().items().nullable().type(schema);
                schema.addProp(SpecificData.CLASS_PROP, clazz.getName());
                return schema;
            }
            return super.createSchema(type, names);
        }

        @Override
        public int resolveUnion(Schema union, Object datum) {
            List<Schema> candidates = union.getTypes();
            for (int index = 0; index < candidates.size(); index += 1) {
                LogicalType candidateType = candidates.get(index).getLogicalType();
                if (candidateType != null) {
                    Conversion<?> conversion = super.getConversionFor(candidateType);
                    if (conversion != null) {
                        return index;
                    }
                }
            }
            Integer index = union.getIndexNamed(getSchemaName(datum));
            if (index != null) {
                return index;
            }
            throw new UnresolvedUnionException(union, datum);
        }

    };

    {
        avroData.addLogicalTypeConversion(new TimestampMillisConversion());
        avroData.addLogicalTypeConversion(new TypeConversion(Type.class, "type"));
    }

    protected void testConvert(Type type, Object value) throws Exception {
        Schema schema = avroData.getSchema(type);
        ReflectDatumWriter writer = new ReflectDatumWriter<>(schema, avroData);
        ReflectDatumReader reader = new ReflectDatumReader<>(schema, schema, avroData);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Encoder encoder = EncoderFactory.get().directBinaryEncoder(output, null);
            writer.write(value, encoder);
            encoder.flush();
            byte[] data = output.toByteArray();
            try (ByteArrayInputStream input = new ByteArrayInputStream(data)) {
                Decoder decoder = DecoderFactory.get().directBinaryDecoder(input, null);
                if (type == AtomicBoolean.class) {
                    AtomicBoolean left = (AtomicBoolean) value;
                    AtomicBoolean right = (AtomicBoolean) reader.read(null, decoder);
                    Assert.assertTrue(TypeUtility.isInstance(left, type));
                    Assert.assertTrue(TypeUtility.isInstance(right, type));
                    Assert.assertThat(right.get(), CoreMatchers.equalTo(left.get()));
                } else if (type == AtomicInteger.class || type == AtomicLong.class) {
                    Number left = (Number) value;
                    Number right = (Number) reader.read(null, decoder);
                    Assert.assertTrue(TypeUtility.isInstance(left, type));
                    Assert.assertTrue(TypeUtility.isInstance(right, type));
                    Assert.assertThat(right.longValue(), CoreMatchers.equalTo(left.longValue()));
                } else {
                    Object left = value;
                    Object right = reader.read(null, decoder);
                    if (value != null) {
                        Assert.assertTrue(TypeUtility.isInstance(left, type));
                        Assert.assertTrue(TypeUtility.isInstance(right, type));
                    }
                    Assert.assertThat(right, CoreMatchers.equalTo(left));
                }
            }
        }
    }

    @Test
    public void testAvro() throws Exception {
        MockComplexObject mock = MockComplexObject.instanceOf(0, "birdy", null, 10, Instant.now(), MockEnumeration.TERRAN);
        testConvert(MockComplexObject.class, mock);
    }

}
