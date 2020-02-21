package com.jstarcraft.core.storage.identification;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

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

    public final static long MAXIMUM_LONG_VALUE = 0x7FFFFFFFFFFFFFFFL;

    /** 标识定义 */
    private final IdentityDefinition definition;

    /** 分区 */
    private final int partition;

    /** 序列 */
    private AtomicLong sequence;

    public CacheIdentityFactory(IdentityDefinition definition, int partition, Long current) {
        List<IdentitySection> sections = definition.getSections();
        assert sections.size() == 2;
        this.definition = definition;
        this.partition = partition;
        long maximum = definition.make(partition, -1L);
        long minimum = definition.make(partition, 0L);
        if (current != null) {
            assert current >= minimum && current < maximum;
        }
        this.sequence = new AtomicLong(current == null ? minimum : current);
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
        return sequence.getAndIncrement();
    }

}
