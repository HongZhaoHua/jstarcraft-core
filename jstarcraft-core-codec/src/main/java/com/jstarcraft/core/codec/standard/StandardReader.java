package com.jstarcraft.core.codec.standard;

import java.io.InputStream;

import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.codec.standard.converter.StandardContext;

/**
 * Standard协议读入器
 * 
 * <pre>
 * 每次解码都必须使用
 * </pre>
 * 
 * @author Birdy
 */
public class StandardReader extends StandardContext {

    private InputStream inputStream;

    public StandardReader(InputStream inputStream, CodecDefinition definition) {
        super(definition);
        this.inputStream = inputStream;
    }

    public InputStream getInputStream() {
        return inputStream;
    }

}
