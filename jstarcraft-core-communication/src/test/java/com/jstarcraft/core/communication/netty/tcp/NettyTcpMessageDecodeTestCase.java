package com.jstarcraft.core.communication.netty.tcp;

import java.io.DataOutputStream;

import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.communication.message.CommunicationMessage;
import com.jstarcraft.core.communication.message.MessageBody;
import com.jstarcraft.core.communication.message.MessageFormat;
import com.jstarcraft.core.communication.message.MessageHead;
import com.jstarcraft.core.communication.message.MessageTail;
import com.jstarcraft.core.communication.netty.NettyBufferOutputStream;
import com.jstarcraft.core.utility.StringUtility;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;

public class NettyTcpMessageDecodeTestCase {

    @Test
    public void testDecode() throws Exception {
        // 合法消息
        ByteBuf legalBuffer = Unpooled.buffer();
        MessageHead head = MessageHead.instanceOf(1, (byte) 1, (byte) 1);
        MessageBody body = MessageBody.instanceOf(true, MessageFormat.JSON, "洪钊桦".getBytes(StringUtility.CHARSET));
        MessageTail tail = MessageTail.instanceOf(10);
        CommunicationMessage message = CommunicationMessage.instanceOf(head, body, tail);
        NettyBufferOutputStream outputBuffer = new NettyBufferOutputStream(legalBuffer);
        DataOutputStream dataOutputStream = new DataOutputStream(outputBuffer);
        CommunicationMessage.writeTo(dataOutputStream, message);
        // 非法消息
        ByteBuf illegalBuffer = Unpooled.buffer();
        for (int data = 0; data < 10; data++) {
            illegalBuffer.writeByte(data);
        }

        MockConnector connector = new MockConnector();
        EmbeddedChannel channel = new EmbeddedChannel(new NettyTcpMessageDecoder(connector));

        channel.writeInbound(legalBuffer.copy());
        Assert.assertEquals(1, connector.getCount());
        Assert.assertEquals(message, connector.getMessage());

        channel.writeInbound(legalBuffer.retainedSlice(0, 10));
        Assert.assertEquals(1, connector.getCount());
        channel.writeInbound(legalBuffer.retainedSlice(10, legalBuffer.readableBytes() - 10));
        Assert.assertEquals(2, connector.getCount());
        Assert.assertEquals(message, connector.getMessage());

        channel.writeInbound(illegalBuffer.copy());
        Assert.assertEquals(2, connector.getCount());

        channel.writeInbound(legalBuffer.retainedSlice(0, 10));
        Assert.assertEquals(2, connector.getCount());
        channel.writeInbound(legalBuffer.retainedSlice(10, legalBuffer.readableBytes() - 10));
        Assert.assertEquals(3, connector.getCount());
        Assert.assertEquals(message, connector.getMessage());

        ByteBuf compositeBuffer = Unpooled.buffer();
        compositeBuffer.writeBytes(illegalBuffer.array(), 0, illegalBuffer.readableBytes());
        compositeBuffer.writeBytes(legalBuffer.array(), 0, 20);
        channel.writeInbound(compositeBuffer.copy());
        Assert.assertEquals(3, connector.getCount());
        channel.writeInbound(legalBuffer.retainedSlice(20, legalBuffer.readableBytes() - 20));
        Assert.assertEquals(4, connector.getCount());
        Assert.assertEquals(message, connector.getMessage());
    }

}
