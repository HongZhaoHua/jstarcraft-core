package com.jstarcraft.core.common.conversion.json;

import java.io.IOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.jstarcraft.core.common.reflection.TypeUtility;

public class TypeJacksonDeserializer extends JsonDeserializer<Type> {

    @Override
    public Type deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        return TypeUtility.string2Type(parser.getValueAsString());
    }

}
