package com.jstarcraft.core.communication.netty;

import java.io.IOException;
import java.io.OutputStream;

import io.netty.buffer.ByteBuf;

/**
 * Netty缓冲输出流
 * 
 * @author Birdy
 *
 */
public class NettyBufferOutputStream extends OutputStream {

    private final ByteBuf buffer;

    public NettyBufferOutputStream(ByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public void write(int data) throws IOException {
        buffer.writeByte(data);
    }

    @Override
    public String toString() {
        return "NettyBufferOutputStream [buffer=" + buffer + "]";
    }

}
