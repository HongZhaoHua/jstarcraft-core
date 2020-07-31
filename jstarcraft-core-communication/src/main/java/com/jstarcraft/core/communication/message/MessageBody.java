package com.jstarcraft.core.communication.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.jstarcraft.core.common.io.IoUtility;
import com.jstarcraft.core.utility.PressUtility;

/**
 * 信息体
 * 
 * <pre>
 * 信息体:[长度(length)][格式(format)][内容(content)]
 * </pre>
 * 
 * @author Birdy
 *
 */
public class MessageBody {

    /** 内容 */
    private byte[] content;

    /** 编解码格式 */
    private MessageFormat type;

    /** 是否Zip */
    private boolean zip;

    public byte[] getContent() {
        return content;
    }

    public MessageFormat getType() {
        return type;
    }

    public boolean isZip() {
        return zip;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object)
            return true;
        if (object == null)
            return false;
        if (getClass() != object.getClass())
            return false;
        MessageBody that = (MessageBody) object;
        EqualsBuilder equal = new EqualsBuilder();
        equal.append(this.content, that.content);
        equal.append(this.type, that.type);
        equal.append(this.zip, that.zip);
        return equal.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hash = new HashCodeBuilder();
        hash.append(content);
        hash.append(type);
        hash.append(zip);
        return hash.toHashCode();
    }

    static MessageBody fromBytes(byte[] data) throws IOException {
        if (data.length == 0) {
            return null;
        }
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        DataInputStream dataInputStream = new DataInputStream(byteArrayInputStream);
        MessageBody value = new MessageBody();
        byte information = dataInputStream.readByte();
        value.type = MessageFormat.fromByte(information);
        value.zip = MessageFormat.isZip(information);

        byte[] content = new byte[dataInputStream.available()];
        IoUtility.read(dataInputStream, content);
        if (value.zip) {
            content = PressUtility.unzip(content, 5, TimeUnit.SECONDS);
        }
        value.content = content;
        return value;
    }

    static byte[] toBytes(MessageBody value) throws IOException {
        if (value == null) {
            return new byte[0];
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataOutputStream dataOutputStream = new DataOutputStream(byteArrayOutputStream);
        byte information = value.zip ? (byte) (value.type.getMark() | MessageFormat.ZIP_MASK) : value.type.getMark();
        dataOutputStream.writeByte(information);
        byte[] content = value.content;
        if (value.zip) {
            content = PressUtility.zip(content, 5);
        }
        IoUtility.write(content, dataOutputStream);
        byte[] data = byteArrayOutputStream.toByteArray();
        return data;
    }

    public static MessageBody instanceOf(boolean zip, MessageFormat type, byte[] content) {
        MessageBody instance = new MessageBody();
        instance.zip = zip;
        instance.type = type;
        instance.content = content;
        return instance;
    }

}
