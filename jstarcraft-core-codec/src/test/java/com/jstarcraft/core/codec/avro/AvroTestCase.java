package com.jstarcraft.core.codec.avro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.reflect.Type;
import java.time.Instant;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;
import org.apache.avro.data.TimeConversions.TimestampMillisConversion;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.Encoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.codec.MockComplexObject;
import com.jstarcraft.core.codec.MockEnumeration;
import com.jstarcraft.core.common.reflection.TypeUtility;

public class AvroTestCase {

    public static class TypeConversion extends Conversion<Type> {

        @Override
        public Class<Type> getConvertedType() {
            return Type.class;
        }

        @Override
        public Schema getRecommendedSchema() {
            return new LogicalType("type").addToSchema(Schema.create(Schema.Type.STRING));
        }

        @Override
        public String getLogicalTypeName() {
            return "type";
        }

        @Override
        public Type fromCharSequence(CharSequence value, Schema schema, LogicalType type) {
            return TypeUtility.string2Type(value.toString());
        }

        @Override
        public CharSequence toCharSequence(Type value, Schema schema, LogicalType type) {
            return TypeUtility.type2String(value);
        }

    }

    @Test
    public void testAvro() throws Exception {
        ReflectData avroData = new ReflectData() {

            @Override
            public <T> Conversion<T> getConversionByClass(Class<T> datumClass, LogicalType logicalType) {
                return (Conversion<T>) super.getConversionFor(logicalType);
            }

        };
        avroData.addLogicalTypeConversion(new TimestampMillisConversion());
        avroData.addLogicalTypeConversion(new TypeConversion());
        Schema schema = avroData.getSchema(MockComplexObject.class);
        ReflectDatumWriter<MockComplexObject> writer = new ReflectDatumWriter<>(schema, avroData);
        ReflectDatumReader<MockComplexObject> reader = new ReflectDatumReader<>(schema, schema, avroData);
        MockComplexObject left = MockComplexObject.instanceOf(0, "birdy", "hong", 10, Instant.now(), MockEnumeration.TERRAN);
        try (ByteArrayOutputStream output = new ByteArrayOutputStream()) {
            Encoder encoder = EncoderFactory.get().directBinaryEncoder(output, null);
            writer.write(left, encoder);
            encoder.flush();
            byte[] data = output.toByteArray();
            try (ByteArrayInputStream input = new ByteArrayInputStream(data)) {
                Decoder decoder = DecoderFactory.get().directBinaryDecoder(input, null);
                MockComplexObject right = reader.read(null, decoder);
                Assert.assertEquals(left, right);
            }
        }
    }

}
