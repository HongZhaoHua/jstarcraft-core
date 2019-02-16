package com.jstarcraft.core.utility;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.BitSet;

import org.apache.commons.lang3.time.DateUtils;

import com.cronutils.model.Cron;
import com.cronutils.model.CronType;
import com.cronutils.model.definition.CronDefinition;
import com.cronutils.model.definition.CronDefinitionBuilder;
import com.cronutils.model.time.ExecutionTime;
import com.cronutils.parser.CronParser;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap.Builder;

/**
 * 时间工具
 */
public class InstantUtility extends DateUtils {

	private static final CronDefinition cronDefinition = CronDefinitionBuilder.instanceDefinitionFor(CronType.QUARTZ);

	private static final CronParser parser = new CronParser(cronDefinition);

	private static final ConcurrentLinkedHashMap<String, Cron> crons;
	
	private static final ConcurrentLinkedHashMap<String, BitSet[]> bits; 

	static {
		{
			Builder<String, Cron> builder = new Builder<>();
			builder.initialCapacity(1000);
			builder.maximumWeightedCapacity(1000);
			builder.concurrencyLevel(Runtime.getRuntime().availableProcessors());
			crons = builder.build();
		}
		{
			Builder<String, BitSet[]> builder = new Builder<>();
			builder.initialCapacity(1000);
			builder.maximumWeightedCapacity(1000);
			builder.concurrencyLevel(Runtime.getRuntime().availableProcessors());
			bits = builder.build();
		}
	}

	private static Cron getCronExpression(String cron) {
		Cron expression = crons.get(cron);
		if (expression == null) {
			synchronized (crons) {
				expression = crons.get(cron);
				if (expression == null) {
					expression = parser.parse(cron);
					crons.put(cron, expression);
				}

			}
		}
		return expression;
	}

	/**
	 * 获取下次执行时间
	 * 
	 * @param cron
	 * @param instant
	 * @return
	 */
	public static LocalDateTime getDateTimeAfter(String cron, LocalDateTime instant) {
		return getDateTimeAfter(cron, instant, ZoneId.systemDefault());
	}

	/**
	 * 获取下次执行时间
	 * 
	 * @param cron
	 * @param instant
	 * @param zone
	 * @return
	 */
	public static LocalDateTime getDateTimeAfter(String cron, LocalDateTime instant, ZoneId zone) {
		Cron expression = getCronExpression(cron);
		ExecutionTime executionTime = ExecutionTime.forCron(expression);
		ZonedDateTime dateTime = ZonedDateTime.of(instant, zone);
		dateTime = executionTime.nextExecution(dateTime);
		return dateTime.toLocalDateTime();
	}

	/**
	 * 获取上次执行时间
	 * 
	 * @param cron
	 * @param instant
	 * @return
	 */
	public static LocalDateTime getDateTimeBefore(String cron, LocalDateTime instant) {
		return getDateTimeBefore(cron, instant, ZoneId.systemDefault());
	}

	/**
	 * 获取上次执行时间
	 * 
	 * @param cron
	 * @param instant
	 * @return
	 */
	public static LocalDateTime getDateTimeBefore(String cron, LocalDateTime instant, ZoneId zone) {
		Cron expression = getCronExpression(cron);
		ExecutionTime executionTime = ExecutionTime.forCron(expression);
		ZonedDateTime dateTime = ZonedDateTime.of(instant, zone);
		dateTime = executionTime.lastExecution(dateTime);
		return dateTime.toLocalDateTime();
	}

	/**
	 * 获取下次执行时间
	 * 
	 * @param cron
	 * @param instant
	 * @return
	 */
	public static Instant getInstantAfter(String cron, Instant instant) {
		return getInstantAfter(cron, instant, ZoneId.systemDefault());
	}

	/**
	 * 获取下次执行时间
	 * 
	 * @param cron
	 * @param instant
	 * @param zone
	 * @return
	 */
	public static Instant getInstantAfter(String cron, Instant instant, ZoneId zone) {
		Cron expression = getCronExpression(cron);
		ExecutionTime executionTime = ExecutionTime.forCron(expression);
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, zone);
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
		return getInstantBefore(cron, instant, ZoneId.systemDefault());
	}

	/**
	 * 获取上次执行时间
	 * 
	 * @param cron
	 * @param instant
	 * @return
	 */
	public static Instant getInstantBefore(String cron, Instant instant, ZoneId zone) {
		Cron expression = getCronExpression(cron);
		ExecutionTime executionTime = ExecutionTime.forCron(expression);
		ZonedDateTime dateTime = ZonedDateTime.ofInstant(instant, zone);
		dateTime = executionTime.lastExecution(dateTime);
		return dateTime.toInstant();
	}

}
