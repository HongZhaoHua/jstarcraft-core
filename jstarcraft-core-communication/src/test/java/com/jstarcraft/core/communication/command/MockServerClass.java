package com.jstarcraft.core.communication.command;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.jstarcraft.core.communication.annotation.CommandVariable;

/**
 * 服务端通讯模块
 * 
 * @author Birdy
 *
 */
public class MockServerClass implements MockServerInterface {

    AtomicInteger createTimes = new AtomicInteger();
    AtomicInteger updateTimes = new AtomicInteger();
    AtomicInteger deleteTimes = new AtomicInteger();
    AtomicInteger getTimes = new AtomicInteger();

    private ConcurrentHashMap<Long, UserObject> users = new ConcurrentHashMap<>();

    @Override
    public UserObject getUser(Long id) {
        getTimes.incrementAndGet();
        return users.get(id);
    }

    @Override
    public UserObject createUser(UserObject object) {
        createTimes.incrementAndGet();
        users.put(object.getId(), object);
        return object;
    }

    @Override
    public String updateUser(Long id, String name) {
        updateTimes.incrementAndGet();
        UserObject object = users.get(id);
        if (object == null) {
            return null;
        }
        String value = object.getName();
        object.setName(name);
        return value;
    }

    @Override
    public String deleteUser(Long id) {
        deleteTimes.incrementAndGet();
        UserObject object = users.remove(id);
        return object.getName();
    }

    @Override
    public @CommandVariable int addition(@CommandVariable int number) {
        return number + number;
    }

    @Override
    public @CommandVariable int addition(@CommandVariable(property = "left") int left, @CommandVariable(property = "right") int right) {
        return left + right;
    }

    public int getTimes() {
        int times = createTimes.get() + updateTimes.get() + deleteTimes.get() + getTimes.get();
        return times;
    }

    @Override
    public @CommandVariable String md5(@CommandVariable String data) {
        return data;
    }

}
