package com.jstarcraft.core.communication.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.time.Instant;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.common.io.IoUtility;

/**
 * 信息头
 * 
 * <pre>
 * 信息头:[长度(length)][序列(sequence)][时间(instant)][指令(command)][模块(module)]
 * </pre>
 * 
 * @author Birdy
 */
public class MessageHead {

    /** 序列号 */
    private int sequence;
    /** 时间 */
    // TODO 可能重构为long类型
    private Instant instant;
    /** 指令 */
    private byte command;
    /** 模块 */
    private byte[] module;

    public int getSequence() {
        return sequence;
    }

    public Instant getInstant() {
        return instant;
    }

    public byte getCommand() {
        return command;
    }

    public byte[] getModule() {
        return module;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        MessageHead that = (MessageHead) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.command, that.command);
        equal.append(this.module, that.module);
        equal.append(this.sequence, that.sequence);
        equal.append(this.instant, that.instant);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(command);
        hash.append(module);
        hash.append(sequence);
        hash.append(instant);
        return hash.toHashCode();
    }

    /**
     * 获取信息头信息，该方法会执行读取头长度
     * 
     * @param data 信息数据
     * @return
     * @throws IOException
     */
    static MessageHead fromBytes(byte[] data) throws IOException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        MessageHead value = new MessageHead();
        value.sequence = dataInputStream.readInt();
        value.instant = Instant.ofEpochMilli(dataInputStream.readLong());
        value.command = dataInputStream.readByte();
        value.module = new byte[dataInputStream.available()];
        IoUtility.read(dataInputStream, value.module);
        return value;
    }

    /**
     * 将信息头转换为 byte[] 表示格式
     * 
     * @param header
     * @return
     * @throws IOException
     */
    static byte[] toBytes(MessageHead value) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeInt(value.sequence);
        dataOutputStream.writeLong(value.instant.toEpochMilli());
        dataOutputStream.writeByte(value.command);
        IoUtility.write(value.module, dataOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        return data;
    }

    public static MessageHead instanceOf(int sequence, byte command, byte... modules) {
        MessageHead instance = new MessageHead();
        instance.sequence = sequence;
        instance.instant = Instant.now();
        instance.command = command;
        instance.module = modules;
        return instance;
    }

}
