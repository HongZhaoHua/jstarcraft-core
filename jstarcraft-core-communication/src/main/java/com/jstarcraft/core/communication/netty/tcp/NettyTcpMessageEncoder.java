package com.jstarcraft.core.communication.netty.tcp;

import java.io.DataOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.communication.exception.CommunicationException;
import com.jstarcraft.core.communication.message.CommunicationMessage;
import com.jstarcraft.core.communication.netty.NettyBufferOutputStream;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 消息编码器
 * 
 * @author Birdy
 *
 */
class NettyTcpMessageEncoder extends MessageToByteEncoder<CommunicationMessage> {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyTcpMessageEncoder.class);

    NettyTcpMessageEncoder() {
    }

    @Override
    protected void encode(ChannelHandlerContext context, CommunicationMessage encode, ByteBuf buffer) throws Exception {
        try {
            NettyBufferOutputStream outputBuffer = new NettyBufferOutputStream(buffer);
            DataOutputStream dataOutputStream = new DataOutputStream(outputBuffer);
            CommunicationMessage.writeTo(dataOutputStream, encode);
            if (LOGGER.isDebugEnabled()) {
                int length = buffer.readableBytes();
                byte[] bytes = new byte[length];
                buffer.getBytes(buffer.readerIndex(), bytes);
                LOGGER.debug("编码消息:长度{},内容{}", new Object[] { length, bytes });
            }
        } catch (Throwable exception) {
            LOGGER.error("编码消息异常", exception);
            throw new CommunicationException(exception);
        }
    }

}
