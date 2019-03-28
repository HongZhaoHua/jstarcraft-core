package com.jstarcraft.core.utility.instant;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.BitSet;

/**
 * 阴历表达式
 * 
 * @author Birdy
 *
 */
public class LunarExpression extends DateTimeExpression {

	/** 秒位图 */
	private final BitSet seconds;

	/** 分位图 */
	private final BitSet minutes;

	/** 时位图 */
	private final BitSet hours;

	/** 日位图 */
	private final BitSet days;

	/** 月位图 */
	private final BitSet months;

	/** 年位图 */
	private final BitSet years;

	public LunarExpression(String expression) {
		super(expression);

		this.seconds = new BitSet(60);
		this.minutes = new BitSet(60);
		this.hours = new BitSet(24);

		this.days = new BitSet(31);
		this.months = new BitSet(13);
		this.years = new BitSet(200);

		String[] fields = expression.split(" ");
		if (fields.length != 5 && fields.length != 6) {
			throw new IllegalArgumentException();
		} else {
			this.setBits(this.seconds, fields[0], 0, 60, 0);
			this.setBits(this.minutes, fields[1], 0, 60, 0);
			this.setBits(this.hours, fields[2], 0, 24, 0);
			this.setBits(this.days, fields[3], 1, 31, 0);
			this.setBits(this.months, fields[4], 1, 13, 0);
			if (fields.length == 6) {
				this.setBits(this.years, fields[5], 1900, 2100, 1900);
			} else {
				this.setBits(this.years, "*", 1900, 2100, 1900);
			}
		}
	}

	private void setBits(BitSet bits, String value, int from, int to, int shift) {
		if (value.contains("?")) {
			value = "*";
		}
		String[] fields = value.split(",");
		for (String field : fields) {
			if (!field.contains("/")) {
				// Not an incrementer so it must be a range (possibly empty)
				int[] range = getRange(field, from, to, shift);
				bits.set(range[0], range[1] + 1);
			} else {
				String[] split = field.split("/");
				if (split.length > 2) {
					throw new IllegalArgumentException("Incrementer has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
				}
				int[] range = getRange(split[0], from, to, shift);
				if (!split[0].contains("-")) {
					range[1] = to - 1;
				}
				int skip = Integer.parseInt(split[1]);
				if (skip <= 0) {
					throw new IllegalArgumentException("Incrementer delta must be 1 or higher: '" + field + "' in expression \"" + this.expression + "\"");
				}
				for (int index = range[0]; index <= range[1]; index += skip) {
					bits.set(index);
				}
			}
		}
	}

	private int[] getRange(String field, int from, int to, int shift) {
		int[] range = new int[2];
		if (field.contains("*")) {
			range[0] = from;
			range[1] = to - 1;
		} else {
			if (!field.contains("-")) {
				range[0] = range[1] = Integer.valueOf(field);
			} else {
				String[] split = field.split("-");
				if (split.length > 2) {
					throw new IllegalArgumentException("Range has more than two fields: '" + field + "' in expression \"" + this.expression + "\"");
				}
				range[0] = Integer.valueOf(split[0]);
				range[1] = Integer.valueOf(split[1]);
			}
			if (range[0] >= to || range[1] >= to) {
				throw new IllegalArgumentException("Range exceeds maximum (" + to + "): '" + field + "' in expression \"" + this.expression + "\"");
			}
			if (range[0] < from || range[1] < from) {
				throw new IllegalArgumentException("Range less than minimum (" + from + "): '" + field + "' in expression \"" + this.expression + "\"");
			}
			if (range[0] > range[1]) {
				throw new IllegalArgumentException("Invalid inverted range: '" + field + "' in expression \"" + this.expression + "\"");
			}
		}

		range[0] = range[0] - shift;
		range[1] = range[1] - shift;
		return range;
	}

	public BitSet getSeconds() {
		return seconds;
	}

	public BitSet getMinutes() {
		return minutes;
	}

	public BitSet getHours() {
		return hours;
	}

	public BitSet getDays() {
		return days;
	}

	public BitSet getMonths() {
		return months;
	}

	public BitSet getYears() {
		return years;
	}

	@Override
	public ZonedDateTime getPreviousDateTime(ZonedDateTime nowDateTime) {
		LunarDate lunar = new LunarDate(nowDateTime.toLocalDate());
		int year = lunar.getYear();
		boolean leap = lunar.isLeap();
		int month = lunar.getMonth();
		int day = lunar.getDay();
		int size = LunarDate.getDaySize(year, leap, month);
		LocalTime time = nowDateTime.toLocalTime();
		int hour = time.getHour();
		int minute = time.getMinute();
		int second = time.getSecond();
		second = seconds.previousSetBit(second - 1);
		if (second == -1) {
			second = seconds.previousSetBit(59);
			minute--;
		}
		minute = minutes.previousSetBit(minute);
		if (minute == -1) {
			second = seconds.previousSetBit(59);
			minute = minutes.previousSetBit(59);
			hour--;
		}
		hour = hours.previousSetBit(hour);
		if (hour == -1) {
			second = seconds.previousSetBit(59);
			minute = minutes.previousSetBit(59);
			hour = hours.previousSetBit(23);
			day--;
		}
		day++;
		do {
			day--;
			day = days.previousSetBit(day);
			if (day == -1 || day > size) {
				second = seconds.previousSetBit(59);
				minute = minutes.previousSetBit(59);
				hour = hours.previousSetBit(23);
				day = days.previousSetBit(30);
				// 从是闰月到非闰月
				if (leap && month == LunarDate.getLeapMonth(year)) {
					leap = false;
				} else {
					month--;
					// 从非闰月到是闰月
					if (month == LunarDate.getLeapMonth(year)) {
						leap = true;
					}
				}
			}
			// 月份是否变化
			if (!months.get(month)) {
				month = months.previousSetBit(month);
				if (month == -1) {
					second = seconds.previousSetBit(59);
					minute = minutes.previousSetBit(59);
					hour = hours.previousSetBit(23);
					day = days.previousSetBit(30);
					month = months.previousSetBit(12);
					year--;
					year = years.previousSetBit(year - LunarDate.MINIMUM_YEAR);
					if (year == -1) {
						throw new IllegalArgumentException();
					}
					year += LunarDate.MINIMUM_YEAR;
				}
				// 可能是闰月
				leap = month == LunarDate.getLeapMonth(year);
			}
			size = LunarDate.getDaySize(year, leap, month);
		} while (day > size);
		lunar = new LunarDate(year, leap, month, day);
		SolarDate solar = lunar.getSolar();
		return ZonedDateTime.of(solar.getDate(), LocalTime.of(hour, minute, second), nowDateTime.getZone());
	}

	@Override
	public ZonedDateTime getNextDateTime(ZonedDateTime nowDateTime) {
		LunarDate lunar = new LunarDate(nowDateTime.toLocalDate());
		int year = lunar.getYear();
		boolean leap = lunar.isLeap();
		int month = lunar.getMonth();
		int day = lunar.getDay();
		int size = LunarDate.getDaySize(year, leap, month);
		LocalTime time = nowDateTime.toLocalTime();
		int hour = time.getHour();
		int minute = time.getMinute();
		int second = time.getSecond();
		second = seconds.nextSetBit(second + 1);
		if (second == -1) {
			second = seconds.nextSetBit(0);
			minute++;
		}
		minute = minutes.nextSetBit(minute);
		if (minute == -1) {
			second = seconds.nextSetBit(0);
			minute = minutes.nextSetBit(0);
			hour++;
		}
		hour = hours.nextSetBit(hour);
		if (hour == -1) {
			second = seconds.nextSetBit(0);
			minute = minutes.nextSetBit(0);
			hour = hours.nextSetBit(0);
			day++;
		}
		day--;
		do {
			day++;
			day = days.nextSetBit(day);
			if (day == -1 || day > size) {
				second = seconds.nextSetBit(0);
				minute = minutes.nextSetBit(0);
				hour = hours.nextSetBit(0);
				day = days.nextSetBit(1);
				// 从非闰月到是闰月
				if (!leap && month == LunarDate.getLeapMonth(year)) {
					leap = true;
				} else {
					month++;
					leap = false;
				}
			}
			// 月份是否变化
			if (!months.get(month)) {
				month = months.nextSetBit(month);
				if (month == -1) {
					second = seconds.nextSetBit(0);
					minute = minutes.nextSetBit(0);
					hour = hours.nextSetBit(0);
					day = days.nextSetBit(1);
					month = months.nextSetBit(1);
					year++;
				}
				year = years.nextSetBit(year - LunarDate.MINIMUM_YEAR);
				if (year == -1) {
					throw new IllegalArgumentException();
				}
				year += LunarDate.MINIMUM_YEAR;
				// 一定非闰月
				leap = false;
			}
			size = LunarDate.getDaySize(year, leap, month);
		} while (day > size);
		lunar = new LunarDate(year, leap, month, day);
		SolarDate solar = lunar.getSolar();
		return ZonedDateTime.of(solar.getDate(), LocalTime.of(hour, minute, second), nowDateTime.getZone());
	}

}
