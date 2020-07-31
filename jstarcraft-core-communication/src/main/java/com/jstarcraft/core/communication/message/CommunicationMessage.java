package com.jstarcraft.core.communication.message;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.common.io.IoUtility;
import com.jstarcraft.core.communication.exception.CommunicationException;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 通讯信息(TODO 既要支持序列化serialize,也要支持持久化persistence)
 * 
 * <pre>
 * 信息结构：[0xFFFFFFFF][长度(length):4bytes][校验(check):8bytes][信息头(head)][信息体(body)][信息尾(tail)]
 * 信息头:[长度(length):4bytes][序列(sequence):4bytes][时间(instant):8bytes][模块(module)][指令(command)]
 * 信息体:[长度(length):4bytes][格式(format)1:bytes][内容(content)] TODO 考虑将异常整合到格式[异常(exception)][模块(module)][代号(code)]
 * 信息尾:[长度(length):4bytes][校验(check):8bytes][内容(content)]
 * </pre>
 * 
 * @author Birdy
 */
public class CommunicationMessage {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommunicationMessage.class);

    /** 消息标志 */
    public static final int MESSAGE_MARK = 0xFFFFFFFF;

    /** 信息头 */
    private MessageHead head;
    /** 信息体 */
    private MessageBody body;
    /** 信息尾 */
    private MessageTail tail;

    public MessageHead getHead() {
        return head;
    }

    public MessageBody getBody() {
        return body;
    }

    public MessageTail getTail() {
        return tail;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        CommunicationMessage that = (CommunicationMessage) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.body, that.body);
        equal.append(this.head, that.head);
        equal.append(this.tail, that.tail);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(body);
        hash.append(head);
        hash.append(tail);
        return hash.toHashCode();
    }

    /**
     * 从指定输入流读取消息
     * 
     * @param in
     * @param codecs
     * @return
     * @throws IOException
     */
    public static CommunicationMessage readFrom(DataInputStream in) throws IOException {
        int mark = in.readInt();
        if (mark != MESSAGE_MARK) {
            String string = StringUtility.format("非法消息:信息结构[{}]不匹配", mark);
            throw new CommunicationException(string);
        }
        // 消息长度
        int length = in.readInt();
        long check = in.readLong();
        Checksum checksum = new CRC32();
        // 消息头长度以及数据
        int headLength = in.readInt();
        length -= headLength;
        if (length < 0) {
            throw new CommunicationException();
        }
        byte[] headData = new byte[headLength];
        IoUtility.read(in, headData);
        checksum.update(headData, 0, headData.length);
        // 消息体长度以及数据
        int bodyLength = in.readInt();
        length -= bodyLength;
        if (length < 0) {
            throw new CommunicationException();
        }
        byte[] bodyData = new byte[bodyLength];
        IoUtility.read(in, bodyData);
        checksum.update(bodyData, 0, bodyData.length);
        // 消息尾长度以及数据
        int tailLength = in.readInt();
        length -= tailLength;
        if (length < 0) {
            throw new CommunicationException();
        }
        byte[] tailData = new byte[tailLength];
        IoUtility.read(in, tailData);
        checksum.update(tailData, 0, tailData.length);
        if (check != checksum.getValue()) {
            throw new CommunicationException();
        }
        MessageHead head = MessageHead.fromBytes(headData);
        MessageBody body = MessageBody.fromBytes(bodyData);
        MessageTail tail = MessageTail.fromBytes(tailData);
        CommunicationMessage message = CommunicationMessage.instanceOf(head, body, tail);
        return message;
    }

    /**
     * 将消息写到指定输出流
     * 
     * @param out
     * @param message
     * @param codecs
     * @throws IOException
     */
    public static void writeTo(DataOutputStream out, CommunicationMessage message) throws IOException {
        byte[] headData = MessageHead.toBytes(message.getHead());
        byte[] bodyData = MessageBody.toBytes(message.getBody());
        byte[] tailData = MessageTail.toBytes(message.getTail());
        out.writeInt(MESSAGE_MARK);
        Checksum checksum = new CRC32();
        checksum.update(headData, 0, headData.length);
        checksum.update(bodyData, 0, bodyData.length);
        checksum.update(tailData, 0, tailData.length);

        // TODO 此处消息长度+20是由于check与headData.length,bodyData.length,tailData.length占用的字节长度
        int length = headData.length + bodyData.length + tailData.length + 20;
        out.writeInt(length);
        long check = checksum.getValue();
        out.writeLong(check);
        out.writeInt(headData.length);
        IoUtility.write(headData, out);
        out.writeInt(bodyData.length);
        IoUtility.write(bodyData, out);
        out.writeInt(tailData.length);
        IoUtility.write(tailData, out);
    }

    public static CommunicationMessage instanceOf(MessageHead head, MessageBody body, MessageTail tail) {
        CommunicationMessage instance = new CommunicationMessage();
        instance.head = head;
        instance.body = body;
        instance.tail = tail;
        return instance;
    }

}
