package com.jstarcraft.core.utility;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.lang3.time.DateUtils;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;

/**
 * 时间工具
 */
public class InstantUtility extends DateUtils {

	private static final CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);

	private static final CronParser parser = new CronParser(cronDefinition);

	private static final ConcurrentHashMap<String, Cron> caches = new ConcurrentHashMap<>();

	private static Cron getCronExpression(String cron) {
		synchronized (caches) {
			Cron expression = caches.get(cron);
			if (expression == null) {
				try {
					expression = parser.parse(cron);
				} catch (Exception exception) {
					throw new IllegalArgumentException(exception);
				}
				caches.put(cron, expression);
			}
			return expression;
		}
	}

	/**
	 * 获取下次执行时间
	 * 
	 * @param cron
	 * @param instant
	 * @return
	 */
	public static Instant getInstantAfter(String cron, Instant instant) {
		Cron expression = getCronExpression(cron);
		ExecutionTime executionTime = ExecutionTime.forCron(expression);
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
		dateTime = executionTime.nextExecution(dateTime);
		return dateTime.toInstant();
	}

	/**
	 * 获取上次执行时间
	 * 
	 * @param cron
	 * @param instant
	 * @return
	 */
	public static Instant getInstantBefore(String cron, Instant instant) {
		Cron expression = getCronExpression(cron);
		ExecutionTime executionTime = ExecutionTime.forCron(expression);
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, ZoneId.systemDefault());
		dateTime = executionTime.lastExecution(dateTime);
		return dateTime.toInstant();
	}

}
