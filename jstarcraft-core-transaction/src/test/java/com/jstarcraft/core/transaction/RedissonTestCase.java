package com.jstarcraft.core.transaction;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.hamcrest.CoreMatchers;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
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

import com.jstarcraft.core.utility.RandomUtility;
import com.jstarcraft.core.utility.StringUtility;

import redis.embedded.RedisServer;

public class RedissonTestCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public final static double EPSILON = 1E-5;

    private static RedisServer redis;

    @BeforeClass
    public static void startRedis() {
        redis = RedisServer.builder().port(6379).setting("maxmemory 64M").build();
        redis.start();
    }

    @AfterClass
    public static void stopRedis() {
        redis.stop();
    }

    @Test
    public void testConnection() {
        // 注意此处的编解码器
        Codec codec = new JsonJacksonCodec();
        Config configuration = new Config();
        configuration.setCodec(codec);
        configuration.useSingleServer().setAddress("redis://127.0.0.1:6379");
        Redisson redisson = null;

        try {
            redisson = (Redisson) Redisson.create(configuration);

            NodesGroup<Node> group = redisson.getNodesGroup();
            Collection<Node> nodes = group.getNodes();
            Assert.assertThat(nodes.size(), CoreMatchers.equalTo(1));
            Node node = group.getNode("redis://127.0.0.1:6379");
            Assert.assertTrue(node.ping());
        } catch (Exception exception) {
            logger.error(StringUtility.EMPTY, exception);
            Assert.fail();
        }
    }

    private double getMedian(ArrayList<Integer> data) {
        int lenght = data.size();
        // 中位数
        double median = 0;
        Collections.sort(data);
        if (lenght % 2 == 0)
            median = (data.get((lenght - 1) / 2) + data.get(lenght / 2)) / 2D;
        else
            median = data.get(lenght / 2);
        return median;
    }

    @Test
    public void testEval() {
        // 注意此处的编解码器
        Codec codec = new StringCodec();
        Config configuration = new Config();
        configuration.setCodec(codec);
        configuration.useSingleServer().setAddress("redis://127.0.0.1:6379");
        Redisson redisson = null;

        try {
            redisson = (Redisson) Redisson.create(configuration);

            RScript script = redisson.getScript();
            List<Object> value = script.eval(RScript.Mode.READ_ONLY, "return {true, 2, 3.33, 'string' ,nil, 'test'}", RScript.ReturnType.MULTI, Collections.emptyList());
            Assert.assertTrue(value.contains(1L));
            Assert.assertTrue(value.contains(2L));
            Assert.assertTrue(value.contains(3L));
            Assert.assertTrue(value.contains("string"));
            Assert.assertEquals(4, value.size());
        } catch (Exception exception) {
            logger.error(StringUtility.EMPTY, exception);
            Assert.fail();
        }
    }

    @Test
    public void testScript() {
        // 注意此处的编解码器
        Codec codec = new JsonJacksonCodec();
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

            // 自定义脚本,相当于
            /**
             * <pre>
             * boolean exists = (limiter != null && limiter.contains(key));
             * int current = number;
             * if (exists) {
             *     current += limiter.get(key);
             * }
             * if (current > limit) {
             *     return false;
             * }
             * limiter.put(key, current);
             * if (!exists) {
             *     setExpire(limiter, expire);
             * }
             * return true;
             * </pre>
             */
            signature = script.scriptLoad("local exists = redis.call('hexists', KEYS[1], ARGV[1]); local current = tonumber(ARGV[2]); local limit = tonumber(ARGV[3]); if (exists == 1) then current = current + redis.call('hget', KEYS[1], ARGV[1]); end; if (current > limit) then return false; end; redis.call('hset', KEYS[1], ARGV[1], current); if (exists == 0) then redis.call('expire', KEYS[1], ARGV[4]); end; return true;");
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.BOOLEAN, Arrays.asList("limiter"), "left", 1000, 1000, 5);
            Assert.assertEquals(Boolean.TRUE, value);
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.BOOLEAN, Arrays.asList("limiter"), "left", 1000, 1000, 5);
            Assert.assertEquals(Boolean.FALSE, value);
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.BOOLEAN, Arrays.asList("limiter"), "right", 1000, 1000, 5);
            Assert.assertEquals(Boolean.TRUE, value);
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.BOOLEAN, Arrays.asList("limiter"), "right", 1000, 1000, 5);
            Assert.assertEquals(Boolean.FALSE, value);
            Thread.sleep(5000);
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.BOOLEAN, Arrays.asList("limiter"), "left", 1000, 1000, 5);
            Assert.assertEquals(Boolean.TRUE, value);
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.BOOLEAN, Arrays.asList("limiter"), "right", 1000, 1000, 5);
            Assert.assertEquals(Boolean.TRUE, value);

            // 自定义脚本,相当于
            /**
             * <pre>
             * Integer index = attributes.get("key");
             * if (index == null) {
             *     index = attributes.size();
             *     attributes.put("key", index);
             * }
             * return index;
             * </pre>
             */
            signature = script.scriptLoad("local index = redis.call('hget', KEYS[1], ARGV[1]); if (not index) then index = redis.call('incr', KEYS[2]) - 1; redis.call('hset', KEYS[1], ARGV[1], index); end; return index;");
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.INTEGER, Arrays.asList("attribute_indexes", "attribute_size"), "to");
            Assert.assertThat(Number.class.cast(value).longValue(), CoreMatchers.equalTo(0L));
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.INTEGER, Arrays.asList("attribute_indexes", "attribute_size"), "be");
            Assert.assertThat(Number.class.cast(value).longValue(), CoreMatchers.equalTo(1L));
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.INTEGER, Arrays.asList("attribute_indexes", "attribute_size"), "or");
            Assert.assertThat(Number.class.cast(value).longValue(), CoreMatchers.equalTo(2L));
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.INTEGER, Arrays.asList("attribute_indexes", "attribute_size"), "not");
            Assert.assertThat(Number.class.cast(value).longValue(), CoreMatchers.equalTo(3L));
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.INTEGER, Arrays.asList("attribute_indexes", "attribute_size"), "to");
            Assert.assertThat(Number.class.cast(value).longValue(), CoreMatchers.equalTo(0L));
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.INTEGER, Arrays.asList("attribute_indexes", "attribute_size"), "be");
            Assert.assertThat(Number.class.cast(value).longValue(), CoreMatchers.equalTo(1L));

            // 自定义脚本,相当于
            /**
             * <pre>
             * Double maximum = boundaries.get("maximum");
             * Double minimum = boundaries.get("minimum");
             * if (maximum != null && minimum != null) {
             *     if (value > maximum) {
             *         maximum = value;
             *         boundaries.put("maximum", maximum);
             *     }
             *     if (value < minimum) {
             *         minimum = value;
             *         boundaries.put("minimum", minimum);
             *     }
             * } else {
             *     maximum = value;
             *     minimum = value;
             *     boundaries.put("maximum", maximum);
             *     boundaries.put("minimum", minimum);
             * }
             * return new Double[] { maximum, minimum };
             * </pre>
             */
            signature = script.scriptLoad("local boundaries = redis.call('hmget', KEYS[1], 'maximum', 'minimum'); local maximum = boundaries[1]; local minimum = boundaries[2]; if (maximum and minimum) then if (ARGV[1] > maximum) then maximum = ARGV[1]; redis.call('hset', KEYS[1], 'maximum', maximum); end; if (ARGV[1] < minimum) then minimum = ARGV[1]; redis.call('hset', KEYS[1], 'minimum', minimum); end; else maximum = ARGV[1]; minimum = ARGV[1]; redis.call('hmset', KEYS[1], 'maximum', maximum, 'minimum', minimum); end; return { maximum, minimum };");
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.MULTI, Arrays.asList("attribute_boundaries"), 0D);
            Assert.assertThat(Number.class.cast(List.class.cast(value).get(0)).doubleValue(), CoreMatchers.equalTo(0D));
            Assert.assertThat(Number.class.cast(List.class.cast(value).get(1)).doubleValue(), CoreMatchers.equalTo(0D));
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.MULTI, Arrays.asList("attribute_boundaries"), 1D);
            Assert.assertThat(Number.class.cast(List.class.cast(value).get(0)).doubleValue(), CoreMatchers.equalTo(1D));
            Assert.assertThat(Number.class.cast(List.class.cast(value).get(1)).doubleValue(), CoreMatchers.equalTo(0D));
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.MULTI, Arrays.asList("attribute_boundaries"), -1.5D);
            Assert.assertThat(Number.class.cast(List.class.cast(value).get(0)).doubleValue(), CoreMatchers.equalTo(1D));
            Assert.assertThat(Number.class.cast(List.class.cast(value).get(1)).doubleValue(), CoreMatchers.equalTo(-1.5D));
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.MULTI, Arrays.asList("attribute_boundaries"), 0D);
            Assert.assertThat(Number.class.cast(List.class.cast(value).get(0)).doubleValue(), CoreMatchers.equalTo(1D));
            Assert.assertThat(Number.class.cast(List.class.cast(value).get(1)).doubleValue(), CoreMatchers.equalTo(-1.5D));

            // 自定义脚本,相当于
            /**
             * <pre>
             * if (count % 2 == 0) {
             *     maximumQueue.offer(term.getValue());
             *     double value = maximumQueue.poll();
             *     minimumQueue.offer(value);
             * } else {
             *     minimumQueue.offer(term.getValue());
             *     double value = minimumQueue.poll();
             *     maximumQueue.offer(value);
             * }
             * count++;
             * return count;
             * </pre>
             */
            int length = 100;
            ArrayList<Integer> data = new ArrayList<>(length);
            signature = script.scriptLoad("local count = redis.call('incr', KEYS[1]) - 1; local maximum = KEYS[2]; local minimum = KEYS[3]; local key = KEYS[4]; local score = ARGV[1]; if (count % 2 == 0) then redis.call('zadd', maximum, score, key); local values = redis.call('zrange', maximum, -1, -1, 'withscores'); redis.call('zrem', maximum, values[1]); redis.call('zadd', minimum, values[2], values[1]); else redis.call('zadd', minimum, score, key); local values = redis.call('zrange', minimum, 0, 0, 'withscores'); redis.call('zrem', minimum, values[1]); redis.call('zadd', maximum, values[2], values[1]); end; return count + 1; ");
            for (int index = 0; index < length; index++) {
                int number = RandomUtility.randomInteger(index);
                value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.INTEGER, Arrays.asList("attribute_count", "attribute_maximum", "attribute_minimum", String.valueOf("key" + index)), number);
                data.add(number);
            }

            // 自定义脚本,相当于
            /**
             * <pre>
             * if (count % 2 == 0) {
             *     return new Double((minimumQueue.peek() + maximumQueue.peek())) / 2D;
             * } else {
             *     return new Double(minimumQueue.peek());
             * }
             * </pre>
             */
            signature = script.scriptLoad("local count = redis.call('get', KEYS[1]); local maximum = KEYS[2]; local minimum = KEYS[3]; local median; if (count % 2 == 0) then maximum = redis.call('zrange', maximum, -1, -1, 'withscores'); minimum = redis.call('zrange', minimum, 0, 0, 'withscores'); median = (maximum[2] + minimum[2]) / 2; return tostring(median); else minimum = redis.call('zrange', minimum, 0, 0, 'withscores'); median = minimum[2]; return tostring(median); end;");
            value = script.evalSha(Mode.READ_WRITE, signature, ReturnType.INTEGER, Arrays.asList("attribute_count", "attribute_maximum", "attribute_minimum"));
            Assert.assertEquals("中位数值比较", getMedian(data), Number.class.cast(value).doubleValue(), EPSILON);
        } catch (Exception exception) {
            logger.error(StringUtility.EMPTY, exception);
            Assert.fail();
        } finally {
            keys.flushdb();
            redisson.shutdown();
        }
    }

    @Test
    public void testSchedule() {
        // 注意此处的编解码器
        Codec codec = new JsonJacksonCodec();
        Config redisConfiguration = new Config();
        redisConfiguration.setCodec(codec);
        redisConfiguration.useSingleServer().setAddress("redis://127.0.0.1:6379");
        Redisson redisson = null;
        RedissonNodeConfig nodeConfiguration = new RedissonNodeConfig(redisConfiguration);
        nodeConfiguration.setExecutorServiceWorkers(Collections.singletonMap("test", 1));
        RedissonNode node = null;
        RKeys keys = null;

        try {
            redisson = (Redisson) Redisson.create(redisConfiguration);
            node = RedissonNode.create(nodeConfiguration);
            node.start();
            keys = redisson.getKeys();
            keys.flushdb();

            RScheduledExecutorService executor = redisson.getExecutorService("test", ExecutorOptions.defaults().taskRetryInterval(5, TimeUnit.SECONDS));
            Assert.assertEquals(redisson.getAtomicLong("counter").get(), 0L);
            executor.schedule(new RedissonTask("counter"), CronSchedule.of("0/1 * * * * ?"));
            Thread.sleep(1000);
            Assert.assertEquals(redisson.getAtomicLong("counter").get(), 1L);
        } catch (Exception exception) {
            logger.error(StringUtility.EMPTY, exception);
            Assert.fail();
        } finally {
            keys.flushdb();
            node.shutdown();
            redisson.shutdown();
        }
    }

}
