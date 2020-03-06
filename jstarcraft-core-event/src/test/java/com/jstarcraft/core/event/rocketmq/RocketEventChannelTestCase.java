package com.jstarcraft.core.event.rocketmq;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.event.EventChannel;
import com.jstarcraft.core.event.EventChannelTestCase;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.MockEvent;
import com.jstarcraft.core.event.rocket.RocketEventChannel;

public class RocketEventChannelTestCase extends EventChannelTestCase {

    @Override
    protected EventChannel getEventChannel(EventMode mode) {
        CodecDefinition definition = CodecDefinition.instanceOf(MockEvent.class);
        ContentCodec codec = new JsonContentCodec(definition);
        return new RocketEventChannel(mode, "Rocket" + mode, "localhost:9876", codec);
    }

}
