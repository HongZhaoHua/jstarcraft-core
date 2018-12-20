package com.jstarcraft.core.distribution.identity;

import java.util.LinkedHashMap;

import org.redisson.api.RAtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于Redis标识管理器
 * 
 * @author Birdy
 *
 */
public class RedisIdentityFactory implements IdentityFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(RedisIdentityFactory.class);

	public final static long MAXIMUM_LONG_VALUE = 0x7FFFFFFFFFFFFFFFL;

	private final RAtomicLong redisson;

	/** 步伐 */
	private final long step;

	/** 序列 */
	private long sequence;

	/** 限制 */
	private long limit;

	/** 标识定义 */
	private final IdentityDefinition definition;

	/** 分区 */
	private final int partition;

	public RedisIdentityFactory(RAtomicLong redisson, long step, int partition, int sequenceBit) {
		this.redisson = redisson;
		this.step = step;
		int partitionBit = IdentityDefinition.DATA_BIT - sequenceBit;
		LinkedHashMap<String, Integer> sections = new LinkedHashMap<>();
		sections.put("partition", partitionBit);
		sections.put("sequence", sequenceBit);
		this.definition = new IdentityDefinition(sections);
		this.partition = partition;
		Long maximum = definition.make(partition, -1L);
		Long minimum = definition.make(partition, 0L);

		this.limit = redisson.addAndGet(step);
		this.sequence = limit - step;
		Long current = definition.make(partition, sequence);
		if (current < minimum || current > maximum) {
			String message = String.format("序列异常,边界范围[{}, {}],当前值{}", minimum, maximum, current);
			LOGGER.error(message);
			new RuntimeException(message);
		}
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
			limit = redisson.addAndGet(step);
			sequence = limit - step;
		}
		Long current = definition.make(partition, sequence++);
		return current;
	}

}
