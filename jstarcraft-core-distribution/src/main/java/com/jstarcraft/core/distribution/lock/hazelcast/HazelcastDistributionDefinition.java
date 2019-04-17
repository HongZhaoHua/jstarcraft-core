package com.jstarcraft.core.distribution.lock.hazelcast;

import java.io.Serializable;
import java.time.Instant;

import com.jstarcraft.core.distribution.lock.DistributionDefinition;

/**
 * Hazelcast分布式定义
 * 
 * @author Birdy
 *
 */
public class HazelcastDistributionDefinition implements Serializable {

	private static final long serialVersionUID = -2615267956144491936L;

	/** 锁名称 */
	private String name;

	/** 最多锁定到指定的时间(必选) */
	private Instant most;

	HazelcastDistributionDefinition() {
	}

	public HazelcastDistributionDefinition(DistributionDefinition definition) {
		this(definition.getName(), definition.getMost());
	}

	public HazelcastDistributionDefinition(String name, Instant most) {
		this.name = name;
		this.most = most;
	}

	public String getName() {
		return name;
	}

	public Instant getMost() {
		return most;
	}

}
