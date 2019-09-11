package com.jstarcraft.core.communication.netty.tcp;

import java.io.DataInputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.communication.exception.CommunicationException;
import com.jstarcraft.core.communication.message.CommunicationMessage;
import com.jstarcraft.core.communication.netty.NettyBufferInputStream;
import com.jstarcraft.core.communication.netty.NettyConnector;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

/**
 * 消息解码器
 * 
 * @author Birdy
 *
 */
class NettyTcpMessageDecoder extends ByteToMessageDecoder {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyTcpMessageDecoder.class);

    /** 连接器 */
    private final NettyConnector<Channel> connector;

    NettyTcpMessageDecoder(NettyConnector<Channel> connector) {
        this.connector = connector;
    }

    @Override
    protected void decode(ChannelHandlerContext context, ByteBuf buffer, List<Object> decode) throws Exception {
        try {
            Channel channel = context.channel();
            if (LOGGER.isDebugEnabled()) {
                int length = buffer.readableBytes();
                byte[] bytes = new byte[length];
                buffer.getBytes(buffer.readerIndex(), bytes);
                LOGGER.debug("解码消息:长度{},内容{}", new Object[] { length, bytes });
            }

            while (buffer.readableBytes() > 4) {
                // 定位MESSAGE_MARK
                int count = 0;
                for (int index = 0, size = buffer.readableBytes(); index < size; index++) {
                    if (buffer.readByte() == -1) {
                        // 累计合法字节
                        count++;
                        if (count == 4) {
                            break;
                        }
                    } else {
                        // 丢弃非法字节
                        buffer.markReaderIndex();
                        count = 0;
                    }
                }

                if (count != 4) {
                    buffer.resetReaderIndex();
                    return;
                }

                if (buffer.readableBytes() < 4) {
                    buffer.resetReaderIndex();
                    return;
                }

                // 消息长度
                int length = buffer.readInt();
                buffer.resetReaderIndex();
                // 此处消息长度+8是由于mark与length占用的长度大小
                if (buffer.readableBytes() < length + 8) {
                    return;
                }

                NettyBufferInputStream inputBuffer = new NettyBufferInputStream(buffer);
                DataInputStream dataInputStream = new DataInputStream(inputBuffer);
                CommunicationMessage message = CommunicationMessage.readFrom(dataInputStream);
                connector.checkData(channel, message);
                buffer.markReaderIndex();
            }
        } catch (Exception exception) {
            LOGGER.error("解码消息异常", exception);
            throw new CommunicationException(exception);
        }
    }

}
