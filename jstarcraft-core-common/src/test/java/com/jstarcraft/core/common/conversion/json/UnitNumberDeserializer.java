package com.jstarcraft.core.common.conversion.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class UnitNumberDeserializer extends JsonDeserializer<UnitNumber> {

    @Override
    public UnitNumber deserialize(JsonParser parser, DeserializationContext context) throws IOException, JsonProcessingException {
        ObjectCodec objectCodec = parser.getCodec();
        JsonNode node = objectCodec.readTree(parser);
        double value = node.get(0).asDouble();
        int unit = node.get(1).asInt();
        return new UnitNumber(unit, value);
    }

}
