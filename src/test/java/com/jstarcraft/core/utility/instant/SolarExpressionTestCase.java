package com.jstarcraft.core.utility.instant;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

public class SolarExpressionTestCase {

	private List<LocalDateTime> dateTimes = new ArrayList<>();
	{
		dateTimes.add(LocalDateTime.of(2020, 1, 1, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 1, 30, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 2, 1, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 3, 1, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 3, 30, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 4, 1, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 4, 30, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 5, 1, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 5, 30, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 6, 1, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 6, 30, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 7, 1, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 7, 30, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 8, 1, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 8, 30, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 9, 1, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 9, 30, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 10, 1, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 10, 30, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 11, 1, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 11, 30, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 12, 1, 12, 0, 0));
		dateTimes.add(LocalDateTime.of(2020, 12, 30, 12, 0, 0));
	}

	@Test
	public void testGetPreviousDateTime() {
		SolarExpression expression = new SolarExpression("0 0 12 1,30 * ?");

		LocalDateTime dateTime = LocalDateTime.of(2021, 1, 1, 0, 0, 0);
		for (int index = dateTimes.size() - 1; index > 0; index--) {
			dateTime = expression.getPreviousDateTime(dateTime);
			Assert.assertEquals(dateTimes.get(index), dateTime);
		}
	}

	@Test
	public void testGetNextDateTime() {
		SolarExpression expression = new SolarExpression("0 0 12 1,30 * ?");

		LocalDateTime dateTime = LocalDateTime.of(2020, 1, 1, 0, 0, 0);
		for (int index = 0, size = dateTimes.size(); index < size; index++) {
			dateTime = expression.getNextDateTime(dateTime);
			Assert.assertEquals(dateTimes.get(index), dateTime);
		}
	}

}
