package com.jstarcraft.core.storage.elasticsearch.converter;

import java.time.Instant;

import org.springframework.core.convert.converter.Converter;

public class NumberToInstantConverter implements Converter<Number, Instant> {

    public Instant convert(Number data) {
        return Instant.ofEpochMilli(data.longValue());
    }

}
