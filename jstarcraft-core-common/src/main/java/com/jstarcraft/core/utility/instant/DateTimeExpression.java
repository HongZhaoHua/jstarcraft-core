package com.jstarcraft.core.utility.instant;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

/**
 * 日期时间表达式
 * 
 * @author Birdy
 *
 */
abstract public class DateTimeExpression {

	/** 表达式 */
	protected final String expression;

	protected DateTimeExpression(String expression) {
		this.expression = expression;
	}

	public String getExpression() {
		return expression;
	}

	/**
	 * 根据指定日期时间获取上一次日期时间
	 * 
	 * @param dateTime
	 * @return
	 */
	abstract public ZonedDateTime getPreviousDateTime(ZonedDateTime dateTime);

	/**
	 * 根据指定日期时间获取下一次日期时间
	 * 
	 * @param dateTime
	 * @return
	 */
	abstract public ZonedDateTime getNextDateTime(ZonedDateTime dateTime);

	/**
	 * 根据指定日期时间获取上一次日期时间
	 * 
	 * @param dateTime
	 * @return
	 */
	public LocalDateTime getPreviousDateTime(LocalDateTime dateTime) {
		ZonedDateTime instant = getPreviousDateTime(ZonedDateTime.of(dateTime, ZoneOffset.UTC));
		return instant == null ? null : instant.toLocalDateTime();
	}

	/**
	 * 根据指定日期时间获取下一次日期时间
	 * 
	 * @param dateTime
	 * @return
	 */
	public LocalDateTime getNextDateTime(LocalDateTime dateTime) {
		ZonedDateTime instant = getNextDateTime(ZonedDateTime.of(dateTime, ZoneOffset.UTC));
		return instant == null ? null : instant.toLocalDateTime();
	}
	
	/**
	 * 根据指定日期时间获取上一次日期时间
	 * 
	 * @param dateTime
	 * @return
	 */
	public Instant getPreviousDateTime(Instant dateTime) {
		ZonedDateTime instant = getPreviousDateTime(ZonedDateTime.ofInstant(dateTime, ZoneOffset.UTC));
		return instant == null ? null : instant.toInstant();
	}

	/**
	 * 根据指定日期时间获取下一次日期时间
	 * 
	 * @param dateTime
	 * @return
	 */
	public Instant getNextDateTime(Instant dateTime) {
		ZonedDateTime instant = getNextDateTime(ZonedDateTime.ofInstant(dateTime, ZoneOffset.UTC));
		return instant == null ? null : instant.toInstant();
	}

	@Override
	public String toString() {
		return "DateTimeExpression [expression=" + expression + "]";
	}

}
