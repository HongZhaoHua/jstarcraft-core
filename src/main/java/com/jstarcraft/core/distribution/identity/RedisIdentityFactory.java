package com.jstarcraft.core.distribution.identity;

import java.util.LinkedHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于Redis标识管理器
 * 
 * @author Birdy
 *
 */
public abstract class RedisIdentityFactory implements IdentityFactory {

	protected static final Logger LOGGER = LoggerFactory.getLogger(RedisIdentityFactory.class);

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

	abstract protected long getLimit(long step);

	protected RedisIdentityFactory(long step, int partition, int sequenceBit) {
		this.step = step;
		int partitionBit = IdentityDefinition.DATA_BIT - sequenceBit;
		this.definition = new IdentityDefinition(partitionBit, sequenceBit);
		this.partition = partition;
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

			Long maximum = definition.make(partition, -1L);
			Long minimum = definition.make(partition, 0L);
			Long current = definition.make(partition, sequence);
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
