package com.jstarcraft.core.communication.netty;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.jstarcraft.core.communication.netty.tcp.NettyTcpMessageDecodeTestCase;
import com.jstarcraft.core.communication.netty.tcp.NettyTcpTestCase;
import com.jstarcraft.core.communication.netty.udp.NettyUdpTestCase;

@RunWith(Suite.class)
@SuiteClasses({ NettyTcpMessageDecodeTestCase.class, NettyTcpTestCase.class, NettyUdpTestCase.class })
public class NettyTestSuite {

}
