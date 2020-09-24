package com.jstarcraft.core.codec.thrift;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.ContentCodecTestCase;
import com.jstarcraft.core.codec.specification.CodecDefinition;

public class ThriftContentCodecTestCase extends ContentCodecTestCase {

    @Override
    protected ContentCodec getContentCodec(CodecDefinition protocolDefinition) {
        ThriftContentCodec codec = new ThriftContentCodec(protocolDefinition);
        return codec;
    }

}
