package com.jstarcraft.core.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.redisson.Redisson;
import org.redisson.api.RBlockingQueue;
import org.redisson.api.RScript;
import org.redisson.api.RScript.Mode;
import org.redisson.api.RScript.ReturnType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.common.conversion.json.JsonUtility;
import com.jstarcraft.core.utility.KeyValue;
import com.jstarcraft.core.utility.StringUtility;

public class GlobalEventBus implements EventBus {

    private static final Logger logger = LoggerFactory.getLogger(GlobalEventBus.class);

    private final static String eventPrefix = "_event_";

    private final static String monitorPrefix = "_monitor_";

    private final static String topicPrefix = "_topic_";

    private final static String registerLua, unregisterLua, triggerLua;

    private final static List<Object> keys = Collections.EMPTY_LIST;

    static {
        StringBuilder buffer = new StringBuilder();

        // 注册脚本
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
        registerLua = buffer.toString();

        // 注销脚本
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
        unregisterLua = buffer.toString();

        // 触发脚本
        buffer.setLength(0);
        buffer.append("local topic = ARGV[2] .. ARGV[3];");
        buffer.append("local monitors = redis.call('hkeys', topic);");
        buffer.append("for index = 1, #monitors, 1 do");
        buffer.append("    local event = ARGV[1] .. monitors[index];");
        buffer.append("    redis.call('lpush', event, ARGV[4]);");
        buffer.append("end;");
        buffer.append("return ARGV[4];");
        triggerLua = buffer.toString();
    }

    private String name;

    private Redisson redisson;

    private RScript script;

    private String registerSignature;

    private String unregisterSignature;

    private String triggerSignature;

    private Map<Class<? extends EventMonitor>, KeyValue<EventMonitor, EventThread>> threads;

    private static class EventData {

        private String key;

        private String value;

        private EventData() {
        }

        private EventData(String key, String value) {
            this.key = key;
            this.value = value;
        }

        public String getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }

        static EventData fromObject(Object object) {
            try {
                Class<?> clazz = object.getClass();
                String key = clazz.getName();
                String value = JsonUtility.object2String(object);
                EventData data = new EventData(key, value);
                return data;
            } catch (Exception exception) {
                // TODO
                throw new RuntimeException(exception);
            }
        }

        static Object toObject(EventData data) {
            try {
                String key = data.getKey();
                String value = data.getValue();
                Class<?> clazz = Class.forName(key);
                Object object = JsonUtility.string2Object(value, clazz);
                return object;
            } catch (Exception exception) {
                // TODO
                throw new RuntimeException(exception);
            }
        }

    }

    private class EventThread extends Thread {

        private EventMonitor monitor;

        private RBlockingQueue<String> queue;

        private EventThread(EventMonitor monitor, RBlockingQueue<String> queue) {
            this.monitor = monitor;
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    String json = this.queue.take();
                    try {
                        EventData data = JsonUtility.string2Object(json, EventData.class);
                        Object event = EventData.toObject(data);
                        monitor.onEvent(event);
                    } catch (Exception exception) {
                        // 记录日志
                        String message = StringUtility.format("监控器[{}]处理Redis事件[{}]时异常", monitor.getClass(), json);
                        logger.error(message, exception);
                    }
                }
            } catch (InterruptedException exception) {
            }
        }
    };

    public GlobalEventBus(String name, Redisson redisson) {
        this.name = name;
        this.redisson = redisson;

        this.script = redisson.getScript();
        this.registerSignature = script.scriptLoad(registerLua);
        this.unregisterSignature = script.scriptLoad(unregisterLua);
        this.triggerSignature = script.scriptLoad(triggerLua);

        this.threads = new HashMap<>();
    }

    @Override
    public synchronized boolean registerMonitor(EventMonitor monitor, Set<Class<?>> topics) {
        if (!threads.containsKey(monitor.getClass())) {
            Collection<String> values = new TreeSet<>();
            for (Class<?> topic : topics) {
                values.add(topic.getName());
            }
            LinkedList<String> arguments = new LinkedList<>();
            arguments.add(name + monitorPrefix);
            arguments.add(name + topicPrefix);
            arguments.add(monitor.getClass().getName());
            arguments.addAll(values);
            boolean register = script.evalSha(Mode.READ_WRITE, registerSignature, ReturnType.BOOLEAN, keys, arguments.toArray());
            if (register) {
                // TODO
                RBlockingQueue<String> queue = redisson.getBlockingQueue(name + eventPrefix + monitor.getClass().getName());
                EventThread thread = new EventThread(monitor, queue);
                thread.start();
                KeyValue<EventMonitor, EventThread> keyValue = new KeyValue<>(monitor, thread);
                threads.put(monitor.getClass(), keyValue);
            }
            return register;
        }
        return false;
    }

    @Override
    public synchronized boolean unregisterMonitor(EventMonitor monitor) {
        if (threads.containsKey(monitor.getClass())) {
            LinkedList<String> arguments = new LinkedList<>();
            arguments.add(name + monitorPrefix);
            arguments.add(name + topicPrefix);
            arguments.add(monitor.getClass().getName());
            boolean unregister = script.evalSha(Mode.READ_WRITE, unregisterSignature, ReturnType.BOOLEAN, keys, arguments.toArray());
            if (unregister) {
                KeyValue<EventMonitor, EventThread> keyValue = threads.remove(monitor.getClass());
                EventThread thread = keyValue.getValue();
                thread.interrupt();
            }
            return unregister;
        }
        return false;
    }

    @Override
    public synchronized Collection<EventMonitor> getMonitors() {
        List<EventMonitor> monitors = new ArrayList<>(threads.size());
        for (KeyValue<EventMonitor, EventThread> keyValue : threads.values()) {
            monitors.add(keyValue.getKey());
        }
        return monitors;
    }

    @Override
    public void triggerEvent(Object event) {
        EventData data = EventData.fromObject(event);
        String json = JsonUtility.object2String(data);
        // TODO 考虑队列已满的场景
        LinkedList<String> arguments = new LinkedList<>();
        arguments.add(name + eventPrefix);
        arguments.add(name + topicPrefix);
        arguments.add(event.getClass().getName());
        arguments.add(json);
        script.evalSha(Mode.READ_WRITE, triggerSignature, ReturnType.MULTI, keys, arguments.toArray());
    }

}
