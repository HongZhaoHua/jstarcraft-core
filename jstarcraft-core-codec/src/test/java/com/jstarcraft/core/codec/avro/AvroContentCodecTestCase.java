package com.jstarcraft.core.codec.avro;

import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.junit.Test;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.ContentCodecTestCase;
import com.jstarcraft.core.codec.MockEnumeration;
import com.jstarcraft.core.codec.specification.CodecDefinition;

public class AvroContentCodecTestCase extends ContentCodecTestCase {

    @Override
    protected ContentCodec getContentCodec(CodecDefinition protocolDefinition) {
        final AvroContentCodec codec = new AvroContentCodec(protocolDefinition, (schema, input) -> {
            return DecoderFactory.get().binaryDecoder(input, null);
//            try {
//                return DecoderFactory.get().jsonDecoder(schema, input);
//            } catch (Exception exception) {
//                throw new RuntimeException(exception);
//            }
        }, (schema, output) -> {
            return EncoderFactory.get().binaryEncoder(output, null);
//            try {
//                return EncoderFactory.get().jsonEncoder(schema, output);
//            } catch (Exception exception) {
//                throw new RuntimeException(exception);
//            }
        });
        return codec;
    }

    @Test
    public void testArray() throws Exception {
        super.testArray();
    }

    @Test
    public void testBoolean() throws Exception {
        super.testBoolean();
    }

    @Test
    public void testInstant() throws Exception {
        super.testInstant();
    }

    @Test
    public void testComplex() throws Exception {
        super.testComplex();
    }

    @Test
    public void testNull() throws Exception {
        super.testNull();
    }

    @Test
    public void testPerformance() {
        super.testPerformance();
    }

    @Test
    public void testNumber() throws Exception {
        super.testNumber();
    }

    @Test
    public void testString() throws Exception {
        super.testString();
    }

    @Test
    public void testType() throws Exception {
        super.testType();
    }

    @Test
    public void testUniMi() throws Exception {
        super.testUniMi();
    }

    @Test
    public void testEnum() throws Exception {
        testConvert(MockEnumeration.class, MockEnumeration.TERRAN);
    }

}
