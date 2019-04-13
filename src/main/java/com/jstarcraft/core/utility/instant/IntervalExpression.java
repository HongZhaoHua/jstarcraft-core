package com.jstarcraft.core.utility.instant;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

import com.jstarcraft.core.utility.StringUtility;

/**
 * 间隔表达式
 * 
 * @author Birdy
 *
 */
public class IntervalExpression extends DateTimeExpression {

	/** 参考日期时间 */
	private LocalDateTime reference;

	/** 间隔 */
	private int iterval;

	public IntervalExpression(String expression) {
		super(expression);

		String[] fields = expression.split(StringUtility.SPACE);
		if (fields.length != 7) {
			throw new IllegalArgumentException();
		} else {
			int second = Integer.parseInt(fields[0]);
			int minute = Integer.parseInt(fields[1]);
			int hour = Integer.parseInt(fields[2]);
			int day = Integer.parseInt(fields[3]);
			int month = Integer.parseInt(fields[4]);
			int year = Integer.parseInt(fields[5]);
			this.reference = LocalDateTime.of(year, month, day, hour, minute, second);
			this.iterval = Integer.parseInt(fields[6]);
		}
	}

	@Override
	public ZonedDateTime getPreviousDateTime(ZonedDateTime dateTime) {
		int step = Math.abs(iterval);
		if (iterval > 0) {
			// 日期时间必须晚于参考点
			assert !dateTime.isBefore(reference.atZone(dateTime.getZone()));
			long duration = ChronoUnit.SECONDS.between(reference, dateTime.toLocalDateTime());
			long shift = (duration - 1) / step * step;
			return reference.plusSeconds(shift).atZone(dateTime.getZone());
		} else {
			// 日期时间必须早于参考点
			assert !dateTime.isAfter(reference.atZone(dateTime.getZone()));
			long duration = ChronoUnit.SECONDS.between(dateTime.toLocalDateTime(), reference);
			long shift = (duration / step + 1) * step;
			return reference.minusSeconds(shift).atZone(dateTime.getZone());
		}
	}

	@Override
	public ZonedDateTime getNextDateTime(ZonedDateTime dateTime) {
		int step = Math.abs(iterval);
		if (iterval > 0) {
			// 日期时间必须晚于参考点
			assert !dateTime.isBefore(reference.atZone(dateTime.getZone()));
			long duration = ChronoUnit.SECONDS.between(reference, dateTime.toLocalDateTime());
			long shift = (duration / step + 1) * step;
			return reference.plusSeconds(shift).atZone(dateTime.getZone());
		} else {
			// 日期时间必须早于参考点
			assert !dateTime.isAfter(reference.atZone(dateTime.getZone()));
			long duration = ChronoUnit.SECONDS.between(dateTime.toLocalDateTime(), reference);
			long shift = (duration - 1) / step * step;
			return reference.minusSeconds(shift).atZone(dateTime.getZone());
		}
	}

}
