package com.jstarcraft.core.distribution.resource.mongo;

import java.time.Instant;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.jstarcraft.core.distribution.exception.DistributionLockException;
import com.jstarcraft.core.distribution.exception.DistributionUnlockException;
import com.jstarcraft.core.distribution.resource.ResourceDefinition;
import com.jstarcraft.core.distribution.resource.ResourceManager;
import com.jstarcraft.core.orm.mongo.MongoAccessor;
import com.jstarcraft.core.orm.mongo.MongoMetadata;

/**
 * Mongo分布式管理器
 * 
 * @author Birdy
 *
 */
public class MongoResourceManager extends ResourceManager {

	private MongoAccessor accessor;

	public MongoResourceManager(MongoAccessor accessor) {
		this.accessor = accessor;
	}

	/**
	 * 尝试创建指定的锁
	 * 
	 * @param names
	 * @return
	 */
	public int create(String... names) {
		int count = 0;
		Instant now = Instant.now();
		for (String name : names) {
			try {
				MongoResourceDefinition definition = new MongoResourceDefinition(name, now);
				accessor.create(MongoResourceDefinition.class, definition);
				count++;
			} catch (Exception exception) {
			}
		}
		return count;
	}

	/**
	 * 尝试删除指定的锁
	 * 
	 * @param names
	 * @return
	 */
	public int delete(String... names) {
		int count = 0;
		for (String name : names) {
			try {
				accessor.delete(MongoResourceDefinition.class, name);
				count++;
			} catch (Exception exception) {
			}
		}
		return count;
	}

	@Override
	protected void lock(ResourceDefinition definition) {
		Instant now = Instant.now();
		Criteria criteria = Criteria.where(MongoMetadata.mongoId).is(definition.getName());
		Criteria[] andCriterias = new Criteria[1];
		andCriterias[0] = Criteria.where("most").lte(now.toEpochMilli());
		Query query = Query.query(criteria.andOperator(andCriterias));
		Update update = new Update();
		update.set("most", definition.getMost().toEpochMilli());
		long count = accessor.update(MongoResourceDefinition.class, query, update);
		if (count != 1) {
			throw new DistributionLockException();
		}
	}

	@Override
	protected void unlock(ResourceDefinition definition) {
		Instant now = Instant.now();
		Criteria criteria = Criteria.where(MongoMetadata.mongoId).is(definition.getName());
		Criteria[] andCriterias = new Criteria[2];
		andCriterias[0] = Criteria.where("most").is(definition.getMost().toEpochMilli());
		andCriterias[1] = Criteria.where("most").gt(now.toEpochMilli());
		Query query = Query.query(criteria.andOperator(andCriterias));
		Update update = new Update();
		update.set("most", now.toEpochMilli());
		long count = accessor.update(MongoResourceDefinition.class, query, update);
		if (count != 1) {
			throw new DistributionUnlockException();
		}
	}

}
