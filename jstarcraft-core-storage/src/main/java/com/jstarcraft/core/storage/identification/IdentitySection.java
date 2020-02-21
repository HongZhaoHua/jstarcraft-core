package com.jstarcraft.core.storage.identification;

/**
 * 标识分段
 * 
 * @author Birdy
 *
 */
public class IdentitySection {

    /** 位数 */
    private final int bit;

    /** 掩码 */
    private final long mask;

    IdentitySection(int bit, long mask) {
        this.bit = bit;
        this.mask = mask;
    }

    public int getBit() {
        return bit;
    }

    public long getMask() {
        return mask;
    }

}
