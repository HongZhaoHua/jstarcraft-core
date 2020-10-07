package com.jstarcraft.core.codec.avro;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.avro.Schema;
import org.apache.avro.SchemaBuilder;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.junit.Test;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.ContentCodecTestCase;
import com.jstarcraft.core.codec.MockEnumeration;
import com.jstarcraft.core.codec.avro.AvroContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;

/**
 * {@link AvroContentCodec} unit test case
 * 
 * @author MnZzV
 */
public class AvroContentCodecTestCase extends ContentCodecTestCase {

    @Override
    protected ContentCodec getContentCodec(CodecDefinition protocolDefinition) {
        final AvroContentCodec codec = new AvroContentCodec(protocolDefinition);
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

    @Test
    public void testReadWriteArvo() throws Exception {

        Schema schema = SchemaBuilder.record("parquet").fields().requiredString("title").requiredString("when").name("where").type(SchemaBuilder.record("where").fields().requiredFloat("longitude").requiredFloat("latitude").endRecord()).noDefault().endRecord();
        GenericRecord parquet = new GenericData.Record(schema);
        parquet.put("title", "title");
        parquet.put("when", "2020-01-01 00:00:00");
        GenericRecord where = new GenericData.Record(schema.getField("where").schema());
        where.put("longitude", 0F);
        where.put("latitude", 0F);
        parquet.put("where", where);

//            ParquetWriter writer = AvroParquetWriter.builder(output).withSchema(schema).build();
//            writer.write(parquet);
//            writer.close();
    }

    public static void main(String[] args) {
        AtomicInteger deep = new AtomicInteger();
        deep.incrementAndGet();
        System.err.println(deep);
    }

}
