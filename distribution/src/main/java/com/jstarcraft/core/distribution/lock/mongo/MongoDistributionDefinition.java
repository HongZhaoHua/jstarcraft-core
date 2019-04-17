package com.jstarcraft.core.distribution.lock.mongo;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.jstarcraft.core.distribution.lock.DistributionDefinition;
import com.jstarcraft.core.utility.IdentityObject;

/**
 * Mongo分布式定义
 * 
 * @author Birdy
 *
 */
@Document
public class MongoDistributionDefinition implements IdentityObject<String> {

	/** 锁名称 */
	@Id
	private String name;

	/** 最多锁定到指定的时间(必选) */
	private long most;

	MongoDistributionDefinition() {
	}

	public MongoDistributionDefinition(DistributionDefinition definition) {
		this(definition.getName(), definition.getMost());
	}

	public MongoDistributionDefinition(String name, Instant most) {
		this.name = name;
		this.most = most.toEpochMilli();
	}

	@Override
	public String getId() {
		return name;
	}

	public String getName() {
		return name;
	}

	public long getMost() {
		return most;
	}

}
