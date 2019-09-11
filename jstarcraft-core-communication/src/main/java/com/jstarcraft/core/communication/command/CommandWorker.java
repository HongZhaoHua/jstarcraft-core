package com.jstarcraft.core.communication.command;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.communication.message.CommunicationMessage;
import com.jstarcraft.core.communication.message.MessageBody;
import com.jstarcraft.core.communication.message.MessageHead;
import com.jstarcraft.core.communication.session.CommunicationSession;
import com.jstarcraft.core.utility.StringUtility;

/**
 * 指令执行者
 * 
 * @author Birdy
 *
 */
class CommandWorker implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandWorker.class);

    /** 指令调度器 */
    private CommandDispatcher dispatcher;
    /** 指令定义 */
    private CommandDefinition definition;
    /** 指令消息 */
    private CommunicationMessage message;
    /** 指令会话 */
    private CommunicationSession<?> session;
    /** 指令对象 */
    private Object object;

    CommandWorker(CommandDispatcher dispatcher, CommandDefinition definition, CommunicationMessage message, CommunicationSession<?> session, Object object) {
        this.dispatcher = dispatcher;
        this.definition = definition;
        this.message = message;
        this.session = session;
        this.object = object;
    }

    @Override
    public void run() {
        InputDefinition inputDefinition = definition.getInputDefinition();
        OutputDefinition outputDefinition = definition.getOutputDefinition();
        Method method = definition.getMethod();
        Object[] requests = inputDefinition.getInputValues(dispatcher.getCodecs(), message, session);
        Object response = null;
        try {
            response = method.invoke(object, requests);
        } catch (Exception exception) {
            String string = StringUtility.format("工作者执行方法[{}]时异常", method);
            LOGGER.error(string, exception);
        }
        MessageBody body = outputDefinition.getMessageBody(dispatcher.getCodecs(), response);
        MessageHead head = message.getHead();
        dispatcher.sendResponse(definition, session, head.getSequence(), body);
    }

}
