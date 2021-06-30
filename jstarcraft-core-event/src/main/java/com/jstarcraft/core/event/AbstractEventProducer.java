package com.jstarcraft.core.event;

import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractEventProducer implements EventProducer {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractEventProducer.class);

    protected static final String CONTEXT = "JStarCraftContext";

    protected static final String DATA = "JStarCraftData";

    protected String name;

    protected Consumer<String> setter;

    protected AbstractEventProducer(String name) {
        this(name, null);
    }

    protected AbstractEventProducer(String name, Consumer<String> setter) {
        this.name = name;
        this.setter = setter;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

}
