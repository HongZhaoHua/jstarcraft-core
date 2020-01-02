package com.jstarcraft.core.transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.redisson.Redisson;
import org.redisson.RedissonNode;
import org.redisson.api.CronSchedule;
import org.redisson.api.ExecutorOptions;
import org.redisson.api.Node;
import org.redisson.api.NodesGroup;
import org.redisson.api.RKeys;
import org.redisson.api.RScheduledExecutorService;
import org.redisson.api.RScript;
import org.redisson.api.RScript.Mode;
import org.redisson.api.RScript.ReturnType;
import org.redisson.client.codec.Codec;
import org.redisson.client.codec.StringCodec;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.redisson.config.RedissonNodeConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.utility.StringUtility;

public class RedissonTestCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public final static double EPSILON = 1E-5;

    @Test
    public void testScript() {
        // 注意此处的编解码器
        Codec codec = new StringCodec();
        Config configuration = new Config();
        configuration.setCodec(codec);
        configuration.useSingleServer().setAddress("redis://127.0.0.1:6379");
        Redisson redisson = null;
        RKeys keys = null;

        try {
            redisson = (Redisson) Redisson.create(configuration);
            keys = redisson.getKeys();
            keys.flushdb();

            RScript script = redisson.getScript();
            String signature = null;
            Object value = null;

            StringBuilder buffer = new StringBuilder();
            buffer.setLength(0);
            buffer.append("local monitor = ARGV[1] .. ARGV[3];");
            buffer.append("local topics = redis.call('lrange', monitor, 0, -1);");
            buffer.append("if (#topics > 0) then");
            buffer.append("    if (#topics ~= #ARGV - 3) then");
            buffer.append("        return false;");
            buffer.append("    end;");
            buffer.append("    for index = 4, #ARGV, 1 do");
            buffer.append("        if (ARGV[index] ~= topics[index - 3]) then");
            buffer.append("            return false;");
            buffer.append("        end;");
            buffer.append("    end;");
            buffer.append("else");
            buffer.append("    for index = 4, #ARGV, 1 do");
            buffer.append("        redis.call('rpush', monitor, ARGV[index]);");
            buffer.append("    end;");
            buffer.append("end;");
            buffer.append("for index = 4, #ARGV, 1 do");
            buffer.append("    local topic = ARGV[2] .. ARGV[index];");
            buffer.append("    redis.call('hincrby', topic, ARGV[3], 1);");
            buffer.append("end;");
            buffer.append("return true;");
            signature = script.scriptLoad(buffer.toString());
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.BOOLEAN, Arrays.asList(), "monitor_", "topic_", "monitor", "topic1", "topic2");
            System.out.println(value);
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.BOOLEAN, Arrays.asList(), "monitor_", "topic_", "monitor", "topic1", "topic2");
            System.out.println(value);
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.BOOLEAN, Arrays.asList(), "monitor_", "topic_", "monitor", "topic1", "topic3");
            System.out.println(value);

            buffer.setLength(0);
            buffer.append("local topic = ARGV[2] .. ARGV[3];");
            buffer.append("local monitors = redis.call('hkeys', topic);");
            buffer.append("for index = 1, #monitors, 1 do");
            buffer.append("    local event = ARGV[1] .. monitors[index];");
            buffer.append("    redis.call('lpush', event, ARGV[4]);");
            buffer.append("end;");
            buffer.append("return monitors;");
            signature = script.scriptLoad(buffer.toString());
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.MULTI, Arrays.asList(), "event_", "topic_", "topic1", "event");
            System.out.println(value);
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.MULTI, Arrays.asList(), "event_", "topic_", "topic2", "event");
            System.out.println(value);
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.MULTI, Arrays.asList(), "event_", "topic_", "topic3", "event");
            System.out.println(value);

            buffer.setLength(0);
            buffer.append("local monitor = ARGV[1] .. ARGV[3];");
            buffer.append("local topics = redis.call('lrange', monitor, 0, -1);");
            buffer.append("local size = 0;");
            buffer.append("for index = 1, #topics, 1 do");
            buffer.append("    local topic = ARGV[2] .. topics[index];");
            buffer.append("    size = redis.call('hincrby', topic, ARGV[3], -1);");
            buffer.append("    if (size == 0) then");
            buffer.append("        redis.call('hdel', topic, ARGV[3]);");
            buffer.append("    end;");
            buffer.append("end;");
            buffer.append("if (size == 0) then");
            buffer.append("    redis.call('del', monitor);");
            buffer.append("    return true;");
            buffer.append("else");
            buffer.append("    return false;");
            buffer.append("end;");
            signature = script.scriptLoad(buffer.toString());
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.BOOLEAN, Arrays.asList(), "monitor_", "topic_", "monitor");
            System.out.println(value);
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.BOOLEAN, Arrays.asList(), "monitor_", "topic_", "monitor");
            System.out.println(value);
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.BOOLEAN, Arrays.asList(), "monitor_", "topic_", "monitor");
            System.out.println(value);
        } catch (Exception exception) {
            logger.error(StringUtility.EMPTY, exception);
            Assert.fail();
        } finally {
            keys.flushdb();
            redisson.shutdown();
        }
    }

}
