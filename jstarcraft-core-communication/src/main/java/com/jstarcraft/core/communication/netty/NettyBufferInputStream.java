package com.jstarcraft.core.communication.netty;

import java.io.IOException;
import java.io.InputStream;

import io.netty.buffer.ByteBuf;

/**
 * Netty缓冲输入流
 * 
 * @author Birdy
 *
 */
public class NettyBufferInputStream extends InputStream {

    private final ByteBuf buffer;

    public NettyBufferInputStream(ByteBuf buffer) {
        this.buffer = buffer;
    }

    public int available() throws IOException {
        return buffer.readableBytes();
    }

    @Override
    public int read() throws IOException {
        int data = buffer.readByte() & 0xFF;
        return data;
    }

    @Override
    public String toString() {
        return "NettyBufferInputStream [buffer=" + buffer + "]";
    }

}