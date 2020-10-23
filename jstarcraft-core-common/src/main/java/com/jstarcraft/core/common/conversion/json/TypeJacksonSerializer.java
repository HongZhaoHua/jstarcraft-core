package com.jstarcraft.core.common.conversion.json;

import java.io.IOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.jstarcraft.core.common.reflection.TypeUtility;

public class TypeJacksonSerializer extends JsonSerializer<Type> {

    @Override
    public void serialize(Type value, JsonGenerator generator, SerializerProvider serializers) throws IOException, JsonProcessingException {
        generator.writeString(TypeUtility.type2String(value));
    }

}