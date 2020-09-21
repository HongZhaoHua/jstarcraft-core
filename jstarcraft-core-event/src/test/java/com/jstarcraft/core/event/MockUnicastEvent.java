package com.jstarcraft.core.event;

public class MockUnicastEvent {

    private int data;

    MockUnicastEvent() {
    }

    public MockUnicastEvent(int data) {
        this.data = data;
    }

    public int getData() {
        return data;
    }

}
