package com.jstarcraft.core.codec.csv;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.ContentCodecTestCase;
import com.jstarcraft.core.codec.specification.CodecDefinition;

public class CsvContentCodecTestCase extends ContentCodecTestCase {

    @Override
    protected ContentCodec getContentCodec(CodecDefinition protocolDefinition) {
        CsvContentCodec codec = new CsvContentCodec(protocolDefinition);
        return codec;
    }

}
