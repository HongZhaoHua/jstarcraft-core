package com.jstarcraft.core.event.redis;

import org.redisson.Redisson;
import org.redisson.client.codec.ByteArrayCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.event.AbstractEventChannel;
import com.jstarcraft.core.event.EventMode;

/**
 * Redis事件管道
 * 
 * @author Birdy
 *
 */
public abstract class RedisEventChannel extends AbstractEventChannel {

    protected static final Logger logger = LoggerFactory.getLogger(RedisEventChannel.class);

    protected static final ByteArrayCodec byteCodec = new ByteArrayCodec();

    protected Redisson redisson;

    protected ContentCodec codec;

    protected RedisEventChannel(EventMode mode, String name, Redisson redisson, ContentCodec codec) {
        super(mode, name);
        this.name = name;
        this.redisson = redisson;
        this.codec = codec;
    }

}
