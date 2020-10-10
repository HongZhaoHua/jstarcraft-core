package com.jstarcraft.core.codec.avro.conversion;

import java.util.Date;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

public class DateConversion extends Conversion<Date> {

    @Override
    public Class<Date> getConvertedType() {
        return Date.class;
    }

    @Override
    public Schema getRecommendedSchema() {
        return new LogicalType(getLogicalTypeName()).addToSchema(Schema.create(Schema.Type.LONG));
    }

    @Override
    public String getLogicalTypeName() {
        return "date";
    }

    @Override
    public Date fromLong(Long value, Schema schema, LogicalType type) {
        return new Date(value);
    }

    @Override
    public Long toLong(Date value, Schema schema, LogicalType type) {
        return value.getTime();
    }

}
