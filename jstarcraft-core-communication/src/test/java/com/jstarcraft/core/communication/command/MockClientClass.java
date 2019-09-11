package com.jstarcraft.core.communication.command;

import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.stereotype.Component;

/**
 * 客户端通讯模块
 * 
 * @author Birdy
 *
 */
@Component
public class MockClientClass implements MockClientInterface {

    private AtomicInteger times = new AtomicInteger();

    @Override
    public void testExecute(UserObject object) {
        times.incrementAndGet();
    }

    public int getTimes() {
        return times.get();
    }

}
