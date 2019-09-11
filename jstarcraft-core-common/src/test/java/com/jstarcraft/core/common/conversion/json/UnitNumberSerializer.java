package com.jstarcraft.core.common.conversion.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class UnitNumberSerializer extends JsonSerializer<UnitNumber> {

    @Override
    public void serialize(UnitNumber value, JsonGenerator generator, SerializerProvider serializers) throws IOException {
        generator.writeStartArray();
        generator.writeNumber(value.getValue());
        generator.writeNumber(value.getUnit());
        generator.writeEndArray();
    }

}
