package com.jstarcraft.core.event.redis;

import org.redisson.Redisson;
import org.redisson.client.codec.ByteArrayCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventBus;
import com.jstarcraft.core.event.EventMode;

public abstract class RedisEventBus extends AbstractEventBus {

    protected static final Logger logger = LoggerFactory.getLogger(RedisEventBus.class);

    protected static final ByteArrayCodec byteCodec = new ByteArrayCodec();

    protected Redisson redisson;

    protected ContentCodec codec;

    protected RedisEventBus(EventMode mode, String name, Redisson redisson, ContentCodec codec) {
        super(mode, name);
        this.name = name;
        this.redisson = redisson;
        this.codec = codec;
    }

}
