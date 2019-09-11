package com.jstarcraft.core.communication.command;

public class MockStrategy implements CommandStrategy {

    @Override
    public void execute(Runnable task) {
        task.run();
    }

}
