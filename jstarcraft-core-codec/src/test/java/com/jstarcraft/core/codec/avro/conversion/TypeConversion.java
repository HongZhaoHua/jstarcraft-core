package com.jstarcraft.core.codec.avro.conversion;

import java.lang.reflect.Type;

import org.apache.avro.Conversion;
import org.apache.avro.LogicalType;
import org.apache.avro.Schema;

import com.jstarcraft.core.common.reflection.TypeUtility;

public class TypeConversion extends Conversion {

    private Class clazz;

    private String name;

    public TypeConversion(Class clazz, String name) {
        this.clazz = clazz;
        this.name = name;
    }

    @Override
    public Class getConvertedType() {
        return clazz;
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
