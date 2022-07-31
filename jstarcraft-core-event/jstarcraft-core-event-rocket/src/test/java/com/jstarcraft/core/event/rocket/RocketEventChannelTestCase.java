package com.jstarcraft.core.event.rocket;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.event.EventChannel;
import com.jstarcraft.core.event.EventChannelTestCase;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.MockBroadcastEvent;
import com.jstarcraft.core.event.MockUnicastEvent;

public class RocketEventChannelTestCase extends EventChannelTestCase {

    @Override
    protected EventChannel getEventChannel(EventMode mode) {
        CodecDefinition definition = CodecDefinition.instanceOf(MockUnicastEvent.class, MockBroadcastEvent.class);
        ContentCodec codec = new JsonContentCodec(definition);
        return new RocketEventChannel(mode, "RocketMQ" + mode, "localhost:9876", codec);
    }

}
