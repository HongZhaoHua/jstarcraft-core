package com.jstarcraft.core.event;

public class MockBroadcastEvent {

    private int data;

    MockBroadcastEvent() {
    }

    public MockBroadcastEvent(int data) {
        this.data = data;
    }

    public int getData() {
        return data;
    }

}
