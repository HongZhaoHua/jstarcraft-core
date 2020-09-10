package com.jstarcraft.core.codec.hessian;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.ContentCodecTestCase;
import com.jstarcraft.core.codec.specification.CodecDefinition;

public class HessianContentCodecTestCase extends ContentCodecTestCase {

    @Override
    protected ContentCodec getContentCodec(CodecDefinition protocolDefinition) {
        HessianContentCodec codec = new HessianContentCodec(protocolDefinition);
        return codec;
    }

}
