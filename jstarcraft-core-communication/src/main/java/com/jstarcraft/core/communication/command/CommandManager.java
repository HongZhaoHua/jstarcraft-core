package com.jstarcraft.core.communication.command;

import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.LinkedList;

import com.jstarcraft.core.communication.message.MessageBody;
import com.jstarcraft.core.communication.session.CommunicationSession;
import com.jstarcraft.core.communication.session.SessionManager;

/**
 * 指令管理器
 * 
 * @author Birdy
 *
 */
public class CommandManager {

    /** 指令调度器 */
    private CommandDispatcher commandDispatcher;

    /** 会话管理器 */
    private SessionManager sessionManager;

    public CommandManager(CommandDispatcher commandDispatcher, SessionManager sessionManager) {
        this.commandDispatcher = commandDispatcher;
        this.sessionManager = sessionManager;
    }

    private CommandContext executeCommand(CommandDefinition definition, CommunicationSession session, Object... arguments) {
        InputDefinition inputDefinition = definition.getInputDefinition();
        MessageBody body = inputDefinition.getMessageBody(commandDispatcher.getCodecs(), arguments);
        return commandDispatcher.sendRequest(definition, session, body);
    }

    public CommandContext executeCommand(CommandDefinition definition, String key, Object... arguments) {
        CommunicationSession session = sessionManager.getSession(key);
        if (session == null) {
            return null;
        }
        return executeCommand(definition, session, arguments);
    }

    public Collection<CommandContext> executeCommands(CommandDefinition definition, Collection<String> keys, Object... arguments) {
        Collection<CommandContext> contexts = new LinkedList<>();
        for (String key : keys) {
            contexts.add(executeCommand(definition, key, arguments));
        }
        return contexts;
    }

    public Collection<CommandContext> executeCommands(CommandDefinition definition, Object... arguments) {
        Collection<CommunicationSession> sessions = sessionManager.getSessions(null);
        Collection<CommandContext> contexts = new LinkedList<>();
        for (CommunicationSession session : sessions) {
            contexts.add(executeCommand(definition, session, arguments));
        }
        return contexts;
    }

    private <I> I getProxy(Class<I> clazz, CommunicationSession session, int wait) {
        CommandProxy proxy = new CommandProxy(commandDispatcher, session, wait);
        I instance = (I) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), new Class[] { clazz }, proxy);
        return instance;
    }

    public <I> I getProxy(Class<I> clazz, String key, int wait) {
        CommunicationSession session = sessionManager.getSession(key);
        return getProxy(clazz, session, wait);
    };

    public <I> Collection<I> getProxies(Class<I> clazz, Collection<String> keys, int wait) {
        Collection<I> proxies = new LinkedList<>();
        for (String key : keys) {
            proxies.add(getProxy(clazz, key, wait));
        }
        return proxies;
    }

    public <I> Collection<I> getProxies(Class<I> clazz, int wait) {
        Collection<I> proxies = new LinkedList<>();
        Collection<CommunicationSession> sessions = sessionManager.getSessions(null);
        for (CommunicationSession session : sessions) {
            proxies.add(getProxy(clazz, session, wait));
        }
        return proxies;
    }

}
