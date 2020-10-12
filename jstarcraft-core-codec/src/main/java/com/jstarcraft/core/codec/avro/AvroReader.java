package com.jstarcraft.core.codec.avro;

import java.io.InputStream;

import com.jstarcraft.core.codec.avro.converter.AvroContext;
import com.jstarcraft.core.codec.specification.CodecDefinition;

/**
 * Avro读入器
 * 
 * @author Yue Zhen Wei
 *
 */
@Deprecated
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
