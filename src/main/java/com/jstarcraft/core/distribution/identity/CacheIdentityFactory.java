package com.jstarcraft.core.distribution.identity;

import java.util.LinkedHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jstarcraft.core.cache.CacheObject;
import com.jstarcraft.core.orm.OrmAccessor;

/**
 * 基于分区标识管理器
 * 
 * <pre>
 * 主键64位[分区段(1 - 32)][应用段(使用增量策略)]
 * </pre>
 * 
 * <pre>
 * 分区段需要考虑兼容其它编程语言不支持64位的情况
 * </pre>
 * 
 * @author Birdy
 */
public class CacheIdentityFactory implements IdentityFactory {

	private static final Logger LOGGER = LoggerFactory.getLogger(CacheIdentityFactory.class);

	public final static long MAXIMUM_LONG_VALUE = 0x7FFFFFFFFFFFFFFFL;

	/** 访问器 */
	private final OrmAccessor accessor;

	/** 类型 */
	private final Class<? extends CacheObject<Long>> clazz;

	/** 标识定义 */
	private final IdentityDefinition definition;

	/** 分区 */
	private final int partition;

	/** 序列 */
	private AtomicLong sequence;

	public CacheIdentityFactory(OrmAccessor accessor, Class<? extends CacheObject<Long>> clazz, int partition, int sequenceBit) {
		this.accessor = accessor;
		this.clazz = clazz;
		int partitionBit = IdentityDefinition.DATA_BIT - sequenceBit;
		LinkedHashMap<String, Integer> sections = new LinkedHashMap<>();
		sections.put("partition", partitionBit);
		sections.put("sequence", sequenceBit);
		this.definition = new IdentityDefinition(sections);
		this.partition = partition;
		Long maximum = definition.make(partition, -1L);
		Long minimum = definition.make(partition, 0L);
		Long current = accessor.maximumIdentity(clazz, minimum, maximum);
		this.sequence = new AtomicLong(current == null ? minimum : current);
	}

	public Class<? extends CacheObject<Long>> getClazz() {
		return clazz;
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
	public long getSequence() {
		return sequence.incrementAndGet();
	}

}
