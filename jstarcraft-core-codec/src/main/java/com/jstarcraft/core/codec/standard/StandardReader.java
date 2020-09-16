package com.jstarcraft.core.codec.standard;

import java.io.InputStream;

import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.codec.standard.converter.ProtocolContext;

/**
 * Standard协议读入器
 * 
 * <pre>
 * 每次解码都必须使用
 * </pre>
 * 
 * @author Birdy
 */
public class StandardReader extends ProtocolContext {

    private InputStream inputStream;

    public StandardReader(InputStream inputStream, CodecDefinition definition) {
        super(definition);
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

}
