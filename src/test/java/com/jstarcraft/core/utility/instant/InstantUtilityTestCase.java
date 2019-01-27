package com.jstarcraft.core.utility.instant;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.utility.InstantUtility;

public class InstantUtilityTestCase {
	
	@Test
	public void testDateTime() {
		String cron = "0 15 10 ? * *";
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);

		LocalTime offset = LocalTime.of(10, 15, 0);
		LocalTime zero = LocalTime.of(0, 0, 0);

		LocalDateTime dateTime = LocalDateTime.of(today, zero);
		LocalDateTime before = InstantUtility.getDateTimeBefore(cron, dateTime, ZoneId.systemDefault());
		LocalDateTime after = InstantUtility.getDateTimeAfter(cron, dateTime, ZoneId.systemDefault());

		dateTime = LocalDateTime.of(yesterday, offset);
		Assert.assertThat(before, CoreMatchers.equalTo(dateTime));

		dateTime = LocalDateTime.of(today, offset);
		Assert.assertThat(after, CoreMatchers.equalTo(dateTime));
	}

	@Test
	public void testInstant() {
		String cron = "0 15 10 ? * *";
		LocalDate today = LocalDate.now();
		LocalDate yesterday = today.minusDays(1);

		LocalTime offset = LocalTime.of(10, 15, 0);
		LocalTime zero = LocalTime.of(0, 0, 0);

		ZonedDateTime dateTime = ZonedDateTime.of(today, zero, ZoneId.systemDefault());
		Instant instant = Instant.from(dateTime);
		Instant before = InstantUtility.getInstantBefore(cron, instant, ZoneId.systemDefault());
		Instant after = InstantUtility.getInstantAfter(cron, instant, ZoneId.systemDefault());

		dateTime = ZonedDateTime.of(yesterday, offset, ZoneId.systemDefault());
		Assert.assertThat(before, CoreMatchers.equalTo(Instant.from(dateTime)));

		dateTime = ZonedDateTime.of(today, offset, ZoneId.systemDefault());
		Assert.assertThat(after, CoreMatchers.equalTo(Instant.from(dateTime)));
	}
	
	

}
