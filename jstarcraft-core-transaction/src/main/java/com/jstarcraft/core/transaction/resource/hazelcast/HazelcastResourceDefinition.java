package com.jstarcraft.core.transaction.resource.hazelcast;

import java.io.Serializable;
import java.time.Instant;

import com.jstarcraft.core.transaction.resource.ResourceDefinition;

/**
 * Hazelcast分布式定义
 * 
 * @author Birdy
 *
 */
public class HazelcastResourceDefinition implements Serializable {

	private static final long serialVersionUID = -2615267956144491936L;

	/** 锁名称 */
	private String name;

	/** 最多锁定到指定的时间(必选) */
	private Instant most;

	HazelcastResourceDefinition() {
	}

	public HazelcastResourceDefinition(ResourceDefinition definition) {
		this(definition.getName(), definition.getMost());
	}

	public HazelcastResourceDefinition(String name, Instant most) {
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
