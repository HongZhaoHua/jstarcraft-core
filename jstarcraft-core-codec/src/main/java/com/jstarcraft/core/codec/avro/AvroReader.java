package com.jstarcraft.core.codec.avro;

import com.jstarcraft.core.codec.avro.converter.AvroContext;
import com.jstarcraft.core.codec.specification.CodecDefinition;

import java.io.InputStream;

/**
 * @author: MnZzV
 **/
public class AvroReader extends AvroContext {

    private InputStream inputStream;

    public AvroReader(InputStream inputStream, CodecDefinition definition) throws Exception {
        super(definition);
        this.inputStream = inputStream;

    }

    public InputStream getInputStream() {
        return inputStream;
    }
}
