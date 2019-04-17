package com.jstarcraft.core.codec.protocolbufferx;

import java.io.OutputStream;

import com.jstarcraft.core.codec.protocolbufferx.converter.ProtocolContext;
import com.jstarcraft.core.codec.specification.CodecDefinition;

/**
 * 协议写出器
 * 
 * <pre>
 * 每次编码都必须使用
 * </pre>
 * 
 * @author Birdy
 */
public class ProtocolWriter extends ProtocolContext {

	private OutputStream outputStream;

	public ProtocolWriter(OutputStream outputStream, CodecDefinition definition) {
		super(definition);
		this.outputStream = outputStream;
	}

	public OutputStream getOutputStream() {
		return outputStream;
	}

}
