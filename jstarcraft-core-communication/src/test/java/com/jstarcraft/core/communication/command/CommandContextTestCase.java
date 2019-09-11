package com.jstarcraft.core.communication.command;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.communication.exception.CommunicationWaitException;

public class CommandContextTestCase {

    @Test
    public void testSetValue() {
        CommandContext context = new CommandContext(null, 0);
        Object value = new Object();
        context.setValue(value);
        Assert.assertThat(context.getValue(), CoreMatchers.equalTo(value));
    }

    @Test
    public void testSetException() {
        CommandContext context = new CommandContext(null, 0);
        context.setException(new CommunicationWaitException());
        try {
            context.getValue();
            Assert.fail();
        } catch (CommunicationWaitException exception) {
        }
    }

}
