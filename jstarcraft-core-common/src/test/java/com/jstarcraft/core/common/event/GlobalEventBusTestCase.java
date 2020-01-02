package com.jstarcraft.core.common.event;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.config.Config;

public class GlobalEventBusTestCase extends EventBusTestCase {

    private static Redisson redisson;
    private static RKeys keys;

    @Override
    protected EventBus getEventBus() {
        GlobalEventBus bus = new GlobalEventBus("redis", redisson);
        return bus;
    }

    @BeforeClass
    public static void start() {
        // 注意此处的编解码器
        Codec codec = new StringCodec();
        Config configuration = new Config();
        configuration.setCodec(codec);
        configuration.useSingleServer().setAddress("redis://127.0.0.1:6379");

        redisson = (Redisson) Redisson.create(configuration);
        keys = redisson.getKeys();
        keys.flushdb();
    }

    @AfterClass
    public static void stop() {
        keys.flushdb();
        redisson.shutdown();
    }

}
