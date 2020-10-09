package com.jstarcraft.core.codec.avro.conversion;

import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

public class AtomicBooleanConversion extends Conversion<AtomicBoolean> {

    @Override
    public Class<AtomicBoolean> getConvertedType() {
        return AtomicBoolean.class;
    }

    @Override
    public Schema getRecommendedSchema() {
        return new LogicalType(getLogicalTypeName()).addToSchema(Schema.create(Schema.Type.BOOLEAN));
    }

    @Override
    public String getLogicalTypeName() {
        return "atomic-boolean";
    }

    @Override
    public AtomicBoolean fromBoolean(Boolean value, Schema schema, LogicalType type) {
        return new AtomicBoolean(value);
    }

    @Override
    public Boolean toBoolean(AtomicBoolean value, Schema schema, LogicalType type) {
        return value.get();
    }

}
