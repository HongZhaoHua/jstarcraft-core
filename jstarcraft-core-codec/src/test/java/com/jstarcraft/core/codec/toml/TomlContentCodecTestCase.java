package com.jstarcraft.core.codec.toml;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.ContentCodecTestCase;
import com.jstarcraft.core.codec.MockEnumeration;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.teesoft.jackson.dataformat.toml.TOMLMapper;
import org.junit.Test;
import sun.jvm.hotspot.utilities.Assert;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Yue Zhen Wei
 **/
public class TomlContentCodecTestCase extends ContentCodecTestCase {

    @Override
    protected ContentCodec getContentCodec(CodecDefinition protocolDefinition) {
        return new TomlContentCodec();
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

    public static void main(String[] args) throws Exception {
        TOMLMapper mapper = new TOMLMapper();
        MyValue value = new MyValue();
        value.name = "justdb";
        value.age = 1;
        String toml = mapper.writeValueAsString(value);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        mapper.writeValue(outputStream, value);
        System.err.println(outputStream.toByteArray().length);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(outputStream.toByteArray());
        MyValue valueNew = mapper.readValue(byteArrayInputStream, MyValue.class);
        Assert.that(valueNew.equals(value), "!!!");

    }

}
class MyValue {
    public String name;
    public int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
// NOTE: if using getters/setters, can keep fields `protected` or `private`
}
