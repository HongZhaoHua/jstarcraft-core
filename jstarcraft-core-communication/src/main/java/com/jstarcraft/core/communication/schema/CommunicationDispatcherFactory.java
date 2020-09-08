package com.jstarcraft.core.communication.schema;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import com.jstarcraft.core.common.lifecycle.LifecycleState;
import com.jstarcraft.core.communication.annotation.CommunicationModule.ModuleSide;
import com.jstarcraft.core.communication.command.CommandDefinition;
import com.jstarcraft.core.communication.command.CommandDispatcher;
import com.jstarcraft.core.communication.command.CommandStrategy;
import com.jstarcraft.core.communication.session.SessionReceiver;
import com.jstarcraft.core.communication.session.SessionSender;

/**
 * 通讯调度器工厂
 * 
 * @author Birdy
 */
public class CommunicationDispatcherFactory implements FactoryBean<CommandDispatcher>, ApplicationListener<ApplicationEvent> {

    private static final Logger logger = LoggerFactory.getLogger(CommunicationDispatcherFactory.class);

    public static final String DEFINITIONS = "definitions";

    public static final String STRATEGIES = "strategies";

    @Autowired(required = true)
    private ApplicationContext applicationContext;

    private ModuleSide side;
    private List<CommandDefinition> definitions;
    private SessionReceiver receiver;
    private SessionSender sender;
    private Map<String, CommandStrategy> strategies;
    private CommandDispatcher commandDispatcher;
    private long wait;

    @Override
    public synchronized void onApplicationEvent(ApplicationEvent event) {
        getObject();
        if (event instanceof ContextRefreshedEvent) {
            if (commandDispatcher.getState() == null) {
                Set<Object> objects = new HashSet<>();
                String[] beanNames = applicationContext.getBeanDefinitionNames();
                for (String name : beanNames) {
                    final Object bean = applicationContext.getBean(name);
                    for (CommandDefinition definition : definitions) {
                        if (definition.getClazz().isInstance(bean)) {
                            objects.add(bean);
                        }
                    }
                }
                // TODO 配置数量
                commandDispatcher.start(objects, 1, 5);
            }
            return;
        }

        if (event instanceof ContextClosedEvent) {
            if (commandDispatcher.getState() == LifecycleState.STARTED) {
                commandDispatcher.stop();
            }
            return;
        }
    }

    public void setSide(ModuleSide side) {
        this.side = side;
    }

    public void setDefinitions(List<CommandDefinition> definitions) {
        this.definitions = definitions;
    }

    public void setReceiver(SessionReceiver receiver) {
        this.receiver = receiver;
    }

    public void setSender(SessionSender sender) {
        this.sender = sender;
    }

    public void setStrategies(Map<String, CommandStrategy> strategies) {
        this.strategies = strategies;
    }

    public void setWait(long wait) {
        this.wait = wait;
    }

    @Override
    public synchronized CommandDispatcher getObject() {
        if (commandDispatcher == null) {
            commandDispatcher = new CommandDispatcher<>(side, definitions, receiver, sender, strategies, wait);
        }
        return commandDispatcher;
    }

    @Override
    public Class<?> getObjectType() {
        return CommandDispatcher.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

}
