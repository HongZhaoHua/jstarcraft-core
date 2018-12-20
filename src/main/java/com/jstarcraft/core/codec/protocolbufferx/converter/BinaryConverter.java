package com.jstarcraft.core.codec.protocolbufferx.converter;

/**
 * 二进制转换器
 * 
 * <pre>
 * 参考ProtocolBuffer协议与ASF3协议
 * </pre>
 * 
 * @author Birdy
 *
 * @param <T>
 */
public abstract class BinaryConverter<T> implements ProtocolConverter<T> {

	/** 1111 0000(类型掩码) */
	private static final byte TYPE_MASK = (byte) 0xF0;

	/** 0000 1111(标记掩码) */
	private static final byte MARK_MASK = (byte) 0x0F;

	/**
	 * 通过指定字节数据获取类型码
	 * 
	 * @param data
	 * @return
	 */
	public static byte getType(byte data) {
		byte code = (byte) (data & TYPE_MASK);
		return code;
	}

	/**
	 * 通过指定字节数据获取标记码
	 * 
	 * @param data
	 * @return
	 */
	public static byte getMark(byte data) {
		byte mark = (byte) (data & MARK_MASK);
		return mark;
	}

}
