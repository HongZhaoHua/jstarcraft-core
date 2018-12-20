package com.jstarcraft.core.codec;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.jstarcraft.core.codec.csv.CsvContentCodecTestCase;
import com.jstarcraft.core.codec.json.JsonContentCodecTestCase;
import com.jstarcraft.core.codec.kryo.KryoContentCodecTestCase;
import com.jstarcraft.core.codec.protocolbufferx.ProtocolBufferXContentCodecTestCase;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CodecDefinitionTestCase.class, CsvContentCodecTestCase.class, JsonContentCodecTestCase.class, KryoContentCodecTestCase.class, ProtocolBufferXContentCodecTestCase.class })
public class CodecTestSuite {

}
