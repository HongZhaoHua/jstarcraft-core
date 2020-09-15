package com.jstarcraft.core.communication.message;

/**
 * 通讯格式
 * 
 * <pre>
 * 由Zip与Mark两部分组成,与{@link MessageBody}的编解码相关.
 * </pre>
 * 
 * @author Birdy
 *
 */
public enum MessageFormat {

    /** 二进制(图片,视频,音频或者第三方格式,由具体业务按需要编码解码) */
    BINARY((byte) 0x01),

    /** CSV(日志格式) */
    CSV((byte) 0x02),

    /** JSON(HTTP格式) */
    JSON((byte) 0x03),

    /** Kryo */
    KRYO((byte) 0x04),

    /** 框架 */
    STANDARD((byte) 0x05);

    /** Zip掩码 */
    public final static byte ZIP_MASK = (byte) 0x80;

    /** 0000 1111(标记掩码) */
    public static final byte MARK_MASK = (byte) 0x0F;

    /** 标记 */
    private final byte mark;

    MessageFormat(byte mark) {
        this.mark = mark;
    }

    public byte getMark() {
        return mark;
    }

    /**
     * 从字节转换为枚举
     * 
     * @param information
     * @return
     */
    public static MessageFormat fromByte(byte information) {
        byte mark = (byte) (information & MARK_MASK);
        switch (mark) {
        case 0x01:
            return BINARY;
        case 0x02:
            return CSV;
        case 0x03:
            return JSON;
        case 0x04:
            return KRYO;
        case 0x05:
            return STANDARD;
        default:
            return null;
        }
    }

    /**
     * 从枚举转换为字节
     * 
     * @param format
     * @param zip
     * @return
     */
    public static byte toByte(MessageFormat format, boolean zip) {
        return zip ? (byte) (format.mark | ZIP_MASK) : format.mark;
    }

    public static boolean isZip(byte information) {
        byte zip = (byte) (information & ZIP_MASK);
        return zip == ZIP_MASK;
    }

}
