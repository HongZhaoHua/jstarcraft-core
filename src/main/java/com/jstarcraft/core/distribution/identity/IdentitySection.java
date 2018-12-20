package com.jstarcraft.core.distribution.identity;

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

	/** 名称 */
	private final String name;

	IdentitySection(int bit, long mask, String name) {
		this.bit = bit;
		this.mask = mask;
		this.name = name;
	}

	public int getBit() {
		return bit;
	}

	public long getMask() {
		return mask;
	}

	public String getName() {
		return name;
	}

}
