package com.jstarcraft.core.common.identification;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于中间件标识管理器
 * 
 * @author Birdy
 *
 */
public abstract class MiddlewareIdentityFactory implements IdentityFactory {

    protected static final Logger LOGGER = LoggerFactory.getLogger(MiddlewareIdentityFactory.class);

    public final static long MAXIMUM_LONG_VALUE = 0x7FFFFFFFFFFFFFFFL;

    /** 步伐 */
    protected final long step;

    /** 序列 */
    protected long sequence;

    /** 限制 */
    protected long limit;

    /** 标识定义 */
    protected final IdentityDefinition definition;

    /** 分区 */
    protected final int partition;

    protected final long maximum;

    protected final long minimum;

    abstract protected long getLimit(long step);

    protected MiddlewareIdentityFactory(IdentityDefinition definition, int partition, long step) {
        List<IdentitySection> sections = definition.getSections();
        if (sections.size() != 2) {
            throw new IllegalArgumentException();
        }
        this.definition = definition;
        this.partition = partition;
        this.step = step;
        this.maximum = definition.make(partition, -1L);
        this.minimum = definition.make(partition, 0L);
    }

    @Override
    public IdentityDefinition getDefinition() {
        return definition;
    }

    @Override
    public int getPartition() {
        return partition;
    }

    @Override
    public synchronized long getSequence() {
        if (sequence == limit) {
            limit = getLimit(step);
            sequence = limit - step;

            long current = definition.make(partition, sequence);
            if (current < minimum || current > maximum) {
                String message = String.format("序列异常,边界范围[{}, {}],当前值{}", minimum, maximum, current);
                LOGGER.error(message);
                new RuntimeException(message);
            }
        }
        Long current = definition.make(partition, sequence++);
        return current;
    }

}
