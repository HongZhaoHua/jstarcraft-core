package com.jstarcraft.core.codec.avro.conversion;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

public class AtomicLongConversion extends Conversion<AtomicLong> {

    @Override
    public Class<AtomicLong> getConvertedType() {
        return AtomicLong.class;
    }

    @Override
    public Schema getRecommendedSchema() {
        return new LogicalType(getLogicalTypeName()).addToSchema(Schema.create(Schema.Type.LONG));
    }

    @Override
    public String getLogicalTypeName() {
        return "atomic-long";
    }

    @Override
    public AtomicLong fromLong(Long value, Schema schema, LogicalType type) {
        return new AtomicLong(value);
    }

    @Override
    public Long toLong(AtomicLong value, Schema schema, LogicalType type) {
        return value.get();
    }

}
