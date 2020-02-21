package com.jstarcraft.core.storage.identification;

import java.util.Arrays;
import java.util.List;

/**
 * 标识定义
 * 
 * <pre>
 * 根据分段制作与分析标识.
 * </pre>
 * 
 * @author Birdy
 *
 */
public class IdentityDefinition {

    /** 数据位(最高位是符号位,正数是0,负数是1,所以identity数据位是63位) */
    public final static byte DATA_BIT = 63;

    /** 分段定义 */
    private final IdentitySection[] sections;

    public IdentityDefinition(int... sections) {
        int bit = 0;
        int index = 0;
        this.sections = new IdentitySection[sections.length];
        for (int section : sections) {
            if (section <= 0) {
                // TODO 异常
                throw new IllegalArgumentException();
            }
            bit += section;
            if (bit > DATA_BIT) {
                // TODO 异常
                throw new IllegalArgumentException();
            }
            // 制作掩码
            long mask = -1L ^ (-1L << section);
            this.sections[index] = new IdentitySection(section, mask);
            index++;
        }

        if (bit != DATA_BIT) {
            // TODO 异常
            throw new IllegalArgumentException();
        }
    }

    public final long make(long... values) {
        if (values.length != this.sections.length) {
            // TODO 异常
        }
        // 通过位运算得到一个64位的标识
        long identity = 0;
        for (int index = 0; index < this.sections.length; index++) {
            IdentitySection section = this.sections[index];
            long mask = section.getMask();
            long value = values[index];
            value = value & mask;
            identity = identity << section.getBit();
            identity = identity | value;
        }
        return identity;
    }

    public final long[] parse(long identity) {
        // 通过位运算得到对应的信息
        long[] values = new long[this.sections.length];
        for (int index = this.sections.length; index > 0; index--) {
            IdentitySection section = this.sections[index - 1];
            long mask = section.getMask();
            long value = identity & mask;
            identity = identity >> section.getBit();
            values[index - 1] = value;
        }
        return values;
    }

    public final List<IdentitySection> getSections() {
        return Arrays.asList(sections);
    }

}
