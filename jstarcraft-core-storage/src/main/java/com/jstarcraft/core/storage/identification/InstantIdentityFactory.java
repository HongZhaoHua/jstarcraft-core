package com.jstarcraft.core.storage.identification;

import java.time.Instant;
import java.util.List;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 基于时间标识管理器
 * 
 * <pre>
 * SnowFlake的结构如下(每部分用-分开):
 * 0 - 0000000000 0000000000 0000000000 0000000000 0 - 00000 - 00000 - 000000000000 
 * 1位标识,由于long基本类型在Java中是带符号的,最高位是符号位,正数是0,负数是1,所以id一般是正数,最高位是0
 * 41位时间截(毫秒级),注意,41位时间截不是存储当前时间的时间截,而是存储时间截的差值(当前时间截 - 开始时间截)
 * 41位的时间截,可以使用69年.T = (1L << 41) / (1000L * 60 * 60 * 24 * 365) = 69
 * 10位的数据机器位,可以部署在1024个节点,包括5位centerId和5位workerId
 * 12位序列,毫秒内的计数,12位的计数顺序号支持每个节点每毫秒(同一机器,同一时间截)产生4096个ID序号
 * SnowFlake的优点是,整体上按照时间自增排序,并且整个分布式系统内不会产生ID碰撞(由中心ID和机器ID作区分),并且效率较高,经测试,SnowFlake每秒能够产生26万ID左右.
 * </pre>
 */
public class InstantIdentityFactory implements IdentityFactory {

    /** 标识定义 */
    private final IdentityDefinition definition;

    /** 时间截 */
    private long current = -1L;

    /** 掩码 */
    private final long mask;

    /** 偏移 */
    private final long offset;

    /** 分区 */
    private final int partition;

    /** 序列 */
    private long sequence;

    public InstantIdentityFactory(IdentityDefinition definition, int partition, Instant offsetInstant) {
        List<IdentitySection> sections = definition.getSections();
        assert sections.size() == 3;
        this.definition = definition;
        this.partition = partition;
        int sequenceBit = sections.get(2).getBit();
        this.mask = -1L ^ (-1L << sequenceBit);
        this.offset = offsetInstant.toEpochMilli();
    }

    /**
     * 阻塞到下一个毫秒,直到获得时间戳
     * 
     * @param last
     * @return
     */
    private long waitInstant(long last) {
        long current = System.currentTimeMillis();
        while (current <= last) {
            current = System.currentTimeMillis();
        }
        return current;
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
        long now = System.currentTimeMillis();
        // 如果当前时间戳小于最近时间戳,则说明系统时钟倒退.应当抛出异常
        if (now < current) {
            throw new RuntimeException(StringUtility.format("序列异常,时钟倒退{}毫秒.", current - now));
        }
        // 如果当前时间戳与最近时间戳相等,则使用序列防止冲突.
        if (current == now) {
            sequence = (sequence + 1) & mask;
            if (sequence == 0) {
                // 阻塞到下一个时间戳
                current = waitInstant(current);
            }
        } else {
            // 时间戳改变,重置序列
            sequence = 0L;
            current = now;
        }
        return definition.make(partition, current - offset, sequence);
    }

    public long getOffset() {
        return offset;
    }

}
