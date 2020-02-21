package com.jstarcraft.core.storage.identification;

import org.junit.After;
import org.junit.Before;
import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

public abstract class RedisIdentityFactoryTestCase extends IdentityFactoryTestCase {

    protected Redisson redisson;

    protected RKeys keys;

    @Before
    public void testBefore() {
        // 注意此处的编解码器
        Codec codec = new JsonJacksonCodec();
        Config configuration = new Config();
        configuration.setCodec(codec);
        configuration.useSingleServer().setAddress("redis://127.0.0.1:6379");
        redisson = (Redisson) Redisson.create(configuration);
        keys = redisson.getKeys();
        keys.flushdb();
    }

    @After
    public void testAfter() {
        keys.flushdb();
        redisson.shutdown();
    }

}
