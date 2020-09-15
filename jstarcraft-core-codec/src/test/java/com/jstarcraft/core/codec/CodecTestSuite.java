package com.jstarcraft.core.codec;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.codec.csv.CsvContentCodecTestCase;
import com.jstarcraft.core.codec.hessian.HessianContentCodecTestCase;
import com.jstarcraft.core.codec.json.JsonContentCodecTestCase;
import com.jstarcraft.core.codec.kryo.KryoContentCodecTestCase;
import com.jstarcraft.core.codec.msgpack.MessagePackContentCodecTestCase;
import com.jstarcraft.core.codec.standard.StandardContentCodecTestCase;
import com.jstarcraft.core.codec.xml.XmlContentCodecTestCase;
import com.jstarcraft.core.codec.yaml.YamlContentCodecTestCase;

@RunWith(Suite.class)
@SuiteClasses({

        CodecDefinitionTestCase.class,

        CsvContentCodecTestCase.class,

        HessianContentCodecTestCase.class,

        JsonContentCodecTestCase.class,

        KryoContentCodecTestCase.class,

        MessagePackContentCodecTestCase.class,

        StandardContentCodecTestCase.class,

        XmlContentCodecTestCase.class,

        YamlContentCodecTestCase.class })
public class CodecTestSuite {

}
