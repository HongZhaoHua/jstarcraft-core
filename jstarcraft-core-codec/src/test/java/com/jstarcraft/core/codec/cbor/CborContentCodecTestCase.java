package com.jstarcraft.core.codec.cbor;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.ContentCodecTestCase;
import com.jstarcraft.core.codec.MockEnumeration;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import org.junit.Test;

/**
 * Cbor编解码器测试类
 *
 * @author Yue Zhen Wei
 */
public class CborContentCodecTestCase extends ContentCodecTestCase {

    @Override
    protected ContentCodec getContentCodec(CodecDefinition protocolDefinition) {
        return new CborContentCodec();
    }

//    @Test
//    public void testType() throws Exception {
//        super.testType();
//    }
}
