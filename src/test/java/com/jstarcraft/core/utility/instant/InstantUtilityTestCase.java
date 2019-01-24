package com.jstarcraft.core.utility.instant;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import com.jstarcraft.core.utility.InstantUtility;

public class InstantUtilityTestCase {

	@Test
	public void testCron() {
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
