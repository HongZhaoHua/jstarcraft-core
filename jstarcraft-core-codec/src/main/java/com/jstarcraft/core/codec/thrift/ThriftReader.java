package com.jstarcraft.core.codec.thrift;

import org.apache.thrift.protocol.TProtocol;

import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.codec.thrift.converter.ThriftContext;

/**
 * 协议读入器
 * 
 * <pre>
 * 每次解码都必须使用
 * </pre>
 * 
 * @author Birdy
 */
public class ThriftReader extends ThriftContext {

    public ThriftReader(CodecDefinition definition, TProtocol protocol) {
        super(definition);
        super.protocol = protocol;
    }

}
