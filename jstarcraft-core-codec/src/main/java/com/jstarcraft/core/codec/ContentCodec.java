package com.jstarcraft.core.codec;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;

/**
 * 消息内容编解码器
 * 
 * @author Birdy
 */
public interface ContentCodec {

    /**
     * 解码内容
     * 
     * @param type
     * @param content
     * @return
     */
    Object decode(Type type, byte[] content);

    /**
     * 解码内容
     * 
     * @param type
     * @param stream
     * @return
     */
    Object decode(Type type, InputStream stream);

    /**
     * 编码内容
     * 
     * @param type
     * @param content
     * @return
     */
    byte[] encode(Type type, Object content);

    /**
     * 编码内容
     * 
     * @param type
     * @param content
     * @param stream
     */
    void encode(Type type, Object content, OutputStream stream);

}
