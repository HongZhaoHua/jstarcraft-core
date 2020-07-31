package com.jstarcraft.core.communication.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.common.io.IoUtility;

/**
 * 信息尾
 * 
 * <pre>
 * 信息尾:[长度(length)][校验(check)][内容(content)]
 * </pre>
 * 
 * @author Birdy
 */
public class MessageTail {

    /** 校验 */
    private long check;
    /** 内容 */
    private byte[] content;

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        MessageTail that = (MessageTail) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.check, that.check);
        equal.append(this.content, that.content);
        return true;
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(check);
        hash.append(content);
        return hash.toHashCode();
    }

    static MessageTail fromBytes(byte[] data) throws IOException {
        if (data.length == 0) {
            return null;
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        MessageTail value = new MessageTail();
        value.check = dataInputStream.readLong();
        value.content = new byte[dataInputStream.available()];
        IoUtility.read(dataInputStream, value.content);
        return value;
    }

    static byte[] toBytes(MessageTail value) throws IOException {
        if (value == null) {
            return new byte[0];
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        dataOutputStream.writeLong(value.check);
        IoUtility.write(value.content, dataOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        return data;
    }

    public static MessageTail instanceOf(long check, byte... content) {
        MessageTail instance = new MessageTail();
        instance.check = check;
        instance.content = content;
        return instance;
    }

}
