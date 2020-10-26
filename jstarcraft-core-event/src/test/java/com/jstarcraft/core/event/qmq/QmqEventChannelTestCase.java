package com.jstarcraft.core.event.qmq;

import com.jstarcraft.core.codec.ContentCodec;
import com.jstarcraft.core.codec.json.JsonContentCodec;
import com.jstarcraft.core.codec.specification.CodecDefinition;
import com.jstarcraft.core.event.EventChannel;
import com.jstarcraft.core.event.EventChannelTestCase;
import com.jstarcraft.core.event.EventMode;
import com.jstarcraft.core.event.MockBroadcastEvent;
import com.jstarcraft.core.event.MockUnicastEvent;

public class QmqEventChannelTestCase extends EventChannelTestCase {

    @Override
    protected EventChannel getEventChannel(EventMode mode) {
        CodecDefinition definition = CodecDefinition.instanceOf(MockUnicastEvent.class, MockBroadcastEvent.class);
        ContentCodec codec = new JsonContentCodec(definition);
        return new QmqEventChannel(mode, "QMQ" + mode, "127.0.0.1", 8080, "token", codec);
    }

}
