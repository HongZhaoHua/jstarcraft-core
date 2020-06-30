package com.jstarcraft.core.storage.elasticsearch.converter;

import java.time.Instant;

import org.springframework.core.convert.converter.Converter;

public class InstantToNumberConverter implements Converter<Instant, Number> {

    public Number convert(Instant data) {
        return data.toEpochMilli();
    }

}
