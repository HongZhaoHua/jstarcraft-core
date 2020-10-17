package com.jstarcraft.core.transaction.redis;

import org.junit.After;
import org.junit.Before;
import org.redisson.Redisson;
import org.redisson.api.RKeys;
import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;

import com.jstarcraft.core.transaction.TransactionManager;
import com.jstarcraft.core.transaction.TransactionManagerTestCase;

public class RedisTransactionManagerTestCase extends TransactionManagerTestCase {

    private Redisson redisson;
    private RKeys keys;

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

    @Override
    protected TransactionManager getDistributionManager() {
        return new RedisTransactionManager(redisson.getScript());
    }

}
