package com.jstarcraft.core.communication;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import com.jstarcraft.core.communication.command.CommandTestSuite;
import com.jstarcraft.core.communication.message.MessageTestSuite;
import com.jstarcraft.core.communication.netty.NettyTestSuite;
import com.jstarcraft.core.communication.session.SessionTestSuite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ CommandTestSuite.class, MessageTestSuite.class, NettyTestSuite.class, SessionTestSuite.class })
public class CommunicationTestSuite {

}
