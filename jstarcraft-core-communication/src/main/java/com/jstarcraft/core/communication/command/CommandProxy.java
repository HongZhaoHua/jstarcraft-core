package com.jstarcraft.core.communication.command;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import com.jstarcraft.core.communication.message.MessageBody;
import com.jstarcraft.core.communication.session.CommunicationSession;

/**
 * 指令代理
 * 
 * @author Birdy
 *
 */
class CommandProxy implements InvocationHandler {

    /** 指令调度者 */
    private CommandDispatcher dispatcher;

    /** 会话 */
    private CommunicationSession session;

    /** 等待(毫秒) */
    private int wait;

    CommandProxy(CommandDispatcher dispatcher, CommunicationSession session, int wait) {
        this.dispatcher = dispatcher;
        this.session = session;
        this.wait = wait;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] values) throws Throwable {
        CommandDefinition definition = dispatcher.getDefinition(method);
        InputDefinition inputDefinition = definition.getInputDefinition();
        MessageBody body = inputDefinition.getMessageBody(dispatcher.getCodecs(), values);
        CommandContext context = dispatcher.sendRequest(definition, session, body);
        if (context == null) {
            return null;
        }
        return context.getValue();
    }

}
