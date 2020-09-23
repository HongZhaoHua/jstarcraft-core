package com.jstarcraft.core.codec.thrift;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.ContentCodecTestCase;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;

import java.lang.reflect.Type;

/**
 * @author huang hong fei
 * @createAt 2020/9/23
 * @description
 */
public class ThriftContentCodecTestCase extends ContentCodecTestCase {

    @Override
    protected ContentCodec getContentCodec(CodecDefinition protocolDefinition) {
        ThriftContentCodec codec = new ThriftContentCodec(protocolDefinition);
        return codec;
    }

    @Override
    public void testArray() throws Exception {
        super.testArray();
    }

    @Override
    public void testBoolean() throws Exception {
        super.testBoolean();
    }

    @Override
    public void testInstant() throws Exception {
        super.testInstant();
    }

    @Override
    public void testComplex() throws Exception {
        super.testComplex();
    }

    @Override
    public void testNumber() throws Exception {
        super.testNumber();
    }

    @Override
    public void testNull() throws Exception {
        super.testNull();
    }

    @Override
    public void testString() throws Exception {
        super.testString();
    }

    @Override
    public void testType() throws Exception {
        super.testType();
    }

    @Override
    public void testUniMi() throws Exception {
        super.testUniMi();
    }

    @Override
    public void testPerformance() {
        super.testPerformance();
    }

    @Override
    protected void testConvert(Type type, Object value) throws Exception {
        super.testConvert(type, value);
    }
}
