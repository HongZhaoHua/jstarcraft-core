package com.jstarcraft.core.codec.standard;

import java.io.OutputStream;

import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.codec.standard.converter.StandardContext;

/**
 * Standard协议写出器
 * 
 * <pre>
 * 每次编码都必须使用
 * </pre>
 * 
 * @author Birdy
 */
public class StandardWriter extends StandardContext {

    private OutputStream outputStream;

    public StandardWriter(OutputStream outputStream, CodecDefinition definition) {
        super(definition);
        this.outputStream = outputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

}
