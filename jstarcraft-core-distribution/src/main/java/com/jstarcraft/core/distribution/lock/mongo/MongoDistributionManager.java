package com.jstarcraft.core.distribution.lock.mongo;

import java.time.Instant;

import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.jstarcraft.core.distribution.exception.DistributionLockException;
import com.jstarcraft.core.distribution.exception.DistributionUnlockException;
import com.jstarcraft.core.distribution.lock.DistributionDefinition;
import com.jstarcraft.core.distribution.lock.DistributionManager;
import com.jstarcraft.core.orm.mongo.MongoAccessor;
import com.jstarcraft.core.orm.mongo.MongoMetadata;

/**
 * Mongo分布式管理器
 * 
 * @author Birdy
 *
 */
public class MongoDistributionManager extends DistributionManager {

	private MongoAccessor accessor;

	public MongoDistributionManager(MongoAccessor accessor) {
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
				MongoDistributionDefinition definition = new MongoDistributionDefinition(name, now);
				accessor.create(MongoDistributionDefinition.class, definition);
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
				accessor.delete(MongoDistributionDefinition.class, name);
				count++;
			} catch (Exception exception) {
			}
		}
		return count;
	}

	@Override
	protected void lock(DistributionDefinition definition) {
		Instant now = Instant.now();
		Criteria criteria = Criteria.where(MongoMetadata.mongoId).is(definition.getName());
		Criteria[] andCriterias = new Criteria[1];
		andCriterias[0] = Criteria.where("most").lte(now.toEpochMilli());
		Query query = Query.query(criteria.andOperator(andCriterias));
		Update update = new Update();
		update.set("most", definition.getMost().toEpochMilli());
		long count = accessor.update(MongoDistributionDefinition.class, query, update);
		if (count != 1) {
			throw new DistributionLockException();
		}
	}

	@Override
	protected void unlock(DistributionDefinition definition) {
		Instant now = Instant.now();
		Criteria criteria = Criteria.where(MongoMetadata.mongoId).is(definition.getName());
		Criteria[] andCriterias = new Criteria[2];
		andCriterias[0] = Criteria.where("most").is(definition.getMost().toEpochMilli());
		andCriterias[1] = Criteria.where("most").gt(now.toEpochMilli());
		Query query = Query.query(criteria.andOperator(andCriterias));
		Update update = new Update();
		update.set("most", now.toEpochMilli());
		long count = accessor.update(MongoDistributionDefinition.class, query, update);
		if (count != 1) {
			throw new DistributionUnlockException();
		}
	}

}
