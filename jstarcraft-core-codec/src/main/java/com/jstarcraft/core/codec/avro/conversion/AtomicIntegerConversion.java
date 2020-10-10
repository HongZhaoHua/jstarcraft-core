package com.jstarcraft.core.codec.avro.conversion;

import java.util.concurrent.atomic.AtomicInteger;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

public class AtomicIntegerConversion extends Conversion<AtomicInteger> {

    @Override
    public Class<AtomicInteger> getConvertedType() {
        return AtomicInteger.class;
    }

    @Override
    public Schema getRecommendedSchema() {
        return new LogicalType(getLogicalTypeName()).addToSchema(Schema.create(Schema.Type.INT));
    }

    @Override
    public String getLogicalTypeName() {
        return "atomic-integer";
    }

    @Override
    public AtomicInteger fromInt(Integer value, Schema schema, LogicalType type) {
        return new AtomicInteger(value);
    }

    @Override
    public Integer toInt(AtomicInteger value, Schema schema, LogicalType type) {
        return value.get();
    }

}
